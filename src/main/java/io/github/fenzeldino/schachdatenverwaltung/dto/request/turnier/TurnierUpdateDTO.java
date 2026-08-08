package io.github.fenzeldino.schachdatenverwaltung.dto.request.turnier;

import io.github.fenzeldino.schachdatenverwaltung.model.TurnierStatus;

import java.time.LocalDate;
import java.util.List;

public record TurnierUpdateDTO(Integer turnierId, String name, LocalDate datum, String ort,
                               TurnierStatus status, List<Integer> spielerIds) {
}
