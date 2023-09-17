package com.pixelthump.tictacshow.service;
import com.pixelthump.seshtypelib.service.StateService;
import com.pixelthump.seshtypelib.service.model.State;
import com.pixelthump.tictacshow.Application;
import com.pixelthump.tictacshow.repository.TicTacShowStateRepository;
import com.pixelthump.tictacshow.repository.model.TicTacShowState;
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

    @Test
    void findBySeshCode() {
        TicTacShowState state = new TicTacShowState();
        when(stateRepository.findBySeshCode(seshCode)).thenReturn(state);
        State result = stateService.findBySeshCode(seshCode);
        verify(stateRepository,times(1)).findBySeshCode(seshCode);
        assertEquals(state, result);
    }

    @Test
    void existsBySeshCode() {
        when(stateRepository.existsBySeshCode(seshCode)).thenReturn(true);
        boolean result = stateService.existsBySeshCode(seshCode);
        verify(stateRepository,times(1)).existsBySeshCode(seshCode);
        assertTrue(result);
    }

    @Test
    void findBySeshCodeAndActive() {
        TicTacShowState state = new TicTacShowState();

        when(stateRepository.findBySeshCodeAndActive(seshCode, true)).thenReturn(Optional.of(state));
        Optional<? extends State> result = stateService.findBySeshCodeAndActive(seshCode,true);
        verify(stateRepository,times(1)).findBySeshCodeAndActive(seshCode, true);
        assertEquals(Optional.of(state), result);
    }

    @Test
    void findByActive() {
        TicTacShowState state = new TicTacShowState();
        List<TicTacShowState> states = Collections.singletonList(state);
        when(stateRepository.findByActive(true)).thenReturn(states);
        List<? extends State> result = stateService.findByActive(true);
        verify(stateRepository,times(1)).findByActive(true);
        assertEquals(states, result);
    }

    @Test
    void save() {

        TicTacShowState state = new TicTacShowState();
        when(stateRepository.save(state)).thenReturn(state);
        State result = stateService.save(state);
        verify(stateRepository,times(1)).save(state);
        assertEquals(state, result);
    }

    @Test
    void getHostState() {

    }

    @Test
    void getControllerState() {

    }
}