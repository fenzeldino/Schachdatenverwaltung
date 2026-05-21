package io.github.fenzeldino.Schachdatenverwaltung.DTO;

import io.github.fenzeldino.Schachdatenverwaltung.Model.Spieler;

public record MatchUpDTO(Integer MatchUpId, Spieler SpielerEins, Spieler SpielerZwei){
}
