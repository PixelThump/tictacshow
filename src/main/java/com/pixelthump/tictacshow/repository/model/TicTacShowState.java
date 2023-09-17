package com.pixelthump.tictacshow.repository.model;
import com.pixelthump.seshtypelib.service.model.State;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
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
