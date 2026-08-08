package io.github.fenzeldino.schachdatenverwaltung.serviceTest;

import io.github.fenzeldino.schachdatenverwaltung.dto.request.verein.VereinCreateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.request.verein.VereinUpdateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.spieler.SpielerResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.verein.VereinResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.model.Spieler;
import io.github.fenzeldino.schachdatenverwaltung.model.Verein;
import io.github.fenzeldino.schachdatenverwaltung.repository.SpielerRepository;
import io.github.fenzeldino.schachdatenverwaltung.repository.VereinRepository;
import io.github.fenzeldino.schachdatenverwaltung.service.VereinService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VereinServiceTest {

    @Mock
    private VereinRepository vereinRepository;

    @Mock
    private SpielerRepository spielerRepository;

    @InjectMocks
    private VereinService vereinService;

    private Verein vereinMitId(int id, String name) {
        Verein verein = new Verein(name);
        verein.setVereinId(id);
        return verein;
    }

    /* ---------- createVerein ---------- */

    @Test
    void createVerein_shouldPersistAndReturnDto() {
        VereinCreateDTO dto = new VereinCreateDTO("SC Dresden 1920", "C0327");
        when(vereinRepository.existsByNameIgnoreCase("SC Dresden 1920")).thenReturn(false);
        when(vereinRepository.save(any(Verein.class))).thenAnswer(inv -> {
            Verein v = inv.getArgument(0);
            v.setVereinId(1);
            return v;
        });

        VereinResponseDTO result = vereinService.createVerein(dto);

        assertEquals(1, result.id());
        assertEquals("SC Dresden 1920", result.name());
        assertEquals("C0327", result.zpsCode());
    }

    @Test
    void createVerein_shouldThrow_WhenNameIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> vereinService.createVerein(new VereinCreateDTO("   ", null)));
        verify(vereinRepository, never()).save(any());
    }

    @Test
    void createVerein_shouldThrow_WhenDtoIsNull() {
        assertThrows(IllegalArgumentException.class, () -> vereinService.createVerein(null));
    }

    @Test
    void createVerein_shouldThrow_WhenNameAlreadyExists() {
        when(vereinRepository.existsByNameIgnoreCase("SC Dresden 1920")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> vereinService.createVerein(new VereinCreateDTO("SC Dresden 1920", null)));
        verify(vereinRepository, never()).save(any());
    }

    /* ---------- lesende Zugriffe ---------- */

    @Test
    void getAllVereine_shouldMapAllEntries() {
        when(vereinRepository.findAll()).thenReturn(List.of(
                vereinMitId(1, "SC Dresden 1920"),
                vereinMitId(2, "SG Leipzig")
        ));

        List<VereinResponseDTO> result = vereinService.getAllVereine();

        assertEquals(2, result.size());
        assertEquals("SC Dresden 1920", result.get(0).name());
        assertEquals("SG Leipzig", result.get(1).name());
    }

    @Test
    void getVerein_shouldThrow_WhenNotFound() {
        when(vereinRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> vereinService.getVerein(99));
    }

    /* ---------- der eigentliche Join ---------- */

    @Test
    void getSpielerImVerein_shouldReturnOnlySpielerOfThatVerein() {
        Verein verein = vereinMitId(1, "SC Dresden 1920");
        Spieler max = new Spieler("Max Mustermann", 1850.0, 23);
        max.setVerein(verein);

        when(vereinRepository.existsById(1)).thenReturn(true);
        when(spielerRepository.findByVerein_VereinId(1)).thenReturn(List.of(max));

        List<SpielerResponseDTO> result = vereinService.getSpielerImVerein(1);

        assertEquals(1, result.size());
        assertEquals("Max Mustermann", result.get(0).name());
        assertEquals(1, result.get(0).vereinId());
        assertEquals("SC Dresden 1920", result.get(0).vereinName());
    }

    @Test
    void getSpielerImVerein_shouldThrow_WhenVereinNotFound() {
        when(vereinRepository.existsById(99)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> vereinService.getSpielerImVerein(99));
        verify(spielerRepository, never()).findByVerein_VereinId(any());
    }

    /* ---------- Zuordnung ---------- */

    @Test
    void spielerZuweisen_shouldSetVereinOnSpieler() {
        Verein verein = vereinMitId(1, "SC Dresden 1920");
        Spieler spieler = new Spieler("Max Mustermann", 1850.0, 23);

        when(vereinRepository.findById(1)).thenReturn(Optional.of(verein));
        when(spielerRepository.findById(5)).thenReturn(Optional.of(spieler));
        when(spielerRepository.save(spieler)).thenReturn(spieler);

        SpielerResponseDTO result = vereinService.spielerZuweisen(1, 5);

        assertEquals(1, result.vereinId());
        assertEquals("SC Dresden 1920", result.vereinName());
        assertSame(verein, spieler.getVerein());
    }

    @Test
    void spielerZuweisen_shouldThrow_WhenSpielerNotFound() {
        when(vereinRepository.findById(1)).thenReturn(Optional.of(vereinMitId(1, "SC Dresden 1920")));
        when(spielerRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> vereinService.spielerZuweisen(1, 99));
    }

    @Test
    void spielerEntfernen_shouldClearVerein() {
        Spieler spieler = new Spieler("Max Mustermann", 1850.0, 23);
        spieler.setVerein(vereinMitId(1, "SC Dresden 1920"));

        when(spielerRepository.findById(5)).thenReturn(Optional.of(spieler));
        when(spielerRepository.save(spieler)).thenReturn(spieler);

        SpielerResponseDTO result = vereinService.spielerEntfernen(5);

        assertNull(result.vereinId());
        assertNull(result.vereinName());
        assertNull(spieler.getVerein());
    }

    /* ---------- update / delete ---------- */

    @Test
    void updateVerein_shouldOverwriteNameAndZpsCode() {
        Verein existing = vereinMitId(1, "Alter Name");
        when(vereinRepository.findById(1)).thenReturn(Optional.of(existing));
        when(vereinRepository.save(existing)).thenReturn(existing);

        VereinResponseDTO result = vereinService.updateVerein(1, new VereinUpdateDTO(1, "Neuer Name", "C0999"));

        assertEquals("Neuer Name", result.name());
        assertEquals("C0999", result.zpsCode());
    }

    @Test
    void deleteVerein_shouldDetachSpielerBeforeDeleting() {
        Verein verein = vereinMitId(1, "SC Dresden 1920");
        Spieler spieler = new Spieler("Max Mustermann", 1850.0, 23);
        spieler.setVerein(verein);

        when(vereinRepository.findById(1)).thenReturn(Optional.of(verein));
        when(spielerRepository.findByVerein_VereinId(1)).thenReturn(List.of(spieler));

        vereinService.deleteVerein(1);

        // Spieler darf nicht mitgelöscht werden, nur die Zuordnung fällt weg
        assertNull(spieler.getVerein());
        verify(spielerRepository).saveAll(anyList());
        verify(vereinRepository).delete(verein);
    }

    @Test
    void deleteVerein_shouldThrow_WhenNotFound() {
        when(vereinRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> vereinService.deleteVerein(99));
        verify(vereinRepository, never()).delete(any());
    }
}
