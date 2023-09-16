package com.pixelthump.tictacshow.service;
import com.pixelthump.seshtypelib.service.StateService;
import com.pixelthump.seshtypelib.service.model.State;
import com.pixelthump.seshtypelib.service.model.messaging.AbstractServiceState;
import com.pixelthump.seshtypelib.service.model.player.Player;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class StateServiceImpl implements StateService {

    @Override
    public State findBySeshCode(String seshCode) {

        return null;
    }

    @Override
    public boolean existsBySeshCode(String seshCode) {

        return false;
    }

    @Override
    public Optional<? extends State> findBySeshCodeAndActive(String seshCode, Boolean active) {

        return Optional.empty();
    }

    @Override
    public List<? extends State> findByActive(Boolean active) {

        return Collections.emptyList();
    }

    @Override
    public State save(State state) {

        return null;
    }

    @Override
    public AbstractServiceState getHostState(State state) {

        return null;
    }

    @Override
    public AbstractServiceState getControllerState(Player player, State state) {

        return null;
    }
}
