package io.github.fenzeldino.schachdatenverwaltung.serviceTest;

import io.github.fenzeldino.schachdatenverwaltung.dto.request.turnier.TurnierCreateDTO;
import io.github.fenzeldino.schachdatenverwaltung.exception.InvalidRequestException;
import io.github.fenzeldino.schachdatenverwaltung.model.Spieler;
import io.github.fenzeldino.schachdatenverwaltung.model.Turnier;
import io.github.fenzeldino.schachdatenverwaltung.model.TurnierStatus;
import io.github.fenzeldino.schachdatenverwaltung.repository.MatchUpRepository;
import io.github.fenzeldino.schachdatenverwaltung.repository.SpielerRepository;
import io.github.fenzeldino.schachdatenverwaltung.repository.TurnierRepository;
import io.github.fenzeldino.schachdatenverwaltung.service.RatingService;
import io.github.fenzeldino.schachdatenverwaltung.service.TurnierService;
import io.github.fenzeldino.schachdatenverwaltung.service.TurnierStatistikService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TurnierPlan001ServiceTest {

    private TurnierRepository turnierRepository;
    private SpielerRepository spielerRepository;
    private TurnierService service;

    @BeforeEach
    void setUp() {
        turnierRepository = mock(TurnierRepository.class);
        spielerRepository = mock(SpielerRepository.class);
        service = new TurnierService(turnierRepository, spielerRepository, mock(MatchUpRepository.class),
                new RatingService(), new TurnierStatistikService());
    }

    @Test
    void createTurnier_shouldRejectMissingOrTooSmallCapacity() {
        assertThrows(InvalidRequestException.class, () -> service.createTurnier(
                new TurnierCreateDTO("Turnier", LocalDate.now(), "Dresden", null, List.of())));
        assertThrows(InvalidRequestException.class, () -> service.createTurnier(
                new TurnierCreateDTO("Turnier", LocalDate.now(), "Dresden", 1, List.of())));
        verifyNoInteractions(turnierRepository);
    }

    @Test
    void addSpielerToTurnier_shouldRejectReachedCapacity() {
        Turnier turnier = new Turnier(1);
        turnier.setMaxTeilnehmer(2);
        turnier.setSpieler(new ArrayList<>(List.of(spieler(1), spieler(2))));
        when(turnierRepository.findById(1)).thenReturn(Optional.of(turnier));
        when(spielerRepository.findById(3)).thenReturn(Optional.of(spieler(3)));

        assertThrows(InvalidRequestException.class, () -> service.addSpielerToTurnier(1, 3));
        verify(turnierRepository, never()).save(any());
    }

    @Test
    void addSpielerToTurnier_shouldKeepLegacyTournamentUnlimited() {
        Turnier turnier = new Turnier(1);
        turnier.setSpieler(new ArrayList<>(List.of(spieler(1), spieler(2))));
        when(turnierRepository.findById(1)).thenReturn(Optional.of(turnier));
        when(spielerRepository.findById(3)).thenReturn(Optional.of(spieler(3)));

        service.addSpielerToTurnier(1, 3);

        assertEquals(3, turnier.getSpieler().size());
        verify(turnierRepository).save(turnier);
    }

    @Test
    void turnierAbschliessen_shouldSetStatusAndReturnUpdatedDto() {
        Turnier turnier = new Turnier(1);
        when(turnierRepository.findById(1)).thenReturn(Optional.of(turnier));
        when(turnierRepository.save(turnier)).thenReturn(turnier);

        var result = service.turnierAbschliessen(1);

        assertEquals(TurnierStatus.ABGESCHLOSSEN, turnier.getStatus());
        assertEquals(TurnierStatus.ABGESCHLOSSEN, result.status());
    }

    private Spieler spieler(int id) {
        return new Spieler(id, "Spieler " + id, 1800, 30, new ArrayList<>());
    }
}
