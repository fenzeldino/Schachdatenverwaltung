package io.github.fenzeldino.schachdatenverwaltung.dto.response.matchUp;

/**
 * Schlanke Spieler-Projektion für den Gebrauch innerhalb einer
 * {@link MatchUpResponseDTO}.
 *
 * Bewusst NICHT die Spieler-Entity direkt eingebettet (wie es vor diesem
 * Fund der Fall war): Spieler hat bidirektionale Beziehungen
 * (Spieler.turnier ↔ Turnier.Spieler, Spieler.verein ↔ Verein.spieler).
 * Eine direkt eingebettete Entity serialisiert Jackson rekursiv über diese
 * Zyklen — ein einzelnes MatchUp mit zwei minimalen Spielern erzeugte dabei
 * bereits 13 KB tief verschachteltes JSON. Dieses DTO kappt den Graphen an
 * der Stelle, die für eine MatchUp-Anzeige tatsächlich gebraucht wird.
 */
public record MatchUpSpielerDTO(Integer id, String name, Double rating) {
}
