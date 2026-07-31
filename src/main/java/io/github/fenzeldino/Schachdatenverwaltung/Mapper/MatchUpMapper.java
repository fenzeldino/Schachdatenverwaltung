package io.github.fenzeldino.Schachdatenverwaltung.Mapper;

import io.github.fenzeldino.Schachdatenverwaltung.DTO.MatchUpDTO;
import io.github.fenzeldino.Schachdatenverwaltung.DTO.Request.MatchUp.MatchUpCreateDTO;
import io.github.fenzeldino.Schachdatenverwaltung.DTO.Response.MatchUp.MatchUpResponseDTO;
import io.github.fenzeldino.Schachdatenverwaltung.Model.MatchUp;

import java.util.Set;

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
