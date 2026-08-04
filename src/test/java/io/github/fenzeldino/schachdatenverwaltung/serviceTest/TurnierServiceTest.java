package io.github.fenzeldino.schachdatenverwaltung.serviceTest;

import io.github.fenzeldino.schachdatenverwaltung.dto.request.turnier.TurnierCreateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.request.turnier.TurnierUpdateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.turnier.TurnierResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.model.MatchUp;
import io.github.fenzeldino.schachdatenverwaltung.model.Spieler;
import io.github.fenzeldino.schachdatenverwaltung.model.Turnier;
import io.github.fenzeldino.schachdatenverwaltung.repository.MatchUpRepository;
import io.github.fenzeldino.schachdatenverwaltung.repository.SpielerRepository;
import io.github.fenzeldino.schachdatenverwaltung.repository.TurnierRepository;
import io.github.fenzeldino.schachdatenverwaltung.service.TurnierService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TurnierServiceTest {

    @Mock
    private TurnierRepository turnierRepository;

    @Mock
    private SpielerRepository spielerRepository;

    @Mock
    private MatchUpRepository matchUpRepository;

    @InjectMocks
    private TurnierService turnierService;

    @Test
    void createTurnier_shouldCreateTurnierWithSpieler() {
        // Arrange
        TurnierCreateDTO createDto = new TurnierCreateDTO(List.of(1, 2));

        Spieler spieler1 = new Spieler(1, "Max Mustermann", 2300.00, 23, new ArrayList<>());
        Spieler spieler2 = new Spieler(2, "Domi Mustermann", 2000.00, 23, new ArrayList<>());
        List<Spieler> spielerListe = List.of(spieler1, spieler2);

        when(spielerRepository.findAllById(List.of(1, 2))).thenReturn(spielerListe);

        Turnier savedTurnier = new Turnier(5);
        savedTurnier.setSpieler(spielerListe);

        when(turnierRepository.save(any(Turnier.class))).thenReturn(savedTurnier);

        // Act
        TurnierResponseDTO result = turnierService.createTurnier(createDto);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.turnierId());
        assertEquals(Set.of(1, 2), result.spielerIds());

        verify(spielerRepository).findAllById(List.of(1, 2));
        verify(turnierRepository).save(argThat(turnier -> turnier.getSpieler().equals(spielerListe)));
    }

    @Test
    void createTurnier_shouldReturnNull_WhenDtoIsNull() {
        TurnierResponseDTO result = turnierService.createTurnier(null);

        assertNull(result);
        verify(turnierRepository, never()).save(any());
    }

    @Test
    void getAllTurniere_shouldReturnAllTurniere() {
        Turnier turnier1 = new Turnier(1);
        Turnier turnier2 = new Turnier(2);

        when(turnierRepository.findAll()).thenReturn(List.of(turnier1, turnier2));

        List<TurnierResponseDTO> result = turnierService.getAllTurniere();

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).turnierId());
        assertEquals(2, result.get(1).turnierId());
        verify(turnierRepository).findAll();
    }

    @Test
    void getTurnier_shouldReturnTurnier_WhenIdExists() {
        Turnier turnier1 = new Turnier(1);

        when(turnierRepository.findById(1)).thenReturn(Optional.of(turnier1));

        TurnierResponseDTO result = turnierService.getTurnier(1);

        assertEquals(1, result.turnierId());
        verify(turnierRepository).findById(1);
    }

    @Test
    void getTurnier_shouldThrowException_WhenIdNotFound() {
        when(turnierRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> turnierService.getTurnier(999));

        verify(turnierRepository).findById(999);
    }

    @Test
    void updateTurnier_shouldUpdateSpielerListe() {
        Turnier existing = new Turnier(1);
        Spieler spielerNeu = new Spieler(3, "Nev Mustermann", 2100.00, 20, new ArrayList<>());

        TurnierUpdateDTO updateDto = new TurnierUpdateDTO(1, List.of(3));

        when(turnierRepository.findById(1)).thenReturn(Optional.of(existing));
        when(spielerRepository.findAllById(List.of(3))).thenReturn(List.of(spielerNeu));
        when(turnierRepository.save(existing)).thenReturn(existing);

        TurnierResponseDTO result = turnierService.updateTurnier(1, updateDto);

        assertEquals(List.of(spielerNeu), existing.getSpieler());
        assertEquals(Set.of(3), result.spielerIds());
        verify(turnierRepository).findById(1);
        verify(spielerRepository).findAllById(List.of(3));
        verify(turnierRepository).save(existing);
    }

    @Test
    void updateTurnier_shouldReturnNull_WhenIdsDoNotMatch() {
        Turnier existing = new Turnier(1);
        TurnierUpdateDTO updateDto = new TurnierUpdateDTO(2, List.of(3));

        when(turnierRepository.findById(1)).thenReturn(Optional.of(existing));

        TurnierResponseDTO result = turnierService.updateTurnier(1, updateDto);

        assertNull(result);
        verify(turnierRepository, never()).save(any());
    }

    @Test
    void deleteTurnier_shouldDelete_WhenExists() {
        when(turnierRepository.existsById(1)).thenReturn(true);

        turnierService.deleteTurnier(1);

        verify(turnierRepository).deleteById(1);
    }

    @Test
    void deleteTurnier_shouldThrowException_WhenNotFound() {
        when(turnierRepository.existsById(999)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> turnierService.deleteTurnier(999));

        verify(turnierRepository, never()).deleteById(anyInt());
    }

    @Test
    void addSpielerToTurnier_byId_shouldAddSpielerToTurnier() {
        Turnier turnier = new Turnier(1);
        Spieler spieler = new Spieler(3, "Nev Mustermann", 2100.00, 20, new ArrayList<>());

        when(spielerRepository.findById(3)).thenReturn(Optional.of(spieler));
        when(turnierRepository.findById(1)).thenReturn(Optional.of(turnier));

        turnierService.addSpielerToTurnier(1, 3);

        assertTrue(turnier.getSpieler().contains(spieler));
        verify(spielerRepository).findById(3);
        verify(turnierRepository).save(turnier);
    }

    @Test
    void addMatchUpToTurnier_byId_shouldLinkExistingMatchUp() {
        Turnier turnier = new Turnier(1);
        Spieler spieler1 = new Spieler(1, "Max Mustermann", 2300.00, 23, new ArrayList<>());
        Spieler spieler2 = new Spieler(2, "Domi Mustermann", 2000.00, 23, new ArrayList<>());
        MatchUp matchUp = new MatchUp(spieler1, spieler2);
        matchUp.setMatchUpId(8);

        when(matchUpRepository.findById(8)).thenReturn(Optional.of(matchUp));
        when(turnierRepository.findById(1)).thenReturn(Optional.of(turnier));

        turnierService.addMatchUpToTurnier(1, 8);

        assertTrue(turnier.getMatchups().contains(matchUp));
        verify(matchUpRepository).findById(8);
        verify(turnierRepository).save(turnier);
    }

    @Test
    void addMatchUpToDB_byId_shouldCreateNewMatchUpBetweenSpieler() {
        Turnier turnier = new Turnier(5);
        Spieler spieler1 = new Spieler(1, "Max Mustermann", 2300.00, 23, new ArrayList<>());
        Spieler spieler2 = new Spieler(2, "Domi Mustermann", 2000.00, 23, new ArrayList<>());

        when(spielerRepository.findById(1)).thenReturn(Optional.of(spieler1));
        when(spielerRepository.findById(2)).thenReturn(Optional.of(spieler2));
        when(turnierRepository.findById(5)).thenReturn(Optional.of(turnier));

        turnierService.addMatchUpToDB(5, 1, 2);

        assertEquals(1, turnier.getMatchups().size());
        assertEquals(spieler1, turnier.getMatchups().getFirst().getSpieler1());
        assertEquals(spieler2, turnier.getMatchups().getFirst().getSpieler2());
        verify(turnierRepository).save(turnier);
    }
}
