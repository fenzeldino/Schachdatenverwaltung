package io.github.fenzeldino.schachdatenverwaltung.dto;

import java.util.Set;

public record TurnierDTO(
        int TurnierId,
        Set<Integer> spielerIds,
        Set<Integer> matchIds
) {
}
