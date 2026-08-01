package io.github.fenzeldino.schachdatenverwaltung.dto.request.spieler;

import java.util.List;

public record SpielerUpdateDTO(Integer spielerId,
                               String Name,
                               Double rating,
                               Integer alter,
                               List<Integer> turnierIds) {
}
