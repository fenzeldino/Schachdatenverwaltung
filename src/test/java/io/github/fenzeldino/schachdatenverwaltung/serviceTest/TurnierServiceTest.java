package io.github.fenzeldino.schachdatenverwaltung.serviceTest;

import io.github.fenzeldino.schachdatenverwaltung.dto.request.turnier.TurnierCreateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.request.turnier.TurnierUpdateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.turnier.TurnierResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.turnier.VereinImTurnierDTO;
import io.github.fenzeldino.schachdatenverwaltung.exception.InvalidRequestException;
import io.github.fenzeldino.schachdatenverwaltung.exception.ResourceNotFoundException;
import io.github.fenzeldino.schachdatenverwaltung.model.MatchUp;
import io.github.fenzeldino.schachdatenverwaltung.model.RatingResult;
import io.github.fenzeldino.schachdatenverwaltung.model.Spieler;
import io.github.fenzeldino.schachdatenverwaltung.model.Turnier;
import io.github.fenzeldino.schachdatenverwaltung.model.TurnierStatus;
import io.github.fenzeldino.schachdatenverwaltung.model.Verein;
import io.github.fenzeldino.schachdatenverwaltung.repository.MatchUpRepository;
import io.github.fenzeldino.schachdatenverwaltung.repository.SpielerRepository;
import io.github.fenzeldino.schachdatenverwaltung.repository.TurnierRepository;
import io.github.fenzeldino.schachdatenverwaltung.service.RatingService;
import io.github.fenzeldino.schachdatenverwaltung.service.TurnierService;
import io.github.fenzeldino.schachdatenverwaltung.service.TurnierStatistikService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

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

    @Spy
    private RatingService ratingService = new RatingService();

    @Spy
    private TurnierStatistikService turnierStatistikService = new TurnierStatistikService();

    @InjectMocks
    private TurnierService turnierService;

    @Test
    void createTurnier_shouldCreateTurnierWithSpieler() {
        // Arrange
        TurnierCreateDTO createDto = new TurnierCreateDTO("Stadtmeisterschaft 2026", LocalDate.of(2026, 9, 12), "Dresden", 32, List.of(1, 2));

        Spieler spieler1 = new Spieler(1, "Max Mustermann", 2300.00, 23, new ArrayList<>());
        Spieler spieler2 = new Spieler(2, "Domi Mustermann", 2000.00, 23, new ArrayList<>());
        List<Spieler> spielerListe = List.of(spieler1, spieler2);

        when(spielerRepository.findAllById(List.of(1, 2))).thenReturn(spielerListe);

        Turnier savedTurnier = new Turnier(5);
        savedTurnier.setName("Stadtmeisterschaft 2026");
        savedTurnier.setDatum(LocalDate.of(2026, 9, 12));
        savedTurnier.setOrt("Dresden");
        savedTurnier.setStatus(TurnierStatus.GEPLANT);
        savedTurnier.setSpieler(spielerListe);

        when(turnierRepository.save(any(Turnier.class))).thenReturn(savedTurnier);

        // Act
        TurnierResponseDTO result = turnierService.createTurnier(createDto);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.turnierId());
        assertEquals("Stadtmeisterschaft 2026", result.name());
        assertEquals(TurnierStatus.GEPLANT, result.status());
        assertEquals(2, result.teilnehmerAnzahl());
        assertEquals(Set.of(1, 2), result.spielerIds());

        verify(spielerRepository).findAllById(List.of(1, 2));
        verify(turnierRepository).save(argThat(turnier ->
                turnier.getSpieler().equals(spielerListe)
                        && turnier.getStatus() == TurnierStatus.GEPLANT
        ));
    }

    @Test
    void createTurnier_shouldThrowException_WhenDtoIsNull() {
        assertThrows(InvalidRequestException.class, () -> turnierService.createTurnier(null));
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

        assertThrows(ResourceNotFoundException.class,
                () -> turnierService.getTurnier(999));

        verify(turnierRepository).findById(999);
    }

    @Test
    void updateTurnier_shouldUpdateSpielerListe() {
        Turnier existing = new Turnier(1);
        Spieler spielerNeu = new Spieler(3, "Nev Mustermann", 2100.00, 20, new ArrayList<>());

        TurnierUpdateDTO updateDto = new TurnierUpdateDTO(
                1, "Vereinspokal", LocalDate.of(2026, 7, 20), "Dresden", 32, TurnierStatus.LAUFEND, List.of(3));

        when(turnierRepository.findById(1)).thenReturn(Optional.of(existing));
        when(spielerRepository.findAllById(List.of(3))).thenReturn(List.of(spielerNeu));
        when(turnierRepository.save(existing)).thenReturn(existing);

        TurnierResponseDTO result = turnierService.updateTurnier(1, updateDto);

        assertEquals(List.of(spielerNeu), existing.getSpieler());
        assertEquals("Vereinspokal", result.name());
        assertEquals(TurnierStatus.LAUFEND, result.status());
        assertEquals(Set.of(3), result.spielerIds());
        verify(turnierRepository).findById(1);
        verify(spielerRepository).findAllById(List.of(3));
        verify(turnierRepository).save(existing);
    }

    @Test
    void updateTurnier_shouldThrowException_WhenIdsDoNotMatch() {
        Turnier existing = new Turnier(1);
        TurnierUpdateDTO updateDto = new TurnierUpdateDTO(
                2, "Vereinspokal", LocalDate.of(2026, 7, 20), "Dresden", 32, TurnierStatus.LAUFEND, List.of(3));

        when(turnierRepository.findById(1)).thenReturn(Optional.of(existing));

        assertThrows(InvalidRequestException.class, () -> turnierService.updateTurnier(1, updateDto));
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

        assertThrows(ResourceNotFoundException.class,
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

    private Verein vereinMitId(int id, String name) {
        Verein verein = new Verein(name);
        verein.setVereinId(id);
        return verein;
    }

    @Test
    void getVereineImTurnier_shouldGroupSpielerByVereinWithCount() {
        Turnier turnier = new Turnier(1);
        Verein dresden = vereinMitId(1, "SC Dresden 1920");
        Verein leipzig = vereinMitId(2, "SG Leipzig");

        Spieler max = new Spieler(1, "Max Mustermann", 2300.00, 23, new ArrayList<>());
        max.setVerein(dresden);
        Spieler erika = new Spieler(2, "Erika Musterfrau", 1670.00, 31, new ArrayList<>());
        erika.setVerein(dresden);
        Spieler anna = new Spieler(3, "Anna Schmidt", 2010.00, 28, new ArrayList<>());
        anna.setVerein(leipzig);

        turnier.setSpieler(List.of(max, erika, anna));
        when(turnierRepository.findById(1)).thenReturn(Optional.of(turnier));

        List<VereinImTurnierDTO> result = turnierService.getVereineImTurnier(1);

        assertEquals(2, result.size());
        // sortiert nach Name: "SC" vor "SG" (C < G)
        assertEquals("SC Dresden 1920", result.get(0).name());
        assertEquals(2, result.get(0).spielerImTurnier());
        assertEquals("SG Leipzig", result.get(1).name());
        assertEquals(1, result.get(1).spielerImTurnier());
    }

    @Test
    void getVereineImTurnier_shouldIgnoreSpielerOhneVerein() {
        Turnier turnier = new Turnier(1);
        Spieler ohneVerein = new Spieler(4, "Kein Verein", 1500.00, 19, new ArrayList<>());

        turnier.setSpieler(List.of(ohneVerein));
        when(turnierRepository.findById(1)).thenReturn(Optional.of(turnier));

        List<VereinImTurnierDTO> result = turnierService.getVereineImTurnier(1);

        assertTrue(result.isEmpty());
    }

    @Test
    void getVereineImTurnier_shouldThrow_WhenTurnierNotFound() {
        when(turnierRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> turnierService.getVereineImTurnier(99));
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

    @Test
    void dresdenCalculator_shouldReturnRatingResultAndUpdateSpieler() {
        // Gewinner-Rating 1700 / Alter 25 -> DWZ-Matrix-Faktor 1.0 (Band 1601-1800 / 21-35),
        // diff=0 -> Zeile "0-50", Favoritensieg -> Basispunkte 5 (siehe RatingServiceTest)
        Spieler gewinner = new Spieler(1, "Max Mustermann", 1700.0, 25, new ArrayList<>());
        Spieler verlierer = new Spieler(2, "Domi Mustermann", 1700.0, 23, new ArrayList<>());
        MatchUp matchUp = new MatchUp(gewinner, verlierer);
        matchUp.setMatchUpId(8);
        matchUp.setGewinner(gewinner);

        when(matchUpRepository.findById(8)).thenReturn(Optional.of(matchUp));

        RatingResult result = turnierService.DresdenCalculator(1, 8);

        assertEquals(1705.0, result.neuesGewinnerRating(), 1e-9);
        assertEquals(1695.0, result.neuesVerliererRating(), 1e-9);
        assertEquals(1705.0, gewinner.getRating(), 1e-9);
        assertEquals(1695.0, verlierer.getRating(), 1e-9);
        verify(spielerRepository).save(gewinner);
        verify(spielerRepository).save(verlierer);
    }

    @Test
    void dresdenCalculator_shouldThrowException_WhenGewinnerNochNichtGesetzt() {
        Spieler spieler1 = new Spieler(1, "Max Mustermann", 1700.0, 25, new ArrayList<>());
        Spieler spieler2 = new Spieler(2, "Domi Mustermann", 1700.0, 23, new ArrayList<>());
        MatchUp matchUp = new MatchUp(spieler1, spieler2);
        matchUp.setMatchUpId(10);

        when(matchUpRepository.findById(10)).thenReturn(Optional.of(matchUp));

        assertThrows(InvalidRequestException.class, () -> turnierService.DresdenCalculator(1, 10));
        verify(spielerRepository, never()).save(any());
    }

    @Test
    void eloBerehcnung_shouldReturnRatingResultAndUpdateSpieler() {
        Spieler gewinner = new Spieler(1, "Max Mustermann", 1500.0, 25, new ArrayList<>());
        Spieler verlierer = new Spieler(2, "Domi Mustermann", 1500.0, 23, new ArrayList<>());
        MatchUp matchUp = new MatchUp(gewinner, verlierer);
        matchUp.setMatchUpId(9);
        matchUp.setGewinner(gewinner);

        when(matchUpRepository.findById(9)).thenReturn(Optional.of(matchUp));

        RatingResult result = turnierService.EloBerehcnung(1, 9);

        // Ea = 0.5 -> Zuwachs = 20 * 0.5 = 10
        assertEquals(1510.0, result.neuesGewinnerRating(), 1e-9);
        assertEquals(1490.0, result.neuesVerliererRating(), 1e-9);
        verify(spielerRepository).save(gewinner);
        verify(spielerRepository).save(verlierer);
    }
}
