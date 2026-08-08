package io.github.fenzeldino.schachdatenverwaltung.dto.request.turnier;

import java.time.LocalDate;
import java.util.List;

public record TurnierCreateDTO(String name, LocalDate datum, String ort, List<Integer> spielerIds) {
}
