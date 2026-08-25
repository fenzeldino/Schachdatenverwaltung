package io.github.fenzeldino.schachdatenverwaltung.service;

import io.github.fenzeldino.schachdatenverwaltung.model.DwzMatrix;
import io.github.fenzeldino.schachdatenverwaltung.model.RatingResult;
import org.springframework.stereotype.Service;

/**
 * Reine Rating-Mathematik: keine Entities, kein Repository-Zugriff, keine
 * Seiteneffekte. Die einzelnen Aufrufer (TurnierService) laden MatchUp und
 * Spieler, rufen hier die Formel auf und wenden das Ergebnis selbst an.
 */
@Service
public class RatingService {

    private static final int[][] PUNKTE_TABELLE = {
            {5,2,0}, // 0-50 Punkte
            {6,3,0}, //51-100Punkte
            {7,4,0}, //101-200Punkte
            {8,5,0}  //>200 Punkte
    };

    /**
     * Rating-Berechnung nach Dresdner Methode. Das Alter fließt über die
     * DWZ-Matrix (Beschleunigungsfaktor) nur beim Gewinner ein.
     */
    public RatingResult dresden(double gewinnerRating, int gewinnerAlter, double verliererRating){
        double diff = Math.abs(gewinnerRating - verliererRating); //Differenz bestimmen für Punkte_Tabelle
        boolean gewinnerWarFavorit = gewinnerRating >= verliererRating; //Favorit bestimmen

        int zeile;
        if (diff <= 50) zeile = 0;
        else if (diff <= 100) zeile = 1;
        else if (diff <= 150) zeile = 2;
        else zeile = 3;

        // Spalte 0 = Favoritensieg, Spalte 2 = Außenseitersieg
        int spalte = gewinnerWarFavorit ? 0 : 2;
        int basisPunkte = PUNKTE_TABELLE[zeile][spalte];

        double faktor = DwzMatrix.getFactor((int) gewinnerRating, gewinnerAlter);

        double punktZuwachs = basisPunkte * faktor;

        double neuesGewinnerRating = gewinnerRating + punktZuwachs;
        double neuesVerliererRating = verliererRating - punktZuwachs; // Bei Dresden meist symmetrisch

        return new RatingResult(neuesGewinnerRating, neuesVerliererRating);
    }

    /**
     * Rating-Berechnung nach Elo. Es wird immer ein fester Gewinner
     * angenommen (Sa = 1.0) — für ein Remis müsste die MatchUp-Klasse ein
     * eigenes Status-Feld prüfen, das es aktuell nicht gibt.
     */
    public RatingResult elo(double gewinnerRating, double verliererRating){
        // Erwartungswert für den Gewinner: 1 / (1 + 10^((RatingVerlierer - RatingGewinner) / 400))
        double ea = 1.0 / (1.0 + Math.pow(10, (verliererRating - gewinnerRating) / 400.0));

        // Fixer K-Faktor von 20 als Standard
        int k = 20;

        double sa = 1.0;
        double punktZuwachs = k * (sa - ea);

        double neuesGewinnerRating = gewinnerRating + punktZuwachs;
        double neuesVerliererRating = verliererRating - punktZuwachs;

        return new RatingResult(neuesGewinnerRating, neuesVerliererRating);
    }
}
