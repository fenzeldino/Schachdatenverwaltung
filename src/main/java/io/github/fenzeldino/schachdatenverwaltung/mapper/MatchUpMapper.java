package io.github.fenzeldino.schachdatenverwaltung.mapper;

import io.github.fenzeldino.schachdatenverwaltung.dto.request.matchUp.MatchUpCreateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.matchUp.MatchUpResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.matchUp.MatchUpSpielerDTO;
import io.github.fenzeldino.schachdatenverwaltung.model.MatchUp;
import io.github.fenzeldino.schachdatenverwaltung.model.Spieler;

public final class MatchUpMapper {

    private MatchUpMapper(){
    }

    public static MatchUpResponseDTO toDto(MatchUp matchUp){

        if(matchUp == null){
            return null;
        }

      return new MatchUpResponseDTO(
              matchUp.getMatchUpId(),
              toSpielerDto(matchUp.getSpieler1()),
              toSpielerDto(matchUp.getSpieler2()),
              toSpielerDto(matchUp.getGewinner())
      );
    }

    /**
     * Projiziert nur die Felder, die eine MatchUp-Anzeige braucht — nicht die
     * volle Spieler-Entity (siehe Klassenkommentar auf MatchUpSpielerDTO:
     * Entity-Einbettung hätte über Spieler.turnier/Spieler.verein rekursiv
     * serialisiert).
     */
    private static MatchUpSpielerDTO toSpielerDto(Spieler spieler){
        if(spieler == null){
            return null;
        }
        return new MatchUpSpielerDTO(spieler.getSpielerId(), spieler.getName(), spieler.getRating());
    }

    public static MatchUp toEntity(MatchUpCreateDTO matchUpDTO){
        return new MatchUp(
                matchUpDTO.spielerEins(),
                matchUpDTO.spielerZwei()
        );
    }


}
