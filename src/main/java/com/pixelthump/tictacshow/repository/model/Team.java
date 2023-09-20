package com.pixelthump.tictacshow.repository.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Team implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicTacShowPlayer> players = new ArrayList<>();


    public void addPlayer(TicTacShowPlayer player){
        players.add(player);
    }

    public List<TicTacShowPlayer> getPlayers() {

        return players;
    }
}
