package io.github.fenzeldino.Schachdatenverwaltung.ServiceTest;

import io.github.fenzeldino.Schachdatenverwaltung.DTO.MatchUpDTO;
import io.github.fenzeldino.Schachdatenverwaltung.Model.MatchUp;
import io.github.fenzeldino.Schachdatenverwaltung.Model.Spieler;
import io.github.fenzeldino.Schachdatenverwaltung.Model.Turnier;
import io.github.fenzeldino.Schachdatenverwaltung.Repository.MatchUpRepository;
import io.github.fenzeldino.Schachdatenverwaltung.Repository.SpielerRepository;
import io.github.fenzeldino.Schachdatenverwaltung.Service.MatchUpService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceMatchUpTest {

    @Mock
    private MatchUpRepository matchUpRepository;

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

        MatchUpDTO matchUpDTO = new MatchUpDTO(spieler1,spieler2);

        MatchUp savedMatchUp = new MatchUp();
        savedMatchUp.setSpieler1(spieler1);
        savedMatchUp.setSpieler2(spieler2);

        when(matchUpRepository.save(any(MatchUp.class)))
                .thenReturn(savedMatchUp);


        MatchUpDTO testMatch = matchUpService.createMatUp(matchUpDTO);

        assertEquals(spieler1, testMatch.SpielerEins());
        assertEquals(spieler2, testMatch.SpielerZwei());

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

        List<MatchUpDTO> testListe = matchUpService.getAllMatchUpsFromDb();

        assertEquals(2,testListe.size());

        verify(matchUpRepository).findAll();
    }

}
