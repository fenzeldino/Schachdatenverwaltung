package io.github.fenzeldino.schachdatenverwaltung.dto.response.verein;

public record VereinResponseDTO(Integer id,
                                String name,
                                String zpsCode,
                                Integer spielerAnzahl) {
}
