package com.pixelthump.tictacshow.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.pixelthump.tictacshow.repository.model.Team;
import com.pixelthump.tictacshow.repository.model.TicTacShowPlayer;
import com.pixelthump.tictacshow.repository.model.TicTacShowStage;
import com.pixelthump.tictacshow.repository.model.TicTacShowState;

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
public class LobbyCommandProcessor {
	private void processLobbyCommand(Command command, TicTacShowState state) {

		if ("startSesh".equals(command.getType())) processStartSeshCommand(command, state);
		if ("joinTeam".equals(command.getType())) processJoinTeamCommand(command, state);
		if ("makeVip".equals(command.getType())) processMakeVipCommand(command, state);
		if("leaveTeam".equals(command.getType())) processLeaveTeamCommand(command, state);
	}

	private void processStartSeshCommand(Command command, TicTacShowState state) {

		if (!TicTacShowStage.LOBBY.equals(state.getCurrentStage())) throw new RuntimeException();
		if (!isVip(command.getPlayerName(), state)) throw new RuntimeException();
		if (!allPlayersJoinedTeams(state)) throw new RuntimeException();
		state.setCurrentStage(TicTacShowStage.MAIN);
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

	private void processMakeVipCommand(Command command, TicTacShowState state) {

		boolean hasVip = hasVip(state);
		boolean isVip = isVip(command.getPlayerName(), state);

		if (hasVip && !isVip) throw new RuntimeException();
		if (hasVip) removeVip(state);

		String newVipName = command.getBody();
		Optional<TicTacShowPlayer> newVipOptional = state.getPlayers().stream().filter(player -> player.getPlayerId().getPlayerName().equals(newVipName)).map(TicTacShowPlayer.class::cast).findFirst();
		if (newVipOptional.isEmpty()) throw new RuntimeException();
		TicTacShowPlayer newVip = newVipOptional.get();
		newVip.setVip(true);
	}

	private void processLeaveTeamCommand(Command command, TicTacShowState state){

		String PlayerName = command.getPlayerName();
		Optional<TicTacShowPlayer> playerOptional = state.getPlayers().stream().filter(player -> player.getPlayerId().getPlayerName().equals(PlayerName)).map(TicTacShowPlayer.class::cast).findFirst();
		if(playerOptional.isEmpty()) throw new RuntimeException();
		TicTacShowPlayer player = playerOptional.get();
		state.removePlayerFromTeam(player);
	}

	private void removeVip(TicTacShowState state) {

		state.getPlayers().forEach(player -> player.setVip(false));
	}

	private boolean hasVip(TicTacShowState state) {

		return state.getPlayers().stream().anyMatch(Player::getVip);
	}

	private boolean allPlayersJoinedTeams(TicTacShowState state) {

		List<TicTacShowPlayer> playersInTeams = new ArrayList<>();
		playersInTeams.addAll(state.getTeamX().getPlayers());
		playersInTeams.addAll(state.getTeamO().getPlayers());
		return playersInTeams.size() == state.getPlayers().size();
	}

	private boolean teamIsJoinable(String teamNameToJoin, TicTacShowState state) {

		Team team;
		if (teamNameToJoin.equals("X")) team = state.getTeamX();
		else if (teamNameToJoin.equals("O")) team = state.getTeamO();
		else throw new RuntimeException();

		Team opponentTeam;
		if (teamNameToJoin.equals("X")) opponentTeam = state.getTeamO();
		else opponentTeam = state.getTeamX();

		int teamSizeDifference = team.getPlayers().size() - opponentTeam.getPlayers().size();
		return teamSizeDifference == 0;
	}

	private boolean playerIsJoined(String playerName, TicTacShowState state) {

		return state.getPlayers().stream().anyMatch(player -> player.getPlayerId().getPlayerName().equals(playerName));
	}

	private boolean isVip(String playerName, TicTacShowState state) {

		return state.getPlayers().stream().anyMatch(player -> player.getPlayerId().getPlayerName().equals(playerName) && player.getVip().equals(true));
	}
}
