package io.github.fenzeldino.schachdatenverwaltung.dto.request.spieler;

import java.util.List;

public record SpielerCreateDTO(
                               String Name,
                               Double rating,
                               Integer alter,
                               List<Integer> turnierIds,
                               Integer vereinId) {

    public SpielerCreateDTO {
    }

    public SpielerCreateDTO(String Name, Double rating, Integer alter, List<Integer> turnierIds) {
        this(Name, rating, alter, turnierIds, null);
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

    @Override
    public Integer vereinId() {
        return vereinId;
    }
}
