package io.github.fenzeldino.Schachdatenverwaltung.DTO.Response.MatchUp;

import io.github.fenzeldino.Schachdatenverwaltung.Model.Spieler;

public record MatchUpResponseDTO(Spieler spielerEins, Spieler spielerZwei) {
}
