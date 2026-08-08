package io.github.fenzeldino.schachdatenverwaltung.mapperTest;

import io.github.fenzeldino.schachdatenverwaltung.dto.request.verein.VereinCreateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.verein.VereinResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.mapper.VereinMapper;
import io.github.fenzeldino.schachdatenverwaltung.model.Spieler;
import io.github.fenzeldino.schachdatenverwaltung.model.Verein;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VereinMapperTest {

    @Test
    void toDto_shouldReturnNull_WhenVereinIsNull() {
        assertNull(VereinMapper.toDto(null));
    }

    @Test
    void toDto_shouldMapVereinFields() {
        Verein verein = new Verein("SC Dresden 1920", "C0327");
        verein.setVereinId(3);

        VereinResponseDTO result = VereinMapper.toDto(verein);

        assertEquals(3, result.id());
        assertEquals("SC Dresden 1920", result.name());
        assertEquals("C0327", result.zpsCode());
    }

    @Test
    void toDto_shouldCountZugeordneteSpieler() {
        Verein verein = new Verein("SC Dresden 1920");
        verein.setSpieler(List.of(
                new Spieler("Max Mustermann", 1850.0, 23),
                new Spieler("Erika Musterfrau", 1670.0, 31)
        ));

        assertEquals(2, VereinMapper.toDto(verein).spielerAnzahl());
    }

    @Test
    void toDto_shouldReturnZero_WhenSpielerListIsNull() {
        Verein verein = new Verein("SG Leipzig");
        verein.setSpieler(null);

        assertEquals(0, VereinMapper.toDto(verein).spielerAnzahl());
    }

    @Test
    void toEntity_shouldReturnNull_WhenDtoIsNull() {
        assertNull(VereinMapper.toEntity(null));
    }

    @Test
    void toEntity_shouldMapNameAndZpsCode() {
        Verein result = VereinMapper.toEntity(new VereinCreateDTO("SG Leipzig", "C0456"));

        assertEquals("SG Leipzig", result.getName());
        assertEquals("C0456", result.getZpsCode());
    }
}
