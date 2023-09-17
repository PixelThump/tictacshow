package com.pixelthump.tictacshow.service;
import com.pixelthump.seshtypelib.repository.CommandRespository;
import com.pixelthump.seshtypelib.repository.model.command.Command;
import com.pixelthump.seshtypelib.service.GameLogicService;
import com.pixelthump.tictacshow.Application;
import com.pixelthump.tictacshow.repository.TicTacShowStateRepository;
import com.pixelthump.tictacshow.repository.model.TicTacShowState;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest(classes = Application.class)
class GameLogicServiceImplTest {

    @MockBean
    TicTacShowStateRepository stateRepository;
    @MockBean
    CommandRespository commandRespository;
    @Autowired
    GameLogicService gameLogicService;
    String seshCode = "abcd";

    @Test
    void processQueue_shouldRetrieveStateAndCommandsAndDeleteAllCommandsAfterwards() {

        TicTacShowState state = new TicTacShowState();
        state.setSeshCode(seshCode);
        when(stateRepository.findBySeshCode(seshCode)).thenReturn(state);
        List<Command> commands = new ArrayList<>();
        when(commandRespository.findByCommandId_State_SeshCodeOrderByCommandId_TimestampAsc(seshCode)).thenReturn(commands);
        gameLogicService.processQueue(seshCode);
        InOrder inOrder = inOrder(stateRepository, commandRespository);
        inOrder.verify(stateRepository, times(1)).findBySeshCode(seshCode);
        inOrder.verify(commandRespository, times(1)).findByCommandId_State_SeshCodeOrderByCommandId_TimestampAsc(seshCode);
        inOrder.verify(commandRespository, times(1)).deleteAll(commands);
    }
}