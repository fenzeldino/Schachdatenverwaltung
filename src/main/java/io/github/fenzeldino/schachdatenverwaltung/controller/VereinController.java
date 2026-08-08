package io.github.fenzeldino.schachdatenverwaltung.controller;

import io.github.fenzeldino.schachdatenverwaltung.dto.request.verein.VereinCreateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.request.verein.VereinUpdateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.spieler.SpielerResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.verein.VereinResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.service.VereinService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/Verein")
@Validated
public class VereinController {

    private final VereinService vereinService;

    public VereinController(VereinService vereinService) {
        this.vereinService = vereinService;
    }

    /* CREATE */
    @PostMapping
    public ResponseEntity<VereinResponseDTO> create(@RequestBody VereinCreateDTO vereinDTO) {
        return new ResponseEntity<>(vereinService.createVerein(vereinDTO), HttpStatus.CREATED);
    }

    @GetMapping("/getAllVereine")
    public ResponseEntity<List<VereinResponseDTO>> getAllVereine() {
        return ResponseEntity.ok(vereinService.getAllVereine());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VereinResponseDTO> getVerein(@PathVariable Integer id) {
        return ResponseEntity.ok(vereinService.getVerein(id));
    }

    /** Spielerliste eines Vereins — der Join, um den es hier eigentlich geht. */
    @GetMapping("/{id}/spieler")
    public ResponseEntity<List<SpielerResponseDTO>> getSpielerImVerein(@PathVariable Integer id) {
        return ResponseEntity.ok(vereinService.getSpielerImVerein(id));
    }

    /** Ordnet einen bestehenden Spieler diesem Verein zu. */
    @PutMapping("/{vereinId}/spieler/{spielerId}")
    public ResponseEntity<SpielerResponseDTO> spielerZuweisen(@PathVariable Integer vereinId,
                                                              @PathVariable Integer spielerId) {
        return ResponseEntity.ok(vereinService.spielerZuweisen(vereinId, spielerId));
    }

    /** Löst die Vereinszugehörigkeit eines Spielers wieder auf. */
    @DeleteMapping("/spieler/{spielerId}")
    public ResponseEntity<SpielerResponseDTO> spielerEntfernen(@PathVariable Integer spielerId) {
        return ResponseEntity.ok(vereinService.spielerEntfernen(spielerId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VereinResponseDTO> updateVerein(@RequestBody VereinUpdateDTO vereinDTO,
                                                          @PathVariable Integer id) {
        return ResponseEntity.ok(vereinService.updateVerein(id, vereinDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVerein(@PathVariable Integer id) {
        vereinService.deleteVerein(id);
        return ResponseEntity.noContent().build();
    }
}
