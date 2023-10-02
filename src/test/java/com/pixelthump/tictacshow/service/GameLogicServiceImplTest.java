package com.pixelthump.tictacshow.service;

import com.pixelthump.seshtypelib.repository.CommandRespository;
import com.pixelthump.seshtypelib.repository.model.command.Command;
import com.pixelthump.seshtypelib.service.GameLogicService;
import com.pixelthump.seshtypelib.service.model.State;
import com.pixelthump.seshtypelib.service.model.player.Player;
import com.pixelthump.tictacshow.repository.TicTacShowStateRepository;
import com.pixelthump.tictacshow.repository.model.Team;
import com.pixelthump.tictacshow.repository.model.TicTacShowPlayer;
import com.pixelthump.tictacshow.repository.model.TicTacShowStage;
import com.pixelthump.tictacshow.repository.model.TicTacShowState;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Log4j2
public class GameLogicServiceImpl implements GameLogicService {

	private final CommandProcessor lobbyCommandProcessor;
	private final TicTacShowStateRepository stateRepository;
	private final CommandRespository commandRespository;

	@Autowired
	public GameLogicServiceImpl(LobbyCommandProcessor lobbyCommandProcessor, TicTacShowStateRepository stateRepository,
		CommandRespository commandRespository) {
		this.lobbyCommandProcessor = lobbyCommandProcessor;
		this.stateRepository = stateRepository;
		this.commandRespository = commandRespository;
	}

	@Override
	public State processQueue(String seshCode) {

		TicTacShowState state = stateRepository.findBySeshCode(seshCode);
		List<Command> commands = commandRespository.findByCommandId_State_SeshCodeOrderByCommandId_TimestampAsc(state.getSeshCode());
		List<Command> processedCommands = new ArrayList<>();

		for (Command command : commands) {
			try {
				State changedState = processCommand(command, state);
				processedCommands.add(command);
				changedState.setHasChanged(true);
			} catch (Exception e) {
				processedCommands.add(command);
				log.warn("Unable to process command={}", command);
			}
		}
		commandRespository.deleteAll(processedCommands);
		return changedState;
	}

	private TicTacShowState processCommand(Command command, TicTacShowState state) {

		if (!state.isHostJoined()) {
			throw new RuntimeException();
		}
		if (state.getCurrentStage().equals(TicTacShowStage.LOBBY)) {
			return lobbyCommandProcessor.processCommand(command, state);
		} else if (state.getCurrentStage().equals(TicTacShowStage.MAIN)) {
			return processMainCommand(command, state);
		}
	}

	private void processMainCommand(Command command, TicTacShowState state) {

		throw new UnsupportedOperationException(command.toString() + " " + state.toString());
	}
}
