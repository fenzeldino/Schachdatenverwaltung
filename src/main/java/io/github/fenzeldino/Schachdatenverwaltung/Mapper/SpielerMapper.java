package io.github.fenzeldino.Schachdatenverwaltung.Mapper;

import io.github.fenzeldino.Schachdatenverwaltung.DTO.SpielerDTO;
import io.github.fenzeldino.Schachdatenverwaltung.Model.Spieler;
import io.github.fenzeldino.Schachdatenverwaltung.Model.Turnier;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Konvertiert zwischen Spieler und SpielerDto
 */

public final class SpielerMapper {

    SpielerMapper(){

    }

    public static SpielerDTO toDto(Spieler spieler){
        if(spieler == null){
            return null;
        }
        Set<Integer> TurnierIds = spieler.getTurnier()
                .stream()
                .map(Turnier::getTunierId)
                .collect(Collectors.toSet());

        return new SpielerDTO(
               spieler.getSpielerId(),
               spieler.getName(),
               spieler.getRating(),
               TurnierIds
        );
    }

}
