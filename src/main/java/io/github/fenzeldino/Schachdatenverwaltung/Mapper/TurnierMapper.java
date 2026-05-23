package io.github.fenzeldino.Schachdatenverwaltung.Mapper;

import io.github.fenzeldino.Schachdatenverwaltung.DTO.TurnierDTO;
import io.github.fenzeldino.Schachdatenverwaltung.Model.MatchUp;
import io.github.fenzeldino.Schachdatenverwaltung.Model.Spieler;
import io.github.fenzeldino.Schachdatenverwaltung.Model.Turnier;

import java.util.Set;
import java.util.stream.Collectors;

public class TurnierMapper {

    public TurnierMapper(){

    }

    public static TurnierDTO toDto(Turnier turnier){
        if(turnier == null){
            return null;
        }

        Set<Integer> spielerIds = turnier.getSpieler()
                .stream()
                .map(Spieler::getSpielerId)
                .collect(Collectors.toSet());

        Set<Integer> matchUpIds = turnier.getMatchups()
                .stream()
                .map(MatchUp::getMatchUpId)
                .collect(Collectors.toSet());

        return new TurnierDTO(
                turnier.getTunierId(),
                spielerIds,
                matchUpIds
        );

    }


}
