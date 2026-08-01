package io.github.fenzeldino.schachdatenverwaltung.dto.request.matchUp;

import io.github.fenzeldino.schachdatenverwaltung.model.Spieler;

public record MatchUpCreateDTO(Spieler spielerEins, Spieler spielerZwei,Integer turnierId) {
}
