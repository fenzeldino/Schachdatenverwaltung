package io.github.fenzeldino.schachdatenverwaltung.dto.response.matchUp;

import io.github.fenzeldino.schachdatenverwaltung.model.Spieler;

public record MatchUpResponseDTO(Spieler spielerEins, Spieler spielerZwei) {
}
