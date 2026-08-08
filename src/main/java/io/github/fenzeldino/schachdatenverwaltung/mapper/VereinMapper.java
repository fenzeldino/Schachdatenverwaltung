package io.github.fenzeldino.schachdatenverwaltung.mapper;

import io.github.fenzeldino.schachdatenverwaltung.dto.request.verein.VereinCreateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.verein.VereinResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.model.Verein;

/**
 * Konvertiert zwischen Verein und den zugehörigen DTOs.
 */
public final class VereinMapper {

    private VereinMapper() {
    }

    public static VereinResponseDTO toDto(Verein verein) {
        if (verein == null) {
            return null;
        }

        return new VereinResponseDTO(
                verein.getVereinId(),
                verein.getName(),
                verein.getZpsCode(),
                verein.getSpieler() == null ? 0 : verein.getSpieler().size()
        );
    }

    public static Verein toEntity(VereinCreateDTO vereinDTO) {
        if (vereinDTO == null) {
            return null;
        }

        return new Verein(vereinDTO.name(), vereinDTO.zpsCode());
    }
}
