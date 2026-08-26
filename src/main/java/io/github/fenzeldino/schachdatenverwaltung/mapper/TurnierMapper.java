package io.github.fenzeldino.schachdatenverwaltung.mapper;

import io.github.fenzeldino.schachdatenverwaltung.dto.response.turnier.TurnierResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.model.MatchUp;
import io.github.fenzeldino.schachdatenverwaltung.model.Spieler;
import io.github.fenzeldino.schachdatenverwaltung.model.Turnier;
import io.github.fenzeldino.schachdatenverwaltung.service.TurnierStatistikService.TurnierStatistik;

import java.util.Set;
import java.util.stream.Collectors;

public class TurnierMapper {

    public TurnierMapper(){

    }

    public static TurnierResponseDTO toDto(Turnier turnier){
        return toDto(turnier, null);
    }

    public static TurnierResponseDTO toDto(Turnier turnier, TurnierStatistik statistik){
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
                turnier.getName(),
                turnier.getDatum(),
                turnier.getOrt(),
                turnier.getStatus(),
                spielerIds.size(),
                turnier.getMaxTeilnehmer(),
                statistik == null ? null : statistik.vereinsAnzahl(),
                statistik == null ? null : statistik.durchschnittsRating(),
                spielerIds,
                matchUpIds
        );

    }


}
