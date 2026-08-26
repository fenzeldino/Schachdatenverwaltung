package io.github.fenzeldino.schachdatenverwaltung.serviceTest;

import io.github.fenzeldino.schachdatenverwaltung.model.Spieler;
import io.github.fenzeldino.schachdatenverwaltung.model.Verein;
import io.github.fenzeldino.schachdatenverwaltung.service.TurnierStatistikService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TurnierStatistikServiceTest {

    private final TurnierStatistikService service = new TurnierStatistikService();

    @Test
    void berechne_shouldReturnZeroAndNullForEmptyList() {
        var statistik = service.berechne(List.of());

        assertEquals(0, statistik.vereinsAnzahl());
        assertNull(statistik.durchschnittsRating());
    }

    @Test
    void berechne_shouldCountDistinctVereineAndIgnoreMissingVerein() {
        Verein verein = new Verein("SC Dresden");
        verein.setVereinId(7);
        Spieler eins = spieler(1, 1800);
        Spieler zwei = spieler(2, 2000);
        Spieler ohneVerein = spieler(3, 1600);
        eins.setVerein(verein);
        zwei.setVerein(verein);

        var statistik = service.berechne(List.of(eins, zwei, ohneVerein));

        assertEquals(1, statistik.vereinsAnzahl());
        assertEquals(1800.0, statistik.durchschnittsRating());
    }

    private Spieler spieler(int id, double rating) {
        return new Spieler(id, "Spieler " + id, rating, 30, new ArrayList<>());
    }
}
