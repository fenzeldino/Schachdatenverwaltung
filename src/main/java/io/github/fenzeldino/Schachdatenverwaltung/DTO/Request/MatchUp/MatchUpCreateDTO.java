package io.github.fenzeldino.Schachdatenverwaltung.DTO.Request.MatchUp;

import io.github.fenzeldino.Schachdatenverwaltung.Model.Spieler;

public record MatchUpCreateDTO(Spieler spielerEins, Spieler spielerZwei,Integer turnierId) {
}
