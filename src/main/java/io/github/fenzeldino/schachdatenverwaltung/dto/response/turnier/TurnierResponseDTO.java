package io.github.fenzeldino.schachdatenverwaltung.dto.response.turnier;

import java.util.Set;

public record TurnierResponseDTO(Integer turnierId, Set<Integer> spielerIds, Set<Integer> matchIds) {
}
