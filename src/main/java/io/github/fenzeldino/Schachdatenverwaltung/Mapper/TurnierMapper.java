package io.github.fenzeldino.Schachdatenverwaltung.Mapper;

import io.github.fenzeldino.Schachdatenverwaltung.DTO.TurnierDTO;
import io.github.fenzeldino.Schachdatenverwaltung.Model.MatchUp;
import io.github.fenzeldino.Schachdatenverwaltung.Model.Spieler;
import io.github.fenzeldino.Schachdatenverwaltung.Model.Turnier;

import java.util.Set;
import java.util.stream.Collectors;

public final class TurnierMapper {

    public TurnierMapper(){}

    public static TurnierDTO toDTO(Turnier turnier){
        if(turnier == null){
            return null;
        }

        Set<Integer> SpielerIds = turnier.getSpieler()
                .stream()
                .map(Spieler::getSpielerId)
                .collect(Collectors.toSet());

        Set<Integer> MatchIds = turnier.getMatchups()
                .stream()
                .map(MatchUp::getMatchUpId)
                .collect(Collectors.toSet());

        return new TurnierDTO(
                turnier.getTunierId(),
                SpielerIds,
                MatchIds
        );
    }
}
