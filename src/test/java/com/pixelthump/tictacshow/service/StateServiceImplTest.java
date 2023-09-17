package com.pixelthump.tictacshow.service;
import com.pixelthump.seshtypelib.service.StateService;
import com.pixelthump.seshtypelib.service.model.State;
import com.pixelthump.seshtypelib.service.model.messaging.AbstractServiceState;
import com.pixelthump.tictacshow.Application;
import com.pixelthump.tictacshow.repository.TicTacShowStateRepository;
import com.pixelthump.tictacshow.repository.model.TicTacShowPlayer;
import com.pixelthump.tictacshow.repository.model.TicTacShowStage;
import com.pixelthump.tictacshow.repository.model.TicTacShowState;
import com.pixelthump.tictacshow.service.model.ControllerLobyState;
import com.pixelthump.tictacshow.service.model.ControllerMainState;
import com.pixelthump.tictacshow.service.model.ServiceHostLobbyState;
import com.pixelthump.tictacshow.service.model.ServiceHostMainState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        assertTrue(state.equals(result));
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

        TicTacShowState state = new TicTacShowState();
        state.setSeshCode(seshCode);
        state.setSeshType("Tic-Tac-Show");
        state.setCurrentStage(TicTacShowStage.LOBBY);
        AbstractServiceState result = stateService.getHostState(state);
        assertEquals(result.getSeshCode(), state.getSeshCode());
        assertEquals(ServiceHostLobbyState.class, result.getClass());
    }

    @Test
    void getHostState_Main() {

        TicTacShowState state = new TicTacShowState();
        state.setSeshCode(seshCode);
        state.setSeshType("Tic-Tac-Show");
        state.setCurrentStage(TicTacShowStage.MAIN);
        AbstractServiceState result = stateService.getHostState(state);
        assertEquals(result.getSeshCode(), state.getSeshCode());
        assertEquals(ServiceHostMainState.class, result.getClass());
    }

    @Test
    void getControllerState_Lobby() {

        TicTacShowState state = new TicTacShowState();
        state.setSeshCode(seshCode);
        state.setSeshType("Tic-Tac-Show");
        state.setCurrentStage(TicTacShowStage.LOBBY);
        TicTacShowPlayer player = new TicTacShowPlayer();
        AbstractServiceState result = stateService.getControllerState(player, state);
        assertEquals(result.getSeshCode(), state.getSeshCode());
        assertEquals(ControllerLobyState.class, result.getClass());
    }
    @Test
    void getControllerState_Main() {

        TicTacShowState state = new TicTacShowState();
        state.setSeshCode(seshCode);
        state.setSeshType("Tic-Tac-Show");
        state.setCurrentStage(TicTacShowStage.MAIN);
        TicTacShowPlayer player = new TicTacShowPlayer();
        AbstractServiceState result = stateService.getControllerState(player, state);
        assertEquals(result.getSeshCode(), state.getSeshCode());
        assertEquals(ControllerMainState.class, result.getClass());
    }
}