package com.pixelthump.tictacshow.repository;
import com.pixelthump.tictacshow.repository.model.TicTacShowState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicTacShowStateRepository extends JpaRepository<TicTacShowState, String> {

    List<TicTacShowState> findByActive(Boolean active);

    Optional<TicTacShowState> findBySeshCodeAndActive(String seshCode, Boolean active);

    boolean existsBySeshCode(String seshCode);

    TicTacShowState findBySeshCode(String seshCode);

}