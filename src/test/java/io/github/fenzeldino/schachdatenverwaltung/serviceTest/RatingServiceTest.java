package io.github.fenzeldino.schachdatenverwaltung.serviceTest;

import io.github.fenzeldino.schachdatenverwaltung.model.RatingResult;
import io.github.fenzeldino.schachdatenverwaltung.service.RatingService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Reine Unit-Tests der Rating-Mathematik, ohne Mockito: RatingService hat
 * keine Abhängigkeiten (kein Repository-Zugriff), also kann man die
 * Formeln direkt gegen unabhängig berechnete Erwartungswerte prüfen.
 *
 * Gewinner-Rating bleibt in den dresden()-Fällen konstant bei 1700 (Alter
 * 25) — das liegt in der DWZ-Matrix im Band 1601-1800 / 21-35 Jahre, was
 * einen festen Faktor von 1.0 ergibt (siehe DwzMatrix). So bleibt bei
 * variierendem Verlierer-Rating nur die diff-abhängige PUNKTE_TABELLE-Zeile
 * als Variable übrig.
 */
class RatingServiceTest {

    private final RatingService ratingService = new RatingService();

    @Test
    void dresden_favoritensieg_zeile0_diffBis50() {
        RatingResult result = ratingService.dresden(1700, 25, 1700); // diff=0

        // Zeile "0-50": Favoritensieg -> Basispunkte 5, Faktor 1.0
        assertEquals(1705.0, result.neuesGewinnerRating(), 1e-9);
        assertEquals(1695.0, result.neuesVerliererRating(), 1e-9);
    }

    @Test
    void dresden_favoritensieg_zeile1_diffBis100() {
        RatingResult result = ratingService.dresden(1700, 25, 1625); // diff=75

        // Zeile "51-100": Favoritensieg -> Basispunkte 6, Faktor 1.0
        assertEquals(1706.0, result.neuesGewinnerRating(), 1e-9);
        assertEquals(1619.0, result.neuesVerliererRating(), 1e-9);
    }

    @Test
    void dresden_favoritensieg_zeile2_diffBis150() {
        RatingResult result = ratingService.dresden(1700, 25, 1575); // diff=125

        // Zeile "101-200": Favoritensieg -> Basispunkte 7, Faktor 1.0
        assertEquals(1707.0, result.neuesGewinnerRating(), 1e-9);
        assertEquals(1568.0, result.neuesVerliererRating(), 1e-9);
    }

    @Test
    void dresden_favoritensieg_zeile3_diffUeber150() {
        RatingResult result = ratingService.dresden(1700, 25, 1500); // diff=200

        // Zeile ">200": Favoritensieg -> Basispunkte 8, Faktor 1.0
        assertEquals(1708.0, result.neuesGewinnerRating(), 1e-9);
        assertEquals(1492.0, result.neuesVerliererRating(), 1e-9);
    }

    @Test
    void dresden_aussenseitersieg_gibtKeinenPunktZuwachs() {
        // Die Außenseiter-Spalte (Spalte 2) der PUNKTE_TABELLE ist in allen
        // vier Zeilen 0 -- bestehendes Verhalten der Formel, hier bewusst
        // dokumentiert statt "repariert" (kein Formel-Change in diesem Plan).
        RatingResult result = ratingService.dresden(1500, 25, 1700); // Gewinner war Außenseiter

        assertEquals(1500.0, result.neuesGewinnerRating(), 1e-9);
        assertEquals(1700.0, result.neuesVerliererRating(), 1e-9);
    }

    @Test
    void elo_gleichesRating_gibtSymmetrischenZuwachs() {
        RatingResult result = ratingService.elo(1500, 1500);

        // Ea = 0.5 -> Zuwachs = 20 * (1 - 0.5) = 10
        assertEquals(1510.0, result.neuesGewinnerRating(), 1e-9);
        assertEquals(1490.0, result.neuesVerliererRating(), 1e-9);
    }

    @Test
    void elo_aussenseitersieg_gibtGroesserenZuwachs() {
        RatingResult result = ratingService.elo(1200, 1600); // Diff = 400

        // Ea = 1 / (1 + 10^(400/400)) = 1/11 -> Zuwachs = 20 * (10/11) = 200/11
        double erwarteterZuwachs = 200.0 / 11.0;
        assertEquals(1200 + erwarteterZuwachs, result.neuesGewinnerRating(), 1e-9);
        assertEquals(1600 - erwarteterZuwachs, result.neuesVerliererRating(), 1e-9);
    }
}
