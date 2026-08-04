package io.github.fenzeldino.schachdatenverwaltung.dto.request.turnier;

import java.util.List;

public record TurnierCreateDTO(List<Integer> spielerIds) {
}
