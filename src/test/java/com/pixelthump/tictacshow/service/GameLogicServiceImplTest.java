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
        assertEquals(TicTacShowStage.MAIN, ((TicTacShowState)result).getCurrentStage());
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
        assertEquals(TicTacShowStage.LOBBY, ((TicTacShowState)result).getCurrentStage());
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
        assertEquals(TicTacShowStage.LOBBY, ((TicTacShowState)result).getCurrentStage());
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

    private TicTacShowPlayer getVip(TicTacShowState state){
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

    private Command getStartSeshCommand(TicTacShowPlayer player){
        Command command = new Command();
        command.setType("startSesh");
        command.setPlayerName(player.getPlayerId().getPlayerName());
        return command;
    }
}
