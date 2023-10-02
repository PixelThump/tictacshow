package com.pixelthump.tictacshow.service;
import com.pixelthump.seshtypelib.repository.CommandRespository;
import com.pixelthump.seshtypelib.repository.model.command.Command;
import com.pixelthump.seshtypelib.service.GameLogicService;
import com.pixelthump.seshtypelib.service.model.State;
import com.pixelthump.seshtypelib.service.model.player.Player;
import com.pixelthump.seshtypelib.service.model.player.PlayerId;
import com.pixelthump.tictacshow.Application;
import com.pixelthump.tictacshow.repository.TicTacShowStateRepository;
import com.pixelthump.tictacshow.repository.model.Team;
import com.pixelthump.tictacshow.repository.model.TicTacShowPlayer;
import com.pixelthump.tictacshow.repository.model.TicTacShowStage;
import com.pixelthump.tictacshow.repository.model.TicTacShowState;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = Application.class)
class GameLogicServiceImplTest {

    @MockBean
    TicTacShowStateRepository stateRepository;
    @MockBean
    CommandRespository commandRespository;
    @MockBean
    LobbyCommandProcessor lobbyCommandProcessor;
    @Autowired
    GameLogicService gameLogicService;
    String seshCode = "abcd";

    @Test
    void processQueue_shouldRetrieveStateAndCommandsAndDeleteAllCommandsAfterwards() {

        TicTacShowState state = getLobbyState(seshCode);
        when(stateRepository.findBySeshCode(state.getSeshCode())).thenReturn(state);
        List<Command> commands = new ArrayList<>();
        when(commandRespository.findByCommandId_State_SeshCodeOrderByCommandId_TimestampAsc(state.getSeshCode())).thenReturn(commands);
        gameLogicService.processQueue(state.getSeshCode());
        InOrder inOrder = inOrder(stateRepository, commandRespository);
        inOrder.verify(stateRepository, times(1)).findBySeshCode(state.getSeshCode());
        inOrder.verify(commandRespository, times(1)).findByCommandId_State_SeshCodeOrderByCommandId_TimestampAsc(state.getSeshCode());
        inOrder.verify(commandRespository, times(1)).deleteAll(commands);
    }

    @Test
    void processQueue_shouldCallLobbyCommandProcessor(){

        TicTacShowState state = getLobbyState(seshCode);
        when(stateRepository.findBySeshCode(state.getSeshCode())).thenReturn(state);

        Command lobbyCommand = new Command();
        List<Command> commands = Collections.singletonList(lobbyCommand);
        when(commandRespository.findByCommandId_State_SeshCodeOrderByCommandId_TimestampAsc(state.getSeshCode())).thenReturn(commands);
        when(lobbyCommandProcessor.processCommand(lobbyCommand, state)).thenReturn(state);
        gameLogicService.processQueue(seshCode);
        verify(lobbyCommandProcessor, times(1)).processCommand(lobbyCommand, state);
    }

    @Test
    void processQueue_vipStartSesh_setHasChanged() {

        TicTacShowState state = getLobbyState(seshCode);
        when(stateRepository.findBySeshCode(state.getSeshCode())).thenReturn(state);

        Command lobbyCommand = new Command();
        List<Command> commands = Collections.singletonList(startSeshCommand);
        when(commandRespository.findByCommandId_State_SeshCodeOrderByCommandId_TimestampAsc(state.getSeshCode())).thenReturn(commands);
        when(lobbyCommandProcessor.processCommand(lobbyCommand, state)).thenReturn(state);

        State result = gameLogicService.processQueue(seshCode);

        assertTrue(result.getHasChanged());
    }

    @Test
    void processQueue_vipStartSesh_shouldStartSesh() {

        TicTacShowState state = getLobbyState(seshCode);
        TicTacShowPlayer vip = getVip(state);
        state.getPlayers().add(vip);
        state.getTeamO().addPlayer(vip);
        when(stateRepository.findBySeshCode(state.getSeshCode())).thenReturn(state);

        Command startSeshCommand = getStartSeshCommand(vip);
        List<Command> commands = Collections.singletonList(startSeshCommand);
        when(commandRespository.findByCommandId_State_SeshCodeOrderByCommandId_TimestampAsc(state.getSeshCode())).thenReturn(commands);

        State result = gameLogicService.processQueue(seshCode);

        assertTrue(result.getHasChanged());
        assertEquals(TicTacShowStage.MAIN, ((TicTacShowState) result).getCurrentStage());
    }

    @Test
    void processQueue_vipStartSesh_with1PlayerNotInTeam_shouldNotStartSesh() {

        TicTacShowState state = getLobbyState(seshCode);
        TicTacShowPlayer vip = getVip(state);
        state.getPlayers().add(vip);
        when(stateRepository.findBySeshCode(state.getSeshCode())).thenReturn(state);

        Command startSeshCommand = getStartSeshCommand(vip);
        List<Command> commands = Collections.singletonList(startSeshCommand);
        when(commandRespository.findByCommandId_State_SeshCodeOrderByCommandId_TimestampAsc(state.getSeshCode())).thenReturn(commands);

        State result = gameLogicService.processQueue(seshCode);

        assertFalse(result.getHasChanged());
        assertEquals(TicTacShowStage.LOBBY, ((TicTacShowState) result).getCurrentStage());
    }

    @Test
    void processQueue_playerStartSesh_shouldNotStartSesh() {

        TicTacShowState state = getLobbyState(seshCode);
        when(stateRepository.findBySeshCode(state.getSeshCode())).thenReturn(state);

        TicTacShowPlayer vip = getVip(state);
        Command startSeshCommand = getStartSeshCommand(vip);
        List<Command> commands = Collections.singletonList(startSeshCommand);
        when(commandRespository.findByCommandId_State_SeshCodeOrderByCommandId_TimestampAsc(state.getSeshCode())).thenReturn(commands);

        State result = gameLogicService.processQueue(seshCode);

        assertFalse(result.getHasChanged());
        assertEquals(TicTacShowStage.LOBBY, ((TicTacShowState) result).getCurrentStage());
    }

    @Test
    void processQueue_joinTeam_shouldJoinTeamX() {

        TicTacShowState state = getLobbyState(seshCode);
        TicTacShowPlayer vip = getVip(state);
        state.getPlayers().add(vip);
        when(stateRepository.findBySeshCode(state.getSeshCode())).thenReturn(state);

        Command joinTeamCommand = getJoinTeamCommand(vip, "X");
        List<Command> commands = Collections.singletonList(joinTeamCommand);
        when(commandRespository.findByCommandId_State_SeshCodeOrderByCommandId_TimestampAsc(state.getSeshCode())).thenReturn(commands);

        TicTacShowState result = (TicTacShowState) gameLogicService.processQueue(state.getSeshCode());

        assertTrue(result.getTeamX().getPlayers().contains(vip));
        assertEquals(1, result.getTeamX().getPlayers().size());
    }

    @Test
    void processQueue_unbalancedTeams_shouldNotJoinTeamX() {

        TicTacShowState state = getLobbyState(seshCode);
        TicTacShowPlayer vip = getVip(state);
        state.getPlayers().add(vip);
        TicTacShowPlayer player1 = getTicTacShowPlayer(state,"player1");
        state.getPlayers().add(player1);
        when(stateRepository.findBySeshCode(state.getSeshCode())).thenReturn(state);

        Command joinTeamCommandVip = getJoinTeamCommand(vip, "X");
        Command joinTeamCommandPlayer1 = getJoinTeamCommand(player1,"X");
        List<Command> commands = new ArrayList<>();
        commands.add(joinTeamCommandVip);
        commands.add(joinTeamCommandPlayer1);
        when(commandRespository.findByCommandId_State_SeshCodeOrderByCommandId_TimestampAsc(state.getSeshCode())).thenReturn(commands);

        TicTacShowState result = (TicTacShowState) gameLogicService.processQueue(state.getSeshCode());

        assertTrue(result.getTeamX().getPlayers().contains(vip));
        assertFalse(result.getTeamX().getPlayers().contains(player1));
        assertEquals(1, result.getTeamX().getPlayers().size());
        assertEquals(0, result.getTeamO().getPlayers().size());
    }

    @Test
    void processQueue_joinTeam_shouldJoinTeamO() {

        TicTacShowState state = getLobbyState(seshCode);
        TicTacShowPlayer vip = getVip(state);
        state.getPlayers().add(vip);
        when(stateRepository.findBySeshCode(state.getSeshCode())).thenReturn(state);

        Command joinTeamCommand = getJoinTeamCommand(vip, "O");
        List<Command> commands = Collections.singletonList(joinTeamCommand);
        when(commandRespository.findByCommandId_State_SeshCodeOrderByCommandId_TimestampAsc(state.getSeshCode())).thenReturn(commands);

        TicTacShowState result = (TicTacShowState) gameLogicService.processQueue(state.getSeshCode());

        assertTrue(result.getTeamO().getPlayers().contains(vip));
        assertEquals(1, result.getTeamO().getPlayers().size());
    }

    @Test
    void processQueue_unbalancedTeams_shouldNotJoinTeamO() {

        TicTacShowState state = getLobbyState(seshCode);
        TicTacShowPlayer vip = getVip(state);
        state.getPlayers().add(vip);
        TicTacShowPlayer player1 = getTicTacShowPlayer(state,"player1");
        state.getPlayers().add(player1);
        when(stateRepository.findBySeshCode(state.getSeshCode())).thenReturn(state);

        Command joinTeamCommandVip = getJoinTeamCommand(vip, "O");
        Command joinTeamCommandPlayer1 = getJoinTeamCommand(player1,"O");
        List<Command> commands = new ArrayList<>();
        commands.add(joinTeamCommandVip);
        commands.add(joinTeamCommandPlayer1);
        when(commandRespository.findByCommandId_State_SeshCodeOrderByCommandId_TimestampAsc(state.getSeshCode())).thenReturn(commands);

        TicTacShowState result = (TicTacShowState) gameLogicService.processQueue(state.getSeshCode());

        assertTrue(result.getTeamO().getPlayers().contains(vip));
        assertFalse(result.getTeamO().getPlayers().contains(player1));
        assertEquals(1, result.getTeamO().getPlayers().size());
        assertEquals(0, result.getTeamX().getPlayers().size());
    }

    @Test
    void processQueue_shouldMakeVip() {

        TicTacShowState state = getLobbyState(seshCode);
        TicTacShowPlayer player = getTicTacShowPlayer(state, "player");
        state.getPlayers().add(player);
        when(stateRepository.findBySeshCode(state.getSeshCode())).thenReturn(state);

        Command makeVipCommand = getMakeVipCommand(player.getPlayerId().getPlayerName(), player.getPlayerId().getPlayerName());
        List<Command> commands = Collections.singletonList(makeVipCommand);
        when(commandRespository.findByCommandId_State_SeshCodeOrderByCommandId_TimestampAsc(state.getSeshCode())).thenReturn(commands);

        TicTacShowState result = (TicTacShowState) gameLogicService.processQueue(state.getSeshCode());

        assertTrue(result.getPlayers().get(0).getVip());
        assertTrue(result.getHasChanged());
    }

    @Test
    void processQueue_alreadyAVip_shouldNotMakeVip() {

        TicTacShowState state = getLobbyState(seshCode);
        TicTacShowPlayer vip = getVip(state);
        state.getPlayers().add(vip);
        TicTacShowPlayer player = getTicTacShowPlayer(state, "player");
        state.getPlayers().add(player);
        when(stateRepository.findBySeshCode(state.getSeshCode())).thenReturn(state);

        Command makeVipCommand = getMakeVipCommand(player.getPlayerId().getPlayerName(), player.getPlayerId().getPlayerName());
        List<Command> commands = Collections.singletonList(makeVipCommand);
        when(commandRespository.findByCommandId_State_SeshCodeOrderByCommandId_TimestampAsc(state.getSeshCode())).thenReturn(commands);

        TicTacShowState result = (TicTacShowState) gameLogicService.processQueue(state.getSeshCode());

        assertTrue(result.getPlayers().get(0).getVip());
        assertFalse(result.getPlayers().get(1).getVip());
        assertFalse(result.getHasChanged());
    }

    @Test
    void processQueue_alreadyAVip_shouldMakeSomeoneElseVip() {

        TicTacShowState state = getLobbyState(seshCode);
        TicTacShowPlayer vip = getVip(state);
        state.getPlayers().add(vip);
        TicTacShowPlayer player = getTicTacShowPlayer(state, "newVip");
        state.getPlayers().add(player);
        when(stateRepository.findBySeshCode(state.getSeshCode())).thenReturn(state);

        Command makeVipCommand = getMakeVipCommand(vip.getPlayerId().getPlayerName(), player.getPlayerId().getPlayerName());
        List<Command> commands = Collections.singletonList(makeVipCommand);
        when(commandRespository.findByCommandId_State_SeshCodeOrderByCommandId_TimestampAsc(state.getSeshCode())).thenReturn(commands);

        TicTacShowState result = (TicTacShowState) gameLogicService.processQueue(state.getSeshCode());

        assertFalse(result.getPlayers().get(0).getVip());
        assertTrue(result.getPlayers().get(1).getVip());
        assertTrue(result.getHasChanged());
    }

    private Command getMakeVipCommand(String executingPlayer, String targetPlayer) {

        Command command = new Command();
        command.setPlayerName(executingPlayer);
        command.setBody(targetPlayer);
        command.setType("makeVip");

        return command;
    }

    @Test
    void processQueue_UnKnownTeam_shouldNotJoinTeamO() {

        TicTacShowState state = getLobbyState(seshCode);
        TicTacShowPlayer vip = getVip(state);
        state.getPlayers().add(vip);
        when(stateRepository.findBySeshCode(state.getSeshCode())).thenReturn(state);

        Command joinTeamCommandVip = getJoinTeamCommand(vip, "A");
        List<Command> commands = Collections.singletonList(joinTeamCommandVip);
        when(commandRespository.findByCommandId_State_SeshCodeOrderByCommandId_TimestampAsc(state.getSeshCode())).thenReturn(commands);

        TicTacShowState result = (TicTacShowState) gameLogicService.processQueue(state.getSeshCode());

        assertEquals(0, result.getTeamO().getPlayers().size());
        assertEquals(0, result.getTeamX().getPlayers().size());
    }

    @Test
    void processQueue_shouldNotJoinTeamO() {

        TicTacShowState state = getLobbyState(seshCode);
        TicTacShowPlayer vip = getVip(state);
        state.getPlayers().add(vip);
        state.getTeamO().addPlayer(vip);
        vip.setTeam(state.getTeamO());
        when(stateRepository.findBySeshCode(state.getSeshCode())).thenReturn(state);

        Command leaveTeamCommand = getLeaveTeamCommand(vip);
        List<Command> commands = Collections.singletonList(joinTeamCommandVip);
        when(commandRespository.findByCommandId_State_SeshCodeOrderByCommandId_TimestampAsc(state.getSeshCode())).thenReturn(commands);

        TicTacShowState result = (TicTacShowState) gameLogicService.processQueue(state.getSeshCode());

        assertEquals(0, result.getTeamO().getPlayers().size());
        assertEquals(0, result.getTeamX().getPlayers().size());
    }

    private Command getLeaveTeamCommand(TicTacShowPlayer player) {

        Command command = new Command();
        command.setType("leaveTeam");
        command.setPlayerName(player.getPlayerName());
        return command;
    }

    private Command getJoinTeamCommand(TicTacShowPlayer vip, String team) {

        Command command = new Command();
        command.setType("joinTeam");
        command.setBody(team);
        command.setPlayerName(vip.getPlayerId().getPlayerName());
        return command;
    }

    private TicTacShowState getLobbyState(String seshCode) {

        TicTacShowState state = new TicTacShowState();
        state.setSeshCode(seshCode);
        state.setCurrentStage(TicTacShowStage.LOBBY);
        state.setPlayers(new ArrayList<>());
        state.setMaxPlayer(5L);
        state.setActive(true);
        state.setHostJoined(true);
        state.setHasChanged(false);
        state.setSeshType("TicTacShow");
        state.setTeamX(new Team());
        state.setTeamO(new Team());
        return state;
    }

    private TicTacShowState getMainState(String seshCode, List<Player> players) {

        TicTacShowState state = new TicTacShowState();
        state.setSeshCode(seshCode);
        state.setCurrentStage(TicTacShowStage.MAIN);
        state.setPlayers(players);
        state.setMaxPlayer(5L);
        state.setActive(true);
        state.setHostJoined(true);
        state.setHasChanged(false);
        state.setSeshType("TicTacShow");
        return state;
    }

    private TicTacShowPlayer getVip(TicTacShowState state) {

        TicTacShowPlayer vip = getTicTacShowPlayer(state, "vip");
        vip.setVip(true);
        return vip;
    }

    private static TicTacShowPlayer getTicTacShowPlayer(TicTacShowState state, String playerName) {

        TicTacShowPlayer player = new TicTacShowPlayer();
        PlayerId playerId = new PlayerId();
        playerId.setSeshCode(state.getSeshCode());
        playerId.setPlayerName(playerName);
        player.setPlayerId(playerId);
        player.setPoints(0L);
        player.setState(state);
        player.setVip(false);
        return player;
    }

    private Command getStartSeshCommand(TicTacShowPlayer player) {

        Command command = new Command();
        command.setType("startSesh");
        command.setPlayerName(player.getPlayerId().getPlayerName());
        return command;
    }
}
