package com.pixelthump.tictacshow.repository.model;
import com.pixelthump.seshtypelib.service.model.State;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@Getter
@Setter
public class TicTacShowState extends State {

    @Enumerated
    @Column(nullable = false)
    private TicTacShowStage currentStage;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "team_x_id")
    private Team teamX;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "team_o_id")
    private Team teamO;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TicTacShowState state = (TicTacShowState) o;
        return getSeshCode() != null && Objects.equals(getSeshCode(), state.getSeshCode());
    }

    @Override
    public int hashCode() {

        return getClass().hashCode();
    }
}
