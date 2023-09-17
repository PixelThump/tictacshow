package com.pixelthump.tictacshow.service;
import com.pixelthump.seshtypelib.service.StateFactory;
import com.pixelthump.seshtypelib.service.model.State;
import com.pixelthump.tictacshow.Application;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
class StateFactoryImplTest {

    @Autowired
    StateFactory stateFactory;

    String seshCode = "abcd";

    @Test
    void createSeshTypeState() {

        State result = stateFactory.createSeshTypeState(seshCode);

        assertEquals(seshCode, result.getSeshCode());
        assertEquals("TicTacShow", result.getSeshType());
        assertNotNull(result.getPlayers());
        assertTrue(result.getPlayers().isEmpty());
        assertFalse(result.isHostJoined());
        assertFalse(result.getActive());
        assertFalse(result.getHasChanged());
        assertEquals(5, result.getMaxPlayer());
    }
}