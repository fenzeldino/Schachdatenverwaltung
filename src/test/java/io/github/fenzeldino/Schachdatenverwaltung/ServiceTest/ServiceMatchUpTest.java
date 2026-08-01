package io.github.fenzeldino.Schachdatenverwaltung.ServiceTest;

import io.github.fenzeldino.Schachdatenverwaltung.DTO.MatchUpDTO;
import io.github.fenzeldino.Schachdatenverwaltung.DTO.Request.MatchUp.MatchUpCreateDTO;
import io.github.fenzeldino.Schachdatenverwaltung.DTO.Request.MatchUp.MatchUpUpdateDTO;
import io.github.fenzeldino.Schachdatenverwaltung.DTO.Response.MatchUp.MatchUpResponseDTO;
import io.github.fenzeldino.Schachdatenverwaltung.Model.MatchUp;
import io.github.fenzeldino.Schachdatenverwaltung.Model.Spieler;
import io.github.fenzeldino.Schachdatenverwaltung.Model.Turnier;
import io.github.fenzeldino.Schachdatenverwaltung.Repository.MatchUpRepository;
import io.github.fenzeldino.Schachdatenverwaltung.Repository.SpielerRepository;
import io.github.fenzeldino.Schachdatenverwaltung.Repository.TurnierRepository;
import io.github.fenzeldino.Schachdatenverwaltung.Service.MatchUpService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

        assertEquals(spieler1, testMatch.spielerEins());
        assertEquals(spieler2, testMatch.spielerZwei());

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
}
