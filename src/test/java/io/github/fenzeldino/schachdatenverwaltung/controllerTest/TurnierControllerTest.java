package io.github.fenzeldino.schachdatenverwaltung.controllerTest;

import io.github.fenzeldino.schachdatenverwaltung.controller.TurnierController;
import io.github.fenzeldino.schachdatenverwaltung.dto.request.turnier.TurnierCreateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.request.turnier.TurnierUpdateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.matchUp.MatchUpResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.spieler.SpielerResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.turnier.TurnierResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.service.TurnierService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TurnierControllerTest {

    @Mock
    private TurnierService turnierService;

    @InjectMocks
    private TurnierController turnierController;

    @Test
    void create_shouldReturnCreatedTurnier() {
        TurnierCreateDTO createDto = new TurnierCreateDTO(List.of(1, 2));
        TurnierResponseDTO responseDto = new TurnierResponseDTO(1, Set.of(1, 2), Set.of());

        when(turnierService.createTurnier(createDto)).thenReturn(responseDto);

        ResponseEntity<TurnierResponseDTO> result = turnierController.create(createDto);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(responseDto, result.getBody());
        verify(turnierService).createTurnier(createDto);
    }

    @Test
    void getAllTurniere_shouldReturnListOfTurniere() {
        List<TurnierResponseDTO> turniere = List.of(
                new TurnierResponseDTO(1, Set.of(), Set.of()),
                new TurnierResponseDTO(2, Set.of(), Set.of())
        );

        when(turnierService.getAllTurniere()).thenReturn(turniere);

        ResponseEntity<List<TurnierResponseDTO>> result = turnierController.getAllTurniere();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(2, result.getBody().size());
        verify(turnierService).getAllTurniere();
    }

    @Test
    void getTurnier_shouldReturnSingleTurnier() {
        TurnierResponseDTO responseDto = new TurnierResponseDTO(1, Set.of(1), Set.of());

        when(turnierService.getTurnier(1)).thenReturn(responseDto);

        TurnierResponseDTO result = turnierController.getTurnier(1);

        assertEquals(responseDto, result);
        verify(turnierService).getTurnier(1);
    }

    @Test
    void updateTurnier_shouldReturnUpdatedTurnier() {
        TurnierUpdateDTO updateDto = new TurnierUpdateDTO(1, List.of(3));
        TurnierResponseDTO responseDto = new TurnierResponseDTO(1, Set.of(3), Set.of());

        when(turnierService.updateTurnier(1, updateDto)).thenReturn(responseDto);

        ResponseEntity<TurnierResponseDTO> result = turnierController.updateTurnier(updateDto, 1);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(responseDto, result.getBody());
        verify(turnierService).updateTurnier(1, updateDto);
    }

    @Test
    void deleteTurnier_shouldReturnNoContent() {
        ResponseEntity<Void> result = turnierController.deleteTurnier(1);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(turnierService).deleteTurnier(1);
    }

    @Test
    void showAllTurnierSpieler_shouldReturnFilteredSpieler() {
        List<SpielerResponseDTO> spieler = List.of(
                new SpielerResponseDTO(1, "Max Mustermann", 2300.00, List.of(1))
        );

        when(turnierService.showAllTurnierSpieler(1, Set.of(1))).thenReturn(spieler);

        ResponseEntity<List<SpielerResponseDTO>> result = turnierController.showAllTurnierSpieler(1, Set.of(1));

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        verify(turnierService).showAllTurnierSpieler(1, Set.of(1));
    }

    @Test
    void showAllMatchUps_shouldReturnFilteredMatchUps() {
        List<MatchUpResponseDTO> matchUps = List.of();

        when(turnierService.showAllMatchUps(1, Set.of(8))).thenReturn(matchUps);

        ResponseEntity<List<MatchUpResponseDTO>> result = turnierController.showAllMatchUps(1, Set.of(8));

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(turnierService).showAllMatchUps(1, Set.of(8));
    }

    @Test
    void addSpielerToTurnier_shouldReturnNoContent() {
        ResponseEntity<Void> result = turnierController.addSpielerToTurnier(1, 12);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(turnierService).addSpielerToTurnier(1, 12);
    }

    @Test
    void addMatchUpToTurnier_shouldReturnNoContent() {
        ResponseEntity<Void> result = turnierController.addMatchUpToTurnier(1, 8);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(turnierService).addMatchUpToTurnier(1, 8);
    }

    @Test
    void addMatchUpToDB_shouldReturnNoContent() {
        ResponseEntity<Void> result = turnierController.addMatchUpToDB(1, 12, 13);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(turnierService).addMatchUpToDB(1, 12, 13);
    }

    @Test
    void calculateDresden_shouldReturnNoContent() {
        ResponseEntity<Void> result = turnierController.calculateDresden(1, 8);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(turnierService).DresdenCalculator(1, 8);
    }

    @Test
    void calculateElo_shouldReturnNoContent() {
        ResponseEntity<Void> result = turnierController.calculateElo(1, 8);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(turnierService).EloBerehcnung(1, 8);
    }
}
