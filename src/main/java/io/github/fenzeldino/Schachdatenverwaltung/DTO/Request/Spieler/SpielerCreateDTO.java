package io.github.fenzeldino.Schachdatenverwaltung.DTO.Request.Spieler;

import java.util.List;

public record SpielerCreateDTO(
                               String Name,
                               Double rating,
                               Integer alter,
                               List<Integer> turnierIds) {

    public SpielerCreateDTO {
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
    public Integer alter() {
        return alter;
    }

    @Override
    public List<Integer> turnierIds() {
        return turnierIds;
    }
}
