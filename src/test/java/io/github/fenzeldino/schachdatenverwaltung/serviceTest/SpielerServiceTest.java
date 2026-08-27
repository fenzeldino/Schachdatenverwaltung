package io.github.fenzeldino.schachdatenverwaltung.serviceTest;

import io.github.fenzeldino.schachdatenverwaltung.dto.request.spieler.SpielerCreateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.request.spieler.SpielerUpdateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.spieler.SpielerResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.exception.InvalidRequestException;
import io.github.fenzeldino.schachdatenverwaltung.exception.ResourceNotFoundException;
import io.github.fenzeldino.schachdatenverwaltung.model.Spieler;
import io.github.fenzeldino.schachdatenverwaltung.model.Turnier;
import io.github.fenzeldino.schachdatenverwaltung.model.Verein;
import io.github.fenzeldino.schachdatenverwaltung.repository.SpielerRepository;
import io.github.fenzeldino.schachdatenverwaltung.repository.TurnierRepository;
import io.github.fenzeldino.schachdatenverwaltung.repository.VereinRepository;
import io.github.fenzeldino.schachdatenverwaltung.service.SpielerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpielerServiceTest {

    @Mock
    private SpielerRepository spielerRepository;

    @Mock
    private TurnierRepository turnierRepository;


    @Mock
    private VereinRepository vereinRepository;

    @InjectMocks
    private SpielerService spielerService;

    @Test
    void createSpieler_shouldCreateSpielerWithTurniere() {
        // Arrange
        SpielerCreateDTO spielerDto = new SpielerCreateDTO(
                "Max Mustermann",
                2300.00,
                23,
                List.of(1, 2)
        );

        Turnier turnier1 = new Turnier(1);
        Turnier turnier2 = new Turnier(2);

        List<Turnier> turniere = List.of(turnier1, turnier2);

        when(turnierRepository.findAllById(List.of(1, 2)))
                .thenReturn(turniere); //Wenn im turnier Rep nach Turnier 1 und 2 gesucht wird dann Liste der Turniere

        // Act
        SpielerResponseDTO result =
                spielerService.createSpieler(spielerDto);

        // Assert
        assertNotNull(result);

        verify(turnierRepository)
                .findAllById(List.of(1, 2)); //Überprüft ob diese Methode aufgerufen wird

        verify(spielerRepository)
                .save(argThat(spieler ->
                        spieler.getTurnier().equals(turniere)
                ));
    }


    @Test
    void createSpieler_shouldRejectNullDto() {
        assertThrows(InvalidRequestException.class, () -> spielerService.createSpieler((SpielerCreateDTO) null));
    }

    @Test
    void createSpieler_shouldRejectMissingName() {
        SpielerCreateDTO nullName = new SpielerCreateDTO(null, 1800.0, 30, List.of());
        SpielerCreateDTO blankName = new SpielerCreateDTO("  ", 1800.0, 30, List.of());
        assertThrows(InvalidRequestException.class, () -> spielerService.createSpieler(nullName));
        assertThrows(InvalidRequestException.class, () -> spielerService.createSpieler(blankName));
    }

    @Test
    void createSpieler_shouldRejectNegativeRating() {
        SpielerCreateDTO spielerDto = new SpielerCreateDTO("Max Mustermann", -1.0, 30, List.of());
        assertThrows(InvalidRequestException.class, () -> spielerService.createSpieler(spielerDto));
    }

    @Test
    void createSpieler_shouldRejectNegativeAge() {
        SpielerCreateDTO spielerDto = new SpielerCreateDTO("Max Mustermann", 1800.0, -1, List.of());
        assertThrows(InvalidRequestException.class, () -> spielerService.createSpieler(spielerDto));
    }

    @Test
    void createSpieler_shouldAssignVerein() {
        SpielerCreateDTO spielerDto = new SpielerCreateDTO("Max Mustermann", 1800.0, 30, List.of(), 7);
        Verein verein = new Verein("Schachclub Dresden");
        verein.setVereinId(7);
        when(vereinRepository.findById(7)).thenReturn(Optional.of(verein));
        SpielerResponseDTO result = spielerService.createSpieler(spielerDto);
        assertEquals(7, result.vereinId());
        assertEquals("Schachclub Dresden", result.vereinName());
        verify(vereinRepository).findById(7);
        verify(spielerRepository).save(argThat(spieler -> spieler.getVerein() == verein));
    }

    @Test
    void createSpieler_shouldRejectUnknownVerein() {
        SpielerCreateDTO spielerDto = new SpielerCreateDTO("Max Mustermann", 1800.0, 30, List.of(), 999);
        when(vereinRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> spielerService.createSpieler(spielerDto));
        verify(spielerRepository, never()).save(argThat(spieler -> true));
    }

    @Test
    void createSpieler_shouldCreateWithoutVerein() {
        SpielerCreateDTO spielerDto = new SpielerCreateDTO("Max Mustermann", 1800.0, 30, List.of());
        SpielerResponseDTO result = spielerService.createSpieler(spielerDto);
        assertNull(result.vereinId());
        assertNull(result.vereinName());
        verify(vereinRepository, never()).findById(org.mockito.ArgumentMatchers.anyInt());
        verify(spielerRepository).save(argThat(spieler -> spieler.getVerein() == null));
    }

    @Test
    void getAllSpieler_shouldReturnAllPlayer(){
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

        List<Spieler> spielerListe = List.of(spieler1,spieler2,spieler3);

        when(spielerRepository.findAll()).thenReturn(spielerListe);

        List<SpielerResponseDTO> testListe = spielerService.getAllSpieler();

        assertEquals(3,testListe.size());
        assertEquals(1,testListe.getFirst().id());
        assertEquals("Max Mustermann", testListe.get(0).name());
        assertEquals(2300.00, testListe.get(0).rating());
        assertEquals(List.of(1, 2), testListe.get(0).TurnierIds());

        assertEquals(2, testListe.get(1).id());
        assertEquals("Domi Mustermann", testListe.get(1).name());

        assertEquals(3, testListe.get(2).id());
        assertEquals("Kev Mustermann", testListe.get(2).name());
        assertEquals(List.of(1), testListe.get(2).TurnierIds());

        verify(spielerRepository).findAll(); // wurde auf spielerService die methode getAllSpieler ausgeführt?
    }

    @Test
    void getSpieler_ShouldReturnOneSpieler_WhenIdExists(){

        Turnier turnier1 = new Turnier(1);
        Turnier turnier2 = new Turnier(2);

        Spieler spieler1 = new Spieler(
                1,
                "Max Mustermann",
                2300.00,
                23,
                List.of(turnier1, turnier2)
        );

        //Arrange
        when(spielerRepository.findById(1)).thenReturn(Optional.of(spieler1));
        //Act
        SpielerResponseDTO testSpieler = spielerService.getSpieler(1);
        //Assert
        verify(spielerRepository).findById(1);
        assertEquals(1,testSpieler.id());
        assertEquals("Max Mustermann",testSpieler.name());
        assertEquals(2300.0,testSpieler.rating()); // double beachten
        assertEquals(List.of(1, 2),testSpieler.TurnierIds());
    }

    @Test
    void getSpieler_ShouldThrowException_WhenIdNotFound(){

        when(spielerRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> spielerService.getSpieler(999));

        verify(spielerRepository).findById(999);
    }

    @Test
    void updateSpieler_shouldRetrunUpdatedPlayerDTO(){

        Turnier turnier1 = new Turnier(1);
        Turnier turnier2 = new Turnier(2);

        Spieler spieler1 = new Spieler(
                1,
                "Max Mustermann",
                2300.00,
                23,
                List.of(turnier1, turnier2)
        );

        SpielerUpdateDTO toupdate = new SpielerUpdateDTO(
                1,
                "Mai Mustermann",
                2200.00,
                21,
                List.of(1, 2)
        );

        when(spielerRepository.findById(1)).thenReturn(Optional.of(spieler1));

        SpielerResponseDTO testSpieler = spielerService.updateSpieler(1,toupdate);

        assertEquals("Mai Mustermann",spieler1.getName());
        assertEquals(2200.00,spieler1.getRating());
        assertEquals(21,spieler1.getAge());
        verify(spielerRepository).findById(1);
        verify(spielerRepository).save(spieler1);
    }

}
