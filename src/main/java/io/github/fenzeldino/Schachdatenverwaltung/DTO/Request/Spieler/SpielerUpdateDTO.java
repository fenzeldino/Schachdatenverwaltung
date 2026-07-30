package io.github.fenzeldino.Schachdatenverwaltung.DTO.Request.Spieler;

import java.util.List;

public record SpielerUpdateDTO(Integer spielerId,
                               String Name,
                               Double rating,
                               Integer alter,
                               List<Integer> turnierIds) {
}
