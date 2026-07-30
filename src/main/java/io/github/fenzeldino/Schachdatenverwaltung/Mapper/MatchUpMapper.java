package io.github.fenzeldino.Schachdatenverwaltung.Mapper;

import io.github.fenzeldino.Schachdatenverwaltung.DTO.MatchUpDTO;
import io.github.fenzeldino.Schachdatenverwaltung.Model.MatchUp;

import java.util.Set;

public final class MatchUpMapper {

    private MatchUpMapper(){
    }

    public static MatchUpDTO toDto(MatchUp matchUp){

        if(matchUp == null){
            return null;
        }

      return new MatchUpDTO(

              matchUp.getSpieler1(),
              matchUp.getSpieler2()
      );
    }


}
