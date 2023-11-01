package com.pixelthump.tictacshow.service;
import com.pixelthump.seshtypelib.service.PlayerService;
import com.pixelthump.seshtypelib.service.model.player.Player;
import com.pixelthump.tictacshow.Application;
import com.pixelthump.tictacshow.repository.TicTacShowPlayerRepository;
import com.pixelthump.tictacshow.repository.model.TicTacShowPlayer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = Application.class)
class PlayerServiceImplTest {

    @MockBean
    TicTacShowPlayerRepository playerRepository;
    @Autowired
    PlayerService playerService;

    @Test
    void existsByPlayerNameAndSeshCode() {
        when(playerRepository.existsByPlayerId_PlayerNameAndPlayerId_SeshCode("playerName", "abcd")).thenReturn(true);
        boolean result = playerService.existsByPlayerNameAndSeshCode("playerName", "abcd");
        verify(playerRepository, times(1)).existsByPlayerId_PlayerNameAndPlayerId_SeshCode("playerName", "abcd");
        assertTrue(result);
    }

    @Test
    void save() {
        Player player = new TicTacShowPlayer();
        when(playerRepository.save(player)).thenReturn(player);
        Player result = playerService.save(player);
        verify(playerRepository, times(1)).save(player);
        assertEquals(player, result);
    }
}