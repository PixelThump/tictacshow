package com.pixelthump.tictacshow.service;
import com.pixelthump.seshtypelib.service.StateService;
import com.pixelthump.seshtypelib.service.model.State;
import com.pixelthump.seshtypelib.service.model.messaging.AbstractServiceState;
import com.pixelthump.seshtypelib.service.model.player.Player;
import com.pixelthump.tictacshow.repository.TicTacShowStateRepository;
import com.pixelthump.tictacshow.repository.model.TicTacShowPlayer;
import com.pixelthump.tictacshow.repository.model.TicTacShowStage;
import com.pixelthump.tictacshow.repository.model.TicTacShowState;
import com.pixelthump.tictacshow.service.model.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class StateServiceImpl implements StateService {

    private final TicTacShowStateRepository stateRepository;
    private final ModelMapper mapper;

    @Autowired
    public StateServiceImpl(TicTacShowStateRepository stateRepository, ModelMapper mapper) {

        this.stateRepository = stateRepository;
        this.mapper = mapper;
    }

    @Override
    public State findBySeshCode(String seshCode) {

        return stateRepository.findBySeshCode(seshCode);
    }

    @Override
    public boolean existsBySeshCode(String seshCode) {

        return stateRepository.existsBySeshCode(seshCode);
    }

    @Override
    public Optional<? extends State> findBySeshCodeAndActive(String seshCode, Boolean active) {

        return stateRepository.findBySeshCodeAndActive(seshCode, active);
    }

    @Override
    public List<? extends State> findByActive(Boolean active) {

        return stateRepository.findByActive(active);
    }

    @Override
    public State save(State state) {

        return stateRepository.save((TicTacShowState) state);
    }

    @Override
    public AbstractServiceState getHostState(State state) {

        AbstractServiceState hostState;
        hostState = getStageHostState((TicTacShowState) state);
        hostState.setSeshCode(state.getSeshCode());
        return hostState;
    }

    @Override
    public AbstractServiceState getControllerState(Player player, State state) {

        AbstractServiceControllerState controllerState = getControllerStageState(player, (TicTacShowState) state);
        controllerState.setSeshCode(state.getSeshCode());
        return controllerState;
    }

    private AbstractServiceHostState getStageHostState(TicTacShowState state) {

        AbstractServiceHostState hostState;

        if (state.getCurrentStage().equals(TicTacShowStage.LOBBY)) {
            hostState = getHostLobbyState(state);
        } else {
            hostState = getHostMainState(state);
        }

        ServiceTeam teamX = mapper.map(state.getTeamX(), ServiceTeam.class);
        hostState.setTeamX(teamX);
        ServiceTeam teamO = mapper.map(state.getTeamO(), ServiceTeam.class);
        hostState.setTeamO(teamO);
        return hostState;
    }

    private AbstractServiceHostState getHostMainState(TicTacShowState state) {

        ServiceHostMainState hostState = new ServiceHostMainState();
        return hostState;
    }

    private AbstractServiceHostState getHostLobbyState(TicTacShowState state) {

        ServiceHostLobbyState hostState = new ServiceHostLobbyState();
        List<ServicePlayer> players = state.getPlayers().stream().map(TicTacShowPlayer.class::cast).map(this::convertToServicePlayer).toList();
        hostState.setPlayers(players);
        hostState.setHasVip(players.stream().anyMatch(ServicePlayer::isVip));
        return hostState;
    }

    private ServicePlayer convertToServicePlayer(TicTacShowPlayer player) {

        ServicePlayer servicePlayer = new ServicePlayer();
        servicePlayer.setPlayerName(player.getPlayerId().getPlayerName());
        servicePlayer.setVip(player.getVip());
        return servicePlayer;
    }

    private AbstractServiceControllerState getControllerStageState(Player player, TicTacShowState state) {

        AbstractServiceControllerState controllerState;

        if (state.getCurrentStage().equals(TicTacShowStage.LOBBY)) {
            controllerState = getControllerLobbyState(player, state);
        } else {
            controllerState = getControllerMainState(player, state);
        }
        return controllerState;
    }

    private ControllerMainState getControllerMainState(Player player, TicTacShowState state) {

        ControllerMainState controllerState = new ControllerMainState();
        return controllerState;
    }

    private ControllerLobyState getControllerLobbyState(Player player, TicTacShowState state) {

        ControllerLobyState controllerState = new ControllerLobyState();
        return controllerState;
    }
}
