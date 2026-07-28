package io.github.fenzeldino.Schachdatenverwaltung.Mapper;


import io.github.fenzeldino.Schachdatenverwaltung.DTO.Request.Spieler.SpielerCreateDTO;
import io.github.fenzeldino.Schachdatenverwaltung.DTO.Response.Spieler.SpielerResponseDTO;
import io.github.fenzeldino.Schachdatenverwaltung.Model.Spieler;
import io.github.fenzeldino.Schachdatenverwaltung.Model.Turnier;

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
            return null;
        }
        return new Spieler(spielerDTO.Name(),spielerDTO.rating(),spielerDTO.alter());
    }

}
