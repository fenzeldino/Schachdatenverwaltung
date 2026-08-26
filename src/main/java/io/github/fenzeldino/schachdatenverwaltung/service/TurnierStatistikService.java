package io.github.fenzeldino.schachdatenverwaltung.service;

import io.github.fenzeldino.schachdatenverwaltung.model.Spieler;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TurnierStatistikService {

    public record TurnierStatistik(int vereinsAnzahl, Double durchschnittsRating) {
    }

    public TurnierStatistik berechne(List<Spieler> teilnehmer) {
        int vereinsAnzahl = (int) teilnehmer.stream()
                .filter(spieler -> spieler.getVerein() != null)
                .map(spieler -> spieler.getVerein().getVereinId())
                .distinct()
                .count();

        Double durchschnittsRating = teilnehmer.isEmpty()
                ? null
                : teilnehmer.stream().mapToDouble(Spieler::getRating).average().orElseThrow();

        return new TurnierStatistik(vereinsAnzahl, durchschnittsRating);
    }
}
