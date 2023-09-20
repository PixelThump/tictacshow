package com.pixelthump.tictacshow.service;
import com.pixelthump.seshtypelib.service.StateFactory;
import com.pixelthump.seshtypelib.service.model.State;
import com.pixelthump.tictacshow.Application;
import com.pixelthump.tictacshow.repository.TicTacShowStateRepository;
import com.pixelthump.tictacshow.repository.model.TicTacShowState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = Application.class)
class StateFactoryImplTest {

    @Autowired
    StateFactory stateFactory;

    @MockBean
    TicTacShowStateRepository stateRepository;

    String seshCode = "abcd";

    @Test
    void createSeshTypeState() {

        when(stateRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        TicTacShowState result = (TicTacShowState) stateFactory.createSeshTypeState(seshCode);
        verify(stateRepository, times(1)).save(result);

        verifyStandardFields(result);
        verifyTeams(result);
    }

    private void verifyStandardFields(State result) {

        assertEquals(seshCode, result.getSeshCode());
        assertEquals("TicTacShow", result.getSeshType());
        assertNotNull(result.getPlayers());
        assertTrue(result.getPlayers().isEmpty());
        assertFalse(result.isHostJoined());
        assertFalse(result.getActive());
        assertFalse(result.getHasChanged());
        assertEquals(5, result.getMaxPlayer());
    }

    private static void verifyTeams(TicTacShowState result) {

        assertNotNull(result.getTeamO());
        assertEquals(0, result.getTeamO().getPlayers().size());
        assertNotNull(result.getTeamX());
        assertEquals(0, result.getTeamX().getPlayers().size());
    }
}