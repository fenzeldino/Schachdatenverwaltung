package io.github.fenzeldino.Schachdatenverwaltung.DTO.Response.Spieler;


import java.util.List;
import java.util.Set;

public record SpielerResponseDTO(Integer id,
                                 String name,
                                 Double rating,
                                 List<Integer> TurnierIds) {
}
