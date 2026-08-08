package io.github.fenzeldino.schachdatenverwaltung.mapperTest;

import io.github.fenzeldino.schachdatenverwaltung.dto.request.spieler.SpielerCreateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.spieler.SpielerResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.mapper.SpielerMapper;
import io.github.fenzeldino.schachdatenverwaltung.model.Spieler;
import io.github.fenzeldino.schachdatenverwaltung.model.Turnier;
import io.github.fenzeldino.schachdatenverwaltung.model.Verein;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SpielerMapperTest {

    @Test
    void toDto_shouldReturnNull_WhenSpielerIsNull() {
        assertNull(SpielerMapper.toDto(null));
    }

    @Test
    void toDto_shouldMapSpielerFieldsAndTurnierIds() {
        Turnier turnier1 = new Turnier(1);
        Turnier turnier2 = new Turnier(2);

        Spieler spieler = new Spieler(
                7,
                "Max Mustermann",
                2300.00,
                23,
                List.of(turnier1, turnier2)
        );

        SpielerResponseDTO result = SpielerMapper.toDto(spieler);

        assertEquals(7, result.id());
        assertEquals("Max Mustermann", result.name());
        assertEquals(2300.00, result.rating());
        assertEquals(List.of(1, 2), result.TurnierIds());
    }

    @Test
    void toDto_shouldMapVerein_WhenSpielerHasOne() {
        Verein verein = new Verein("SC Dresden 1920", "C0327");
        verein.setVereinId(4);

        Spieler spieler = new Spieler("Max Mustermann", 2300.00, 23);
        spieler.setVerein(verein);

        SpielerResponseDTO result = SpielerMapper.toDto(spieler);

        assertEquals(4, result.vereinId());
        assertEquals("SC Dresden 1920", result.vereinName());
    }

    @Test
    void toDto_shouldLeaveVereinNull_WhenSpielerHasNone() {
        SpielerResponseDTO result = SpielerMapper.toDto(new Spieler("Max Mustermann", 2300.00, 23));

        assertNull(result.vereinId());
        assertNull(result.vereinName());
    }

    @Test
    void toEntity_shouldReturnNull_WhenDtoIsNull() {
        assertNull(SpielerMapper.toEntity(null));
    }

    @Test
    void toEntity_shouldMapFieldsFromCreateDto() {
        SpielerCreateDTO createDto = new SpielerCreateDTO("Max Mustermann", 2300.00, 23, List.of(1, 2));

        Spieler result = SpielerMapper.toEntity(createDto);

        assertEquals("Max Mustermann", result.getName());
        assertEquals(2300.00, result.getRating());
        assertEquals(23, result.getAge());
    }
}
