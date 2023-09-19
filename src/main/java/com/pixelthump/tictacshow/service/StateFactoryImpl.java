package com.pixelthump.tictacshow.service;
import com.pixelthump.seshtypelib.service.StateFactory;
import com.pixelthump.seshtypelib.service.model.State;
import com.pixelthump.tictacshow.repository.model.TicTacShowState;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class StateFactoryImpl implements StateFactory {

    @Override
    public State createSeshTypeState(String seshCode) {

        TicTacShowState state = new TicTacShowState();
        fillStandardFields(seshCode, state);
        return state;
    }

    private static void fillStandardFields(String seshCode, TicTacShowState state) {

        state.setSeshCode(seshCode);
        state.setSeshType("TicTacShow");
        state.setPlayers(new ArrayList<>());
        state.setHostJoined(false);
        state.setActive(false);
        state.setHasChanged(false);
        state.setMaxPlayer(5L);
    }
}
