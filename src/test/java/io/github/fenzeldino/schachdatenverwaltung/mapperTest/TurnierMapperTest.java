package io.github.fenzeldino.schachdatenverwaltung.mapperTest;

import io.github.fenzeldino.schachdatenverwaltung.dto.response.turnier.TurnierResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.mapper.TurnierMapper;
import io.github.fenzeldino.schachdatenverwaltung.model.MatchUp;
import io.github.fenzeldino.schachdatenverwaltung.model.Spieler;
import io.github.fenzeldino.schachdatenverwaltung.model.Turnier;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TurnierMapperTest {

    @Test
    void toDto_shouldReturnNull_WhenTurnierIsNull() {
        assertNull(TurnierMapper.toDto(null));
    }

    @Test
    void toDto_shouldMapSpielerAndMatchUpIds() {
        Turnier turnier = new Turnier(1);

        Spieler spieler1 = new Spieler(1, "Max Mustermann", 2300.00, 23, new ArrayList<>());
        Spieler spieler2 = new Spieler(2, "Domi Mustermann", 2000.00, 23, new ArrayList<>());
        turnier.setSpieler(List.of(spieler1, spieler2));

        MatchUp matchUp = new MatchUp(spieler1, spieler2);
        matchUp.setMatchUpId(8);
        turnier.setMatchups(matchUp);

        TurnierResponseDTO result = TurnierMapper.toDto(turnier);

        assertEquals(1, result.turnierId());
        assertEquals(Set.of(1, 2), result.spielerIds());
        assertEquals(Set.of(8), result.matchIds());
    }
}
