package io.github.fenzeldino.schachdatenverwaltung.dto.response.spieler;


import java.util.List;

public record SpielerResponseDTO(Integer id,
                                 String name,
                                 Double rating,
                                 List<Integer> TurnierIds) {
}
