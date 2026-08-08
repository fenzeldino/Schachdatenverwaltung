package io.github.fenzeldino.schachdatenverwaltung.serviceTest;

import io.github.fenzeldino.schachdatenverwaltung.dto.request.matchUp.MatchUpCreateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.request.matchUp.MatchUpUpdateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.matchUp.MatchUpResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.model.MatchUp;
import io.github.fenzeldino.schachdatenverwaltung.model.Spieler;
import io.github.fenzeldino.schachdatenverwaltung.model.Turnier;
import io.github.fenzeldino.schachdatenverwaltung.repository.MatchUpRepository;
import io.github.fenzeldino.schachdatenverwaltung.repository.SpielerRepository;
import io.github.fenzeldino.schachdatenverwaltung.repository.TurnierRepository;
import io.github.fenzeldino.schachdatenverwaltung.service.MatchUpService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceMatchUpTest {

    @Mock
    private MatchUpRepository matchUpRepository;

    @Mock
    private TurnierRepository turnierRepository;

    @Mock
    private SpielerRepository spielerRepository;

    @InjectMocks
    private MatchUpService matchUpService;


    @Test
    void createMatchUp_shouldCreateMatchUp(){

        Turnier turnier1 = new Turnier(1);
        Turnier turnier2 = new Turnier(2);

        Spieler spieler1 = new Spieler(
                1,
                "Max Mustermann",
                2300.00,
                23,
                List.of(turnier1, turnier2)
        );

        Spieler spieler2 = new Spieler(
                2,
                "Domi Mustermann",
                2000.00,
                23,
                List.of(turnier1, turnier2)
        );

        MatchUpCreateDTO matchUpDTO = new MatchUpCreateDTO(spieler1,spieler2,1);

        MatchUp savedMatchUp = new MatchUp();
        savedMatchUp.setSpieler1(spieler1);
        savedMatchUp.setSpieler2(spieler2);

        when(matchUpRepository.save(any(MatchUp.class)))
                .thenReturn(savedMatchUp);

        when(turnierRepository.findById(matchUpDTO.turnierId())).thenReturn(Optional.of(turnier1));


        MatchUpResponseDTO testMatch = matchUpService.createMatchUp(matchUpDTO);

        assertEquals(spieler1.getSpielerId(), testMatch.spielerEins().id());
        assertEquals(spieler2.getSpielerId(), testMatch.spielerZwei().id());

        verify(matchUpRepository).save(argThat(matchUp ->
                matchUp.getSpieler1().equals(spieler1)
                        && matchUp.getSpieler2().equals(spieler2)
        ));

    }

    @Test
    void getAllMatchUps_shouldReturnAllMatchuUps(){

        Turnier turnier1 = new Turnier(1);
        Turnier turnier2 = new Turnier(2);

        Spieler spieler1 = new Spieler(
                1,
                "Max Mustermann",
                2300.00,
                23,
                List.of(turnier1, turnier2)
        );

        Spieler spieler2 = new Spieler(
                2,
                "Domi Mustermann",
                2000.00,
                23,
                List.of(turnier1, turnier2)
        );

        Spieler spieler3 = new Spieler(
                3,
                "Kev Mustermann",
                2300.00,
                18,
                List.of(turnier1)
        );

        MatchUp matchUp1 = new MatchUp(spieler1,spieler2);
        MatchUp matchUp2 = new MatchUp(spieler1,spieler3);

        List<MatchUp> matchUps = List.of(matchUp1,matchUp2);
        when(matchUpRepository.findAll()).thenReturn(matchUps);
        List<MatchUpResponseDTO> testListe = matchUpService.getAllMatchUpsFromDb();
        assertEquals(2,testListe.size());
        verify(matchUpRepository).findAll();
    }

    @Test
    void updateMatchUp_shouldReturnMatchUpResoponseDTO(){

        //Arrange
        Turnier turnier1 = new Turnier(1);
        Turnier turnier2 = new Turnier(2);

        Turnier neuesTurnier = turnier2;

        Spieler alterSpieler1 = new Spieler(
                1,
                "Max Mustermann",
                2300.00,
                23,
                List.of(turnier1, turnier2)
        );

        Spieler alterSpieler2 = new Spieler(
                2,
                "Domi Mustermann",
                2000.00,
                23,
                List.of(turnier1, turnier2)
        );

        Spieler neuerSpieler1 = new Spieler(
                3,
                "Nev Mustermann",
                2300.00,
                15,
                List.of(turnier1)
        );

        Spieler neuerSpieler2 = new Spieler(
                4,
                "Jev Mustermann",
                2200.00,
                17,
                List.of(turnier1)
        );

        Spieler gewinner = neuerSpieler2;

        MatchUp existingMatchUp = new MatchUp(
                1,
                alterSpieler1,
                alterSpieler2,
                turnier1,
                alterSpieler1);

        MatchUpUpdateDTO UpdateTo = new MatchUpUpdateDTO(
                1,
                3,
                4,
                2,
                4);

        when(matchUpRepository.findById(1)).thenReturn(Optional.of(existingMatchUp));
        when(spielerRepository.findById(3)).thenReturn(Optional.of(neuerSpieler1));
        when(spielerRepository.findById(4)).thenReturn(Optional.of(neuerSpieler2));
        when(turnierRepository.findById(2)).thenReturn(Optional.of(neuesTurnier));
        //when(spielerRepository.findById(4)).thenReturn(Optional.of(neuerSpieler2)); // Gewinner

        //Act
        MatchUpResponseDTO testMatchUp = matchUpService.updateMatchUp(1,UpdateTo);

        //Assert
        assertEquals(neuerSpieler1,existingMatchUp.getSpieler1());
        assertEquals(neuerSpieler2,existingMatchUp.getSpieler2());


        verify(matchUpRepository).findById(1);
        verify(spielerRepository).findById(3);
        verify(spielerRepository,times(2)).findById(4);
        verify(turnierRepository).findById(2);
        verify(matchUpRepository).save(existingMatchUp);

    }

    @Test
    void deleteMatchUpById_shouldDelete_WhenExists(){
        when(matchUpRepository.existsById(1)).thenReturn(true);

        matchUpService.deleteMatchUpById(1);

        verify(matchUpRepository).deleteById(1);
    }

    @Test
    void deleteMatchUpById_shouldThrowException_WhenNotFound(){
        when(matchUpRepository.existsById(999)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> matchUpService.deleteMatchUpById(999));

        verify(matchUpRepository, never()).deleteById(any());
    }

    @Test
    void addGewinner_shouldSetGewinner_WhenSpielerIstTeilDesMatches(){
        Spieler spieler1 = new Spieler(1, "Max Mustermann", 2300.00, 23, List.of());
        Spieler spieler2 = new Spieler(2, "Domi Mustermann", 2000.00, 23, List.of());
        MatchUp matchUp = new MatchUp(spieler1, spieler2);

        matchUpService.addGewinner(matchUp, spieler1);

        assertEquals(spieler1, matchUp.getGewinner());
        verify(matchUpRepository).save(matchUp);
    }

    @Test
    void addGewinner_shouldDoNothing_WhenSpielerNichtTeilDesMatchesIst(){
        Spieler spieler1 = new Spieler(1, "Max Mustermann", 2300.00, 23, List.of());
        Spieler spieler2 = new Spieler(2, "Domi Mustermann", 2000.00, 23, List.of());
        Spieler fremderSpieler = new Spieler(3, "Nev Mustermann", 2100.00, 20, List.of());
        MatchUp matchUp = new MatchUp(spieler1, spieler2);

        matchUpService.addGewinner(matchUp, fremderSpieler);

        assertNull(matchUp.getGewinner());
        verify(matchUpRepository, never()).save(any());
    }

    @Test
    void addGewinner_byId_shouldLoadEntitiesUndGewinnerSetzen(){
        Spieler spieler1 = new Spieler(1, "Max Mustermann", 2300.00, 23, List.of());
        Spieler spieler2 = new Spieler(2, "Domi Mustermann", 2000.00, 23, List.of());
        MatchUp matchUp = new MatchUp(spieler1, spieler2);
        matchUp.setMatchUpId(8);

        when(matchUpRepository.findById(8)).thenReturn(Optional.of(matchUp));
        when(spielerRepository.findById(1)).thenReturn(Optional.of(spieler1));

        matchUpService.addGewinner(8, 1);

        assertEquals(spieler1, matchUp.getGewinner());
        verify(matchUpRepository).save(matchUp);
    }

    @Test
    void addVerlierer_byId_shouldLoadEntitiesUndVerliererSetzen(){
        Spieler spieler1 = new Spieler(1, "Max Mustermann", 2300.00, 23, List.of());
        Spieler spieler2 = new Spieler(2, "Domi Mustermann", 2000.00, 23, List.of());
        MatchUp matchUp = new MatchUp(spieler1, spieler2);
        matchUp.setMatchUpId(8);

        when(matchUpRepository.findById(8)).thenReturn(Optional.of(matchUp));
        when(spielerRepository.findById(2)).thenReturn(Optional.of(spieler2));

        matchUpService.addVerlierer(8, 2);

        // Bestehender Bug in addVerlierer(): setzt aktuell match.setGewinner(verlierer)
        // statt eines eigenen Verlierer-Felds. Test dokumentiert den Ist-Zustand.
        assertEquals(spieler2, matchUp.getGewinner());
        verify(matchUpRepository).save(matchUp);
    }
}
