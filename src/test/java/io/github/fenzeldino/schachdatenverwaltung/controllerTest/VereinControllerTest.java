package io.github.fenzeldino.schachdatenverwaltung.controllerTest;

import io.github.fenzeldino.schachdatenverwaltung.controller.VereinController;
import io.github.fenzeldino.schachdatenverwaltung.dto.request.verein.VereinCreateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.request.verein.VereinUpdateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.spieler.SpielerResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.verein.VereinResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.service.VereinService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VereinControllerTest {

    @Mock
    private VereinService vereinService;

    @InjectMocks
    private VereinController vereinController;

    @Test
    void create_shouldReturnCreatedVerein() {
        VereinCreateDTO createDto = new VereinCreateDTO("SC Dresden 1920", "C0327");
        VereinResponseDTO responseDto = new VereinResponseDTO(1, "SC Dresden 1920", "C0327", 0);

        when(vereinService.createVerein(createDto)).thenReturn(responseDto);

        ResponseEntity<VereinResponseDTO> result = vereinController.create(createDto);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(responseDto, result.getBody());
        verify(vereinService).createVerein(createDto);
    }

    @Test
    void getAllVereine_shouldReturnList() {
        List<VereinResponseDTO> vereine = List.of(
                new VereinResponseDTO(1, "SC Dresden 1920", "C0327", 2),
                new VereinResponseDTO(2, "SG Leipzig", "C0456", 1)
        );
        when(vereinService.getAllVereine()).thenReturn(vereine);

        ResponseEntity<List<VereinResponseDTO>> result = vereinController.getAllVereine();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(2, result.getBody().size());
        verify(vereinService).getAllVereine();
    }

    @Test
    void getVerein_shouldReturnSingleVerein() {
        VereinResponseDTO responseDto = new VereinResponseDTO(1, "SC Dresden 1920", "C0327", 2);
        when(vereinService.getVerein(1)).thenReturn(responseDto);

        ResponseEntity<VereinResponseDTO> result = vereinController.getVerein(1);

        assertEquals(responseDto, result.getBody());
        verify(vereinService).getVerein(1);
    }

    @Test
    void getSpielerImVerein_shouldReturnSpielerList() {
        List<SpielerResponseDTO> spieler = List.of(
                new SpielerResponseDTO(1, "Max Mustermann", 1850.00, List.of(1), 1, "SC Dresden 1920"),
                new SpielerResponseDTO(2, "Erika Musterfrau", 1670.00, List.of(), 1, "SC Dresden 1920")
        );
        when(vereinService.getSpielerImVerein(1)).thenReturn(spieler);

        ResponseEntity<List<SpielerResponseDTO>> result = vereinController.getSpielerImVerein(1);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(2, result.getBody().size());
        assertEquals("SC Dresden 1920", result.getBody().get(0).vereinName());
        verify(vereinService).getSpielerImVerein(1);
    }

    @Test
    void spielerZuweisen_shouldReturnUpdatedSpieler() {
        SpielerResponseDTO responseDto =
                new SpielerResponseDTO(5, "Max Mustermann", 1850.00, List.of(), 1, "SC Dresden 1920");
        when(vereinService.spielerZuweisen(1, 5)).thenReturn(responseDto);

        ResponseEntity<SpielerResponseDTO> result = vereinController.spielerZuweisen(1, 5);

        assertEquals(1, result.getBody().vereinId());
        verify(vereinService).spielerZuweisen(1, 5);
    }

    @Test
    void spielerEntfernen_shouldReturnSpielerWithoutVerein() {
        SpielerResponseDTO responseDto =
                new SpielerResponseDTO(5, "Max Mustermann", 1850.00, List.of(), null, null);
        when(vereinService.spielerEntfernen(5)).thenReturn(responseDto);

        ResponseEntity<SpielerResponseDTO> result = vereinController.spielerEntfernen(5);

        assertEquals(null, result.getBody().vereinId());
        verify(vereinService).spielerEntfernen(5);
    }

    @Test
    void updateVerein_shouldReturnUpdatedVerein() {
        VereinUpdateDTO updateDto = new VereinUpdateDTO(1, "Neuer Name", "C0999");
        VereinResponseDTO responseDto = new VereinResponseDTO(1, "Neuer Name", "C0999", 2);
        when(vereinService.updateVerein(1, updateDto)).thenReturn(responseDto);

        ResponseEntity<VereinResponseDTO> result = vereinController.updateVerein(updateDto, 1);

        assertEquals("Neuer Name", result.getBody().name());
        verify(vereinService).updateVerein(1, updateDto);
    }

    @Test
    void deleteVerein_shouldReturnNoContent() {
        ResponseEntity<Void> result = vereinController.deleteVerein(1);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(vereinService).deleteVerein(1);
    }
}
