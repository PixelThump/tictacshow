package com.pixelthump.tictacshow.service;
import com.pixelthump.seshtypelib.service.PlayerService;
import com.pixelthump.seshtypelib.service.model.player.Player;
import org.springframework.stereotype.Component;

@Component
public class PlayerServiceImpl implements PlayerService {

    @Override
    public boolean existsByPlayerNameAndSeshCode(String playerName, String seshCode) {

        return false;
    }

    @Override
    public Player save(Player player) {

        return null;
    }
}
