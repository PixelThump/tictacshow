package com.pixelthump.tictacshow.service.model;
import com.pixelthump.seshtypelib.service.model.messaging.AbstractServiceState;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AbstractServiceControllerState extends AbstractServiceState {

    private ServicePlayer player;
}
