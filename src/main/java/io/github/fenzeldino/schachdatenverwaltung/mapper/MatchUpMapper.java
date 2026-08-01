package io.github.fenzeldino.schachdatenverwaltung.mapper;

import io.github.fenzeldino.schachdatenverwaltung.dto.request.matchUp.MatchUpCreateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.matchUp.MatchUpResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.model.MatchUp;

public final class MatchUpMapper {

    private MatchUpMapper(){
    }

    public static MatchUpResponseDTO toDto(MatchUp matchUp){

        if(matchUp == null){
            return null;
        }

      return new MatchUpResponseDTO(
              matchUp.getSpieler1(),
              matchUp.getSpieler2()
      );
    }

    public static MatchUp toEntity(MatchUpCreateDTO matchUpDTO){
        return new MatchUp(
                matchUpDTO.spielerEins(),
                matchUpDTO.spielerZwei()
        );
    }


}
