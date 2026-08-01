package io.github.fenzeldino.schachdatenverwaltung.dto;

import io.github.fenzeldino.schachdatenverwaltung.model.Spieler;

public record MatchUpDTO(Spieler SpielerEins, Spieler SpielerZwei){
}
