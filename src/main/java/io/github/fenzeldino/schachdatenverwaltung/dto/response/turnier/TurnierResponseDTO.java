package io.github.fenzeldino.schachdatenverwaltung.dto.response.turnier;

import io.github.fenzeldino.schachdatenverwaltung.model.TurnierStatus;

import java.time.LocalDate;
import java.util.Set;

/**
 * @param teilnehmerAnzahl abgeleitet aus spielerIds.size() — für die
 *                         Turnier-Liste (Miro-Frame "1. Turnierverwaltung")
 *                         mitgeliefert, damit die Oberfläche nicht extra
 *                         zählen muss.
 */
public record TurnierResponseDTO(Integer turnierId,
                                 String name,
                                 LocalDate datum,
                                 String ort,
                                 TurnierStatus status,
                                 Integer teilnehmerAnzahl,
                                 Set<Integer> spielerIds,
                                 Set<Integer> matchIds) {
}
