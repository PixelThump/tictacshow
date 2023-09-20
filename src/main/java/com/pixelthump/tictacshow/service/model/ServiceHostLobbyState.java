package com.pixelthump.tictacshow.service.model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ServiceHostLobbyState extends AbstractServiceHostState {

    private List<ServicePlayer> players;
    boolean hasVip;
}
