package io.github.fenzeldino.schachdatenverwaltung.dto.response.matchUp;

import io.github.fenzeldino.schachdatenverwaltung.model.Spieler;

/**
 * @param gewinner null solange das Ergebnis noch nicht gesetzt ist. Es gibt
 *                (noch) kein eigenes Verlierer-Feld — siehe bekannter Bug
 *                zu MatchUpService.addVerlierer() im Miro-Board.
 */
public record MatchUpResponseDTO(Integer matchUpId, Spieler spielerEins, Spieler spielerZwei, Spieler gewinner) {
}
