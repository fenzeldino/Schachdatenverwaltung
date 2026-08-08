package io.github.fenzeldino.schachdatenverwaltung.dto.response.matchUp;

/**
 * @param gewinner null solange das Ergebnis noch nicht gesetzt ist. Es gibt
 *                (noch) kein eigenes Verlierer-Feld — siehe bekannter Bug
 *                zu MatchUpService.addVerlierer() im Miro-Board.
 */
public record MatchUpResponseDTO(Integer matchUpId, MatchUpSpielerDTO spielerEins,
                                 MatchUpSpielerDTO spielerZwei, MatchUpSpielerDTO gewinner) {
}
