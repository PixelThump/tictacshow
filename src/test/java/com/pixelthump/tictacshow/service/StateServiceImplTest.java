package com.pixelthump.tictacshow.service;
import com.pixelthump.seshtypelib.service.StateService;
import com.pixelthump.seshtypelib.service.model.State;
import com.pixelthump.seshtypelib.service.model.messaging.AbstractServiceState;
import com.pixelthump.seshtypelib.service.model.player.Player;
import com.pixelthump.seshtypelib.service.model.player.PlayerId;
import com.pixelthump.tictacshow.Application;
import com.pixelthump.tictacshow.repository.TicTacShowStateRepository;
import com.pixelthump.tictacshow.repository.model.Team;
import com.pixelthump.tictacshow.repository.model.TicTacShowPlayer;
import com.pixelthump.tictacshow.repository.model.TicTacShowStage;
import com.pixelthump.tictacshow.repository.model.TicTacShowState;
import com.pixelthump.tictacshow.service.model.ControllerLobbyState;
import com.pixelthump.tictacshow.service.model.ControllerMainState;
import com.pixelthump.tictacshow.service.model.ServiceHostLobbyState;
import com.pixelthump.tictacshow.service.model.ServiceHostMainState;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = Application.class)
class StateServiceImplTest {

    @MockBean
    TicTacShowStateRepository stateRepository;
    @Autowired
    StateService stateService;
    String seshCode = "abcd";

    @AfterEach
    void teardown(){
        reset(stateRepository);
    }

    @Test
    void findBySeshCode() {

        TicTacShowState state = new TicTacShowState();
        when(stateRepository.findBySeshCode(seshCode)).thenReturn(state);
        State result = stateService.findBySeshCode(seshCode);
        verify(stateRepository, times(1)).findBySeshCode(seshCode);
        assertEquals(state, result);
        assertEquals(state, result);
    }

    @Test
    void existsBySeshCode() {

        when(stateRepository.existsBySeshCode(seshCode)).thenReturn(true);
        boolean result = stateService.existsBySeshCode(seshCode);
        verify(stateRepository, times(1)).existsBySeshCode(seshCode);
        assertTrue(result);
    }

    @Test
    void findBySeshCodeAndActive() {

        TicTacShowState state = new TicTacShowState();

        when(stateRepository.findBySeshCodeAndActive(seshCode, true)).thenReturn(Optional.of(state));
        Optional<? extends State> result = stateService.findBySeshCodeAndActive(seshCode, true);
        verify(stateRepository, times(1)).findBySeshCodeAndActive(seshCode, true);
        assertEquals(Optional.of(state), result);
    }

    @Test
    void findByActive() {

        TicTacShowState state = new TicTacShowState();
        List<TicTacShowState> states = Collections.singletonList(state);
        when(stateRepository.findByActive(true)).thenReturn(states);
        List<? extends State> result = stateService.findByActive(true);
        verify(stateRepository, times(1)).findByActive(true);
        assertEquals(states, result);
    }

    @Test
    void save() {

        TicTacShowState state = new TicTacShowState();
        when(stateRepository.save(state)).thenReturn(state);
        State result = stateService.save(state);
        verify(stateRepository, times(1)).save(state);
        assertEquals(state, result);
    }

    @Test
    void getHostState_Lobby() {

        TicTacShowState state = getLobbyState();

        AbstractServiceState result = stateService.getHostState(state);

        assertEquals(result.getSeshCode(), state.getSeshCode());
        assertEquals(ServiceHostLobbyState.class, result.getClass());
        ServiceHostLobbyState castedResult = (ServiceHostLobbyState) result;

        assertNotNull(castedResult.getPlayers());
        assertEquals(2, castedResult.getPlayers().size());

        assertNotNull(castedResult.getTeamX());
        assertNotNull(castedResult.getTeamX().getPlayers());
        assertEquals(1, castedResult.getTeamX().getPlayers().size());
        assertEquals("player", castedResult.getTeamX().getPlayers().get(0).getPlayerName());
        assertFalse(castedResult.getTeamX().getPlayers().get(0).isVip());

        assertNotNull(castedResult.getTeamO());
        assertNotNull(castedResult.getTeamO().getPlayers());
        assertEquals(1, castedResult.getTeamO().getPlayers().size());
        assertEquals("vip", castedResult.getTeamO().getPlayers().get(0).getPlayerName());
        assertTrue(castedResult.getTeamO().getPlayers().get(0).isVip());
    }

    @Test
    void getHostState_Main() {

        TicTacShowState state = getMainState();
        AbstractServiceState result = stateService.getHostState(state);
        assertEquals(result.getSeshCode(), state.getSeshCode());
        assertEquals(ServiceHostMainState.class, result.getClass());
        ServiceHostMainState castedResult = (ServiceHostMainState) result;

        assertNotNull(castedResult.getTeamX());
        assertNotNull(castedResult.getTeamX().getPlayers());
        assertEquals(1, castedResult.getTeamX().getPlayers().size());
        assertEquals("player", castedResult.getTeamX().getPlayers().get(0).getPlayerName());
        assertFalse(castedResult.getTeamX().getPlayers().get(0).isVip());

        assertNotNull(castedResult.getTeamO());
        assertNotNull(castedResult.getTeamO().getPlayers());
        assertEquals(1, castedResult.getTeamO().getPlayers().size());
        assertEquals("vip", castedResult.getTeamO().getPlayers().get(0).getPlayerName());
        assertTrue(castedResult.getTeamO().getPlayers().get(0).isVip());
    }

    @Test
    void getControllerState_Lobby() {

        TicTacShowState state = getLobbyState();
        Player player = state.getPlayers().get(0);
        AbstractServiceState result = stateService.getControllerState(player, state);
        assertEquals(result.getSeshCode(), state.getSeshCode());
        assertEquals(ControllerLobbyState.class, result.getClass());
        ControllerLobbyState castedResult = (ControllerLobbyState) result;
        assertEquals(player.getPlayerId().getPlayerName(), castedResult.getPlayer().getPlayerName());
        assertTrue(castedResult.getPlayer().isVip());
    }
    @Test
    void getControllerState_Main() {

        TicTacShowState state = getMainState();
        Player player = state.getPlayers().get(0);
        AbstractServiceState result = stateService.getControllerState(player, state);
        assertEquals(result.getSeshCode(), state.getSeshCode());
        assertEquals(ControllerMainState.class, result.getClass());
        ControllerMainState castedResult = (ControllerMainState) result;
        assertEquals(player.getPlayerId().getPlayerName(), castedResult.getPlayer().getPlayerName());
        assertTrue(castedResult.getPlayer().isVip());
    }

    @NotNull
    private TicTacShowState getLobbyState() {

        TicTacShowState state = new TicTacShowState();
        state.setSeshCode(seshCode);
        state.setSeshType("Tic-Tac-Show");
        state.setCurrentStage(TicTacShowStage.LOBBY);

        TicTacShowPlayer vip = getVip(state);
        state.getPlayers().add(vip);
        Team teamO = new Team();
        teamO.addPlayer(vip);
        state.setTeamO(teamO);

        TicTacShowPlayer player = getTicTacShowPlayer(state,"player");
        state.getPlayers().add(player);
        Team teamX = new Team();
        teamX.addPlayer(player);
        state.setTeamX(teamX);

        state.setHostJoined(true);
        return state;
    }

    @NotNull
    private TicTacShowState getMainState() {

        TicTacShowState state = new TicTacShowState();
        state.setSeshCode(seshCode);
        state.setSeshType("Tic-Tac-Show");
        state.setCurrentStage(TicTacShowStage.MAIN);

        TicTacShowPlayer vip = getVip(state);
        state.getPlayers().add(vip);
        Team teamO = new Team();
        teamO.addPlayer(vip);
        state.setTeamO(teamO);

        TicTacShowPlayer player = getTicTacShowPlayer(state,"player");
        state.getPlayers().add(player);
        Team teamX = new Team();
        teamX.addPlayer(player);
        state.setTeamX(teamX);

        state.setHostJoined(true);
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

}