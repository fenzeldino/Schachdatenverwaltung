package io.github.fenzeldino.Schachdatenverwaltung.DTO;

import io.github.fenzeldino.Schachdatenverwaltung.Model.Spieler;

import java.util.List;

public record TurnierDTO(
        int TurnierId,
        List<Spieler> spieler,
        List<MatchUpDTO> matches
) {
}
