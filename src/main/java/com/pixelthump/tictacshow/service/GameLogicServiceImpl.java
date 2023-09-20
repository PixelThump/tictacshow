package com.pixelthump.tictacshow.service;
import com.pixelthump.seshtypelib.repository.CommandRespository;
import com.pixelthump.seshtypelib.repository.model.command.Command;
import com.pixelthump.seshtypelib.service.GameLogicService;
import com.pixelthump.seshtypelib.service.model.State;
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

        throw new UnsupportedOperationException(command.toString() + " " + state.toString());
    }

    private void processLobbyCommand(Command command, TicTacShowState state) {

        if ("startSesh".equals(command.getType())) processStartSeshCommand(command, state);
        if ("joinTeam".equals(command.getType())) processJoinTeamCommand(command, state);
    }

    private void processStartSeshCommand(Command command, TicTacShowState state) {

        if (!TicTacShowStage.LOBBY.equals(state.getCurrentStage())) throw new RuntimeException();
        if (!isVip(command.getPlayerName(), state)) throw new RuntimeException();
        if (!allPlayersJoinedTeams(state)) throw new RuntimeException();
        state.setCurrentStage(TicTacShowStage.MAIN);
    }

    private boolean allPlayersJoinedTeams(TicTacShowState state) {

        List<TicTacShowPlayer> playersInTeams = new ArrayList<>();
        playersInTeams.addAll(state.getTeamX().getTicTacShowPlayers());
        playersInTeams.addAll(state.getTeamO().getTicTacShowPlayers());
        return playersInTeams.size() == state.getPlayers().size();
    }

    private void processJoinTeamCommand(Command command, TicTacShowState state) {
        String playerName = command.getPlayerName();
        if (!playerIsJoined(playerName, state)) throw new RuntimeException();

        String teamNameToJoin = command.getBody();
        if (!teamIsJoinable(teamNameToJoin, state)) throw new RuntimeException();

        Optional<TicTacShowPlayer> joiningPlayerOptional = state.getPlayers().stream().filter(player -> player.getPlayerId().getPlayerName().equals(playerName)).map(TicTacShowPlayer.class::cast).findFirst();
        if (joiningPlayerOptional.isEmpty()) throw new RuntimeException();

        TicTacShowPlayer joiningPlayer = joiningPlayerOptional.get();
        if (teamNameToJoin.equals("X")) state.getTeamX().addPlayer(joiningPlayer);
        else state.addPlayerToTeam(joiningPlayer, teamNameToJoin);
    }

    private boolean teamIsJoinable(String teamNameToJoin, TicTacShowState state) {

        Team team;
        if (teamNameToJoin.equals("X")) team = state.getTeamX();
        else if (teamNameToJoin.equals("O")) team = state.getTeamO();
        else throw new RuntimeException();

        Team opponentTeam;
        if (teamNameToJoin.equals("X")) opponentTeam = state.getTeamO();
        else opponentTeam = state.getTeamX();

        int teamSizeDifference = team.getTicTacShowPlayers().size() - opponentTeam.getTicTacShowPlayers().size();
        return teamSizeDifference == 0;
    }

    private boolean playerIsJoined(String playerName, TicTacShowState state) {

        return state.getPlayers().stream().anyMatch(player -> player.getPlayerId().getPlayerName().equals(playerName));
    }

    private boolean isVip(String playerName, TicTacShowState state) {

        return state.getPlayers().stream().anyMatch(player -> player.getPlayerId().getPlayerName().equals(playerName) && player.getVip().equals(true));
    }
}