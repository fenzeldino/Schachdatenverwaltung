package io.github.fenzeldino.schachdatenverwaltung.controllerTest;

import io.github.fenzeldino.schachdatenverwaltung.controller.MatchUpController;
import io.github.fenzeldino.schachdatenverwaltung.dto.request.matchUp.MatchUpCreateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.request.matchUp.MatchUpUpdateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.matchUp.MatchUpResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.model.Spieler;
import io.github.fenzeldino.schachdatenverwaltung.service.MatchUpService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchUpControllerTest {

    @Mock
    private MatchUpService matchUpService;

    @InjectMocks
    private MatchUpController matchUpController;

    @Test
    void create_shouldReturnCreatedMatchUp() {
        Spieler spieler1 = new Spieler(1, "Max Mustermann", 2300.00, 23, new ArrayList<>());
        Spieler spieler2 = new Spieler(2, "Domi Mustermann", 2000.00, 23, new ArrayList<>());

        MatchUpCreateDTO createDto = new MatchUpCreateDTO(spieler1, spieler2, 1);
        MatchUpResponseDTO responseDto = new MatchUpResponseDTO(spieler1, spieler2);

        when(matchUpService.createMatchUp(createDto)).thenReturn(responseDto);

        ResponseEntity<MatchUpResponseDTO> result = matchUpController.create(createDto);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(responseDto, result.getBody());
        verify(matchUpService).createMatchUp(createDto);
    }

    @Test
    void getAllMatchUps_shouldReturnList() {
        Spieler spieler1 = new Spieler(1, "Max Mustermann", 2300.00, 23, new ArrayList<>());
        Spieler spieler2 = new Spieler(2, "Domi Mustermann", 2000.00, 23, new ArrayList<>());
        List<MatchUpResponseDTO> matchUps = List.of(new MatchUpResponseDTO(spieler1, spieler2));

        when(matchUpService.getAllMatchUpsFromDb()).thenReturn(matchUps);

        ResponseEntity<List<MatchUpResponseDTO>> result = matchUpController.getAllMatchUps();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        verify(matchUpService).getAllMatchUpsFromDb();
    }

    @Test
    void updateMatchUp_shouldReturnUpdatedMatchUp() {
        Spieler spieler1 = new Spieler(1, "Max Mustermann", 2300.00, 23, new ArrayList<>());
        Spieler spieler2 = new Spieler(2, "Domi Mustermann", 2000.00, 23, new ArrayList<>());

        MatchUpUpdateDTO updateDto = new MatchUpUpdateDTO(1, 1, 2, 1, 1);
        MatchUpResponseDTO responseDto = new MatchUpResponseDTO(spieler1, spieler2);

        when(matchUpService.updateMatchUp(1, updateDto)).thenReturn(responseDto);

        ResponseEntity<MatchUpResponseDTO> result = matchUpController.updateMatchUp(updateDto, 1);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(responseDto, result.getBody());
        verify(matchUpService).updateMatchUp(1, updateDto);
    }

    @Test
    void deleteMatchUp_shouldReturnNoContent() {
        ResponseEntity<Void> result = matchUpController.deleteMatchUp(1);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(matchUpService).deleteMatchUpById(1);
    }

    @Test
    void addGewinner_shouldReturnNoContent() {
        ResponseEntity<Void> result = matchUpController.addGewinner(8, 1);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(matchUpService).addGewinner(8, 1);
    }

    @Test
    void addVerlierer_shouldReturnNoContent() {
        ResponseEntity<Void> result = matchUpController.addVerlierer(8, 2);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(matchUpService).addVerlierer(8, 2);
    }
}
