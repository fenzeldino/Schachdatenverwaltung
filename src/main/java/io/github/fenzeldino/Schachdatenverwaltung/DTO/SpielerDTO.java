package io.github.fenzeldino.Schachdatenverwaltung.DTO;
import io.github.fenzeldino.Schachdatenverwaltung.Model.Turnier;

import java.util.List;
import java.util.Set;
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
