package io.github.fenzeldino.schachdatenverwaltung.mapper;

import io.github.fenzeldino.schachdatenverwaltung.dto.response.turnier.TurnierResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.model.MatchUp;
import io.github.fenzeldino.schachdatenverwaltung.model.Spieler;
import io.github.fenzeldino.schachdatenverwaltung.model.Turnier;

import java.util.Set;
import java.util.stream.Collectors;

public class TurnierMapper {

    public TurnierMapper(){

    }

    public static TurnierResponseDTO toDto(Turnier turnier){
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

        return new TurnierResponseDTO(
                turnier.getTunierId(),
                spielerIds,
                matchUpIds
        );

    }


}
