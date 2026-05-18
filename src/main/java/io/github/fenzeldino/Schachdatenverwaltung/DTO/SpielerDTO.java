package io.github.fenzeldino.Schachdatenverwaltung.DTO;
import java.util.Set;
public record SpielerDTO(Integer spielerId,
                         String Name,
                         Double rating,
                         Set<Integer> TurnierIds) {

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
    public Set<Integer> TurnierIds() {
        return TurnierIds;
    }
}
