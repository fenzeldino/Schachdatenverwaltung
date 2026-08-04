package io.github.fenzeldino.schachdatenverwaltung.controllerTest;

import io.github.fenzeldino.schachdatenverwaltung.controller.SpielerController;
import io.github.fenzeldino.schachdatenverwaltung.dto.request.spieler.SpielerCreateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.request.spieler.SpielerUpdateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.spieler.SpielerResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.service.SpielerService;
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
class SpielerControllerTest {

    @Mock
    private SpielerService spielerService;

    @InjectMocks
    private SpielerController spielerController;

    @Test
    void create_shouldReturnCreatedSpieler() {
        SpielerCreateDTO createDto = new SpielerCreateDTO("Max Mustermann", 2300.00, 23, List.of(1));
        SpielerResponseDTO responseDto = new SpielerResponseDTO(1, "Max Mustermann", 2300.00, List.of(1));

        when(spielerService.createSpieler(createDto)).thenReturn(responseDto);

        ResponseEntity<SpielerResponseDTO> result = spielerController.create(createDto);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(responseDto, result.getBody());
        verify(spielerService).createSpieler(createDto);
    }

    @Test
    void getAllSpieler_shouldReturnList() {
        List<SpielerResponseDTO> spieler = List.of(
                new SpielerResponseDTO(1, "Max Mustermann", 2300.00, List.of(1)),
                new SpielerResponseDTO(2, "Domi Mustermann", 2000.00, List.of(1))
        );

        when(spielerService.getAllSpieler()).thenReturn(spieler);

        ResponseEntity<List<SpielerResponseDTO>> result = spielerController.getAllSpieler();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(2, result.getBody().size());
        verify(spielerService).getAllSpieler();
    }

    @Test
    void getSpieler_shouldReturnSingleSpieler() {
        SpielerResponseDTO responseDto = new SpielerResponseDTO(1, "Max Mustermann", 2300.00, List.of(1));

        when(spielerService.getSpieler(1)).thenReturn(responseDto);

        SpielerResponseDTO result = spielerController.getSpieler(1);

        assertEquals(responseDto, result);
        verify(spielerService).getSpieler(1);
    }

    @Test
    void updateSpieler_shouldReturnUpdatedSpieler() {
        SpielerUpdateDTO updateDto = new SpielerUpdateDTO(1, "Mai Mustermann", 2200.00, 21, List.of(1));
        SpielerResponseDTO responseDto = new SpielerResponseDTO(1, "Mai Mustermann", 2200.00, List.of(1));

        when(spielerService.updateSpieler(1, updateDto)).thenReturn(responseDto);

        ResponseEntity<SpielerResponseDTO> result = spielerController.updateSpieler(updateDto, 1);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(responseDto, result.getBody());
        verify(spielerService).updateSpieler(1, updateDto);
    }

    @Test
    void deleteSpieler_shouldReturnNoContent() {
        ResponseEntity<Void> result = spielerController.deleteSpieler(1);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(spielerService).deleteSpieler(1);
    }
}
