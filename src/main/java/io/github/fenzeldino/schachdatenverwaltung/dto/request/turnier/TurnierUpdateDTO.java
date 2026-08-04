package io.github.fenzeldino.schachdatenverwaltung.dto.request.turnier;

import java.util.List;

public record TurnierUpdateDTO(Integer turnierId, List<Integer> spielerIds) {
}
