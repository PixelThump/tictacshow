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
    private List<TicTacShowPlayer> ticTacShowPlayers = new ArrayList<>();

    public List<TicTacShowPlayer> getTicTacShowPlayers() {

        return ticTacShowPlayers;
    }

    public void setTicTacShowPlayers(List<TicTacShowPlayer> ticTacShowPlayers) {

        this.ticTacShowPlayers = ticTacShowPlayers;
    }

    public Long getId() {

        return id;
    }

    public void setId(Long id) {

        this.id = id;
    }
}
