package io.github.fenzeldino.schachdatenverwaltung.mapper;


import io.github.fenzeldino.schachdatenverwaltung.dto.request.spieler.SpielerCreateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.spieler.SpielerResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.model.Spieler;
import io.github.fenzeldino.schachdatenverwaltung.model.Turnier;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Konvertiert zwischen Spieler und SpielerDto
 */

public final class SpielerMapper {

    private SpielerMapper(){

    }

    public static SpielerResponseDTO toDto(Spieler spieler){
        if(spieler == null){
            return null;
        }
        List<Integer> TurnierIds = spieler.getTurnier()
                .stream()
                .map(Turnier::getTunierId)
                .collect(Collectors.toList());

        return new SpielerResponseDTO(
               spieler.getSpielerId(),
               spieler.getName(),
               spieler.getRating(),
               TurnierIds
        );
    }


    public static Spieler toEntity(SpielerCreateDTO spielerDTO){
        if(spielerDTO == null){
            System.out.println("Leeres SpielerDto");
            return null;
        }
        return new Spieler(spielerDTO.Name(),spielerDTO.rating(),spielerDTO.alter());
    }

}
