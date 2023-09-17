package com.pixelthump.tictacshow.service;
import com.pixelthump.seshtypelib.repository.CommandRespository;
import com.pixelthump.seshtypelib.repository.model.command.Command;
import com.pixelthump.seshtypelib.service.GameLogicService;
import com.pixelthump.seshtypelib.service.model.State;
import com.pixelthump.tictacshow.repository.TicTacShowStateRepository;
import com.pixelthump.tictacshow.repository.model.TicTacShowStage;
import com.pixelthump.tictacshow.repository.model.TicTacShowState;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
public class GameLogicServiceImpl implements GameLogicService {

    private final TicTacShowStateRepository stateRepository;
    private final CommandRespository commandRespository;

    @Autowired
    public GameLogicServiceImpl(TicTacShowStateRepository stateRepository, CommandRespository commandRespository) {

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
                processCommand(command, state);
                processedCommands.add(command);
                state.setHasChanged(true);
            } catch (Exception e) {
                processedCommands.add(command);
                log.warn("Unable to process command={}", command);
            }
        }
        commandRespository.deleteAll(processedCommands);
        return state;
    }

    private void processCommand(Command command, TicTacShowState state) {

        if (state.getCurrentStage().equals(TicTacShowStage.LOBBY)) {
            processLobbyCommand(command, state);
        } else if (state.getCurrentStage().equals(TicTacShowStage.MAIN)) {
            processMainCommand(command, state);
        }
    }

    private void processMainCommand(Command command, TicTacShowState state) {

    }

    private void processLobbyCommand(Command command, TicTacShowState state) {

        if ("startSesh".equals(command.getType())) processStartSeshCommand(command, state);
    }

    private void processStartSeshCommand(Command command, TicTacShowState state) {

        boolean playerIsVip = isVip(command.getPlayerName(), state);
        if (!playerIsVip) throw new IllegalStateException();
        if (!TicTacShowStage.LOBBY.equals(state.getCurrentStage())) throw new IllegalStateException();
        state.setCurrentStage(TicTacShowStage.MAIN);
        state.setHasChanged(true);
    }

    private boolean isVip(String playerName, TicTacShowState state) {

        return state.getPlayers().stream().anyMatch(player -> player.getPlayerId().getPlayerName().equals(playerName) && player.getVip().equals(true));
    }
}
