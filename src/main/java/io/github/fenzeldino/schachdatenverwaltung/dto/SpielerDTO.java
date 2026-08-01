package io.github.fenzeldino.schachdatenverwaltung.dto;
import io.github.fenzeldino.schachdatenverwaltung.model.Turnier;

import java.util.List;

public record SpielerDTO(Integer spielerId,
                         String Name,
                         Double rating,
                         List<Turnier> turnierIds) {

    public SpielerDTO {
    }

    @Override
    public Integer spielerId() {
        return spielerId;
    }

    @Override
    public String Name() {
        return Name;
    }

    @Override
    public Double rating() {
        return rating;
    }

    @Override
    public List<Turnier> turnierIds() {
        return turnierIds;
    }
}
