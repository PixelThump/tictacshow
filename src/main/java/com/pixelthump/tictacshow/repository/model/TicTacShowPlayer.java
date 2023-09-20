package com.pixelthump.tictacshow.repository.model;
import com.pixelthump.seshtypelib.service.model.player.Player;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@Getter
@Setter
public class TicTacShowPlayer extends Player {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "team_id")
    private Team team;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TicTacShowPlayer player = (TicTacShowPlayer) o;
        return getPlayerId() != null && Objects.equals(getPlayerId(), player.getPlayerId());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getPlayerId());
    }
}
