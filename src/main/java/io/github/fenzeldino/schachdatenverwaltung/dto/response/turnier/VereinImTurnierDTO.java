package io.github.fenzeldino.schachdatenverwaltung.dto.response.turnier;

/**
 * Projektion für "welche Vereine sind in diesem Turnier vertreten"
 * (Miro-Frame "4. Turnier – Detail", Abschnitt "Vereine im Turnier").
 *
 * spielerImTurnier zählt nur die Teilnehmer DIESES Turniers, nicht die
 * Gesamtgröße des Vereins — dafür siehe VereinResponseDTO.spielerAnzahl.
 */
public record VereinImTurnierDTO(Integer vereinId,
                                 String name,
                                 Integer spielerImTurnier) {
}
