package com.pixelthump.tictacshow.repository;
import com.pixelthump.tictacshow.repository.model.TicTacShowState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicTacShowStateRepository extends JpaRepository<TicTacShowState, String> {

}