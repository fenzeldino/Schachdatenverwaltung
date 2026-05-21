package io.github.fenzeldino.Schachdatenverwaltung.DTO;

import io.github.fenzeldino.Schachdatenverwaltung.Model.Spieler;

import java.util.List;
import java.util.Set;

public record TurnierDTO(
        int TurnierId,
        Set<Integer> spielerIds,
        Set<Integer> matchIds
) {
}
