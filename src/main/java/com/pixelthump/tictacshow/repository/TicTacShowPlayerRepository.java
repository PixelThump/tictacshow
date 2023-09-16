package com.pixelthump.tictacshow.repository;
import com.pixelthump.seshtypelib.service.model.player.PlayerId;
import com.pixelthump.tictacshow.repository.model.TicTacShowPlayer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicTacShowPlayerRepository extends JpaRepository<TicTacShowPlayer, PlayerId> {

}