package io.github.fenzeldino.schachdatenverwaltung.mapperTest;

import io.github.fenzeldino.schachdatenverwaltung.dto.request.matchUp.MatchUpCreateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.matchUp.MatchUpResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.mapper.MatchUpMapper;
import io.github.fenzeldino.schachdatenverwaltung.model.MatchUp;
import io.github.fenzeldino.schachdatenverwaltung.model.Spieler;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MatchUpMapperTest {

    @Test
    void toDto_shouldReturnNull_WhenMatchUpIsNull() {
        assertNull(MatchUpMapper.toDto(null));
    }

    @Test
    void toDto_shouldMapSpielerFields() {
        Spieler spieler1 = new Spieler(1, "Max Mustermann", 2300.00, 23, new ArrayList<>());
        Spieler spieler2 = new Spieler(2, "Domi Mustermann", 2000.00, 23, new ArrayList<>());
        MatchUp matchUp = new MatchUp(spieler1, spieler2);

        MatchUpResponseDTO result = MatchUpMapper.toDto(matchUp);

        assertEquals(spieler1.getSpielerId(), result.spielerEins().id());
        assertEquals(spieler1.getName(), result.spielerEins().name());
        assertEquals(spieler1.getRating(), result.spielerEins().rating());
        assertEquals(spieler2.getSpielerId(), result.spielerZwei().id());
        assertNull(result.gewinner());
    }

    @Test
    void toDto_shouldProjectSpielerFelder_undNichtDieVolleEntity() {
        // Regressionstest fuer den Rekursions-Fund: die volle Spieler-Entity
        // (mit Spieler.turnier) darf hier nie mehr direkt eingebettet sein.
        Spieler spieler1 = new Spieler(1, "Max Mustermann", 2300.00, 23, new ArrayList<>());
        Spieler spieler2 = new Spieler(2, "Domi Mustermann", 2000.00, 23, new ArrayList<>());
        MatchUp matchUp = new MatchUp(spieler1, spieler2);

        MatchUpResponseDTO result = MatchUpMapper.toDto(matchUp);

        assertEquals(io.github.fenzeldino.schachdatenverwaltung.dto.response.matchUp.MatchUpSpielerDTO.class,
                result.spielerEins().getClass());
    }

    @Test
    void toDto_shouldMapMatchUpIdUndGewinner() {
        Spieler spieler1 = new Spieler(1, "Max Mustermann", 2300.00, 23, new ArrayList<>());
        Spieler spieler2 = new Spieler(2, "Domi Mustermann", 2000.00, 23, new ArrayList<>());
        MatchUp matchUp = new MatchUp(spieler1, spieler2);
        matchUp.setMatchUpId(8);
        matchUp.setGewinner(spieler1);

        MatchUpResponseDTO result = MatchUpMapper.toDto(matchUp);

        assertEquals(8, result.matchUpId());
        assertEquals(spieler1.getSpielerId(), result.gewinner().id());
        assertEquals(spieler1.getName(), result.gewinner().name());
    }

    @Test
    void toEntity_shouldMapFieldsFromCreateDto() {
        Spieler spieler1 = new Spieler(1, "Max Mustermann", 2300.00, 23, new ArrayList<>());
        Spieler spieler2 = new Spieler(2, "Domi Mustermann", 2000.00, 23, new ArrayList<>());
        MatchUpCreateDTO createDto = new MatchUpCreateDTO(spieler1, spieler2, 1);

        MatchUp result = MatchUpMapper.toEntity(createDto);

        assertEquals(spieler1, result.getSpieler1());
        assertEquals(spieler2, result.getSpieler2());
    }
}
