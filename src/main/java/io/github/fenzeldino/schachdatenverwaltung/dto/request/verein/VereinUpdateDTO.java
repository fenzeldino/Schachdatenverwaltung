package io.github.fenzeldino.schachdatenverwaltung.dto.request.verein;

public record VereinUpdateDTO(Integer vereinId,
                              String name,
                              String zpsCode) {
}
