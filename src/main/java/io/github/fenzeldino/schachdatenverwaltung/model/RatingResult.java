package io.github.fenzeldino.schachdatenverwaltung.model;

/**
 * Ergebnis einer Rating-Berechnung (Dresden oder Elo): die neuen Ratings
 * für Gewinner und Verlierer eines MatchUps.
 */
public record RatingResult(double neuesGewinnerRating, double neuesVerliererRating) {
}
