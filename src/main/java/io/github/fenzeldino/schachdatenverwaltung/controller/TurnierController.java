package io.github.fenzeldino.schachdatenverwaltung.controller;

import io.github.fenzeldino.schachdatenverwaltung.dto.request.turnier.TurnierCreateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.request.turnier.TurnierUpdateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.matchUp.MatchUpResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.spieler.SpielerResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.turnier.TurnierResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.turnier.VereinImTurnierDTO;
import io.github.fenzeldino.schachdatenverwaltung.service.TurnierService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/Turnier")
@Validated

public class TurnierController {

    private final TurnierService turnierService;

    public TurnierController(TurnierService turnierService){
        this.turnierService = turnierService;
    }

    /* CREATE */
    @PostMapping
    public ResponseEntity<TurnierResponseDTO> create(@RequestBody TurnierCreateDTO turnierDTO){
        TurnierResponseDTO created = turnierService.createTurnier(turnierDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/getAllTurniere")
    public ResponseEntity<List<TurnierResponseDTO>> getAllTurniere(){
        return ResponseEntity.ok(turnierService.getAllTurniere());
    }

    @GetMapping("/{id}")
    public TurnierResponseDTO getTurnier(@PathVariable Integer id){
        return turnierService.getTurnier(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TurnierResponseDTO> updateTurnier(@RequestBody TurnierUpdateDTO turnierDTO, @PathVariable Integer id){
        TurnierResponseDTO updated = turnierService.updateTurnier(id, turnierDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTurnier(@PathVariable Integer id){
        turnierService.deleteTurnier(id);
        return ResponseEntity.noContent().build();
    }

    /* Spieler eines Turniers gefiltert nach IDs anzeigen: GET /api/Turnier/5/spieler?ids=1,2,3 */
    @GetMapping("/{turnierId}/spieler")
    public ResponseEntity<List<SpielerResponseDTO>> showAllTurnierSpieler(@PathVariable Integer turnierId, @RequestParam Set<Integer> ids){
        return ResponseEntity.ok(turnierService.showAllTurnierSpieler(turnierId, ids));
    }

    /* Vereine, die im Turnier vertreten sind, mit Teilnehmerzahl je Verein: GET /api/Turnier/5/vereine */
    @GetMapping("/{turnierId}/vereine")
    public ResponseEntity<List<VereinImTurnierDTO>> getVereineImTurnier(@PathVariable Integer turnierId){
        return ResponseEntity.ok(turnierService.getVereineImTurnier(turnierId));
    }

    /* MatchUps eines Turniers gefiltert nach IDs anzeigen: GET /api/Turnier/5/matchups?ids=8,9 */
    @GetMapping("/{turnierId}/matchups")
    public ResponseEntity<List<MatchUpResponseDTO>> showAllMatchUps(@PathVariable Integer turnierId, @RequestParam Set<Integer> ids){
        return ResponseEntity.ok(turnierService.showAllMatchUps(turnierId, ids));
    }

    /* Bereits existierenden Spieler zum Turnier hinzufügen: POST /api/Turnier/5/spieler/12 */
    @PostMapping("/{turnierId}/spieler/{spielerId}")
    public ResponseEntity<Void> addSpielerToTurnier(@PathVariable Integer turnierId, @PathVariable Integer spielerId){
        turnierService.addSpielerToTurnier(turnierId, spielerId);
        return ResponseEntity.noContent().build();
    }

    /* Bereits existierendes MatchUp mit dem Turnier verknüpfen: POST /api/Turnier/5/matchups/8/link */
    @PostMapping("/{turnierId}/matchups/{matchUpId}/link")
    public ResponseEntity<Void> addMatchUpToTurnier(@PathVariable Integer turnierId, @PathVariable Integer matchUpId){
        turnierService.addMatchUpToTurnier(turnierId, matchUpId);
        return ResponseEntity.noContent().build();
    }

    /* Neues MatchUp zwischen zwei existierenden Spielern erstellen: POST /api/Turnier/5/matchups/neu/12/13 */
    @PostMapping("/{turnierId}/matchups/neu/{spieler1Id}/{spieler2Id}")
    public ResponseEntity<Void> addMatchUpToDB(@PathVariable Integer turnierId, @PathVariable Integer spieler1Id, @PathVariable Integer spieler2Id){
        turnierService.addMatchUpToDB(turnierId, spieler1Id, spieler2Id);
        return ResponseEntity.noContent().build();
    }

    /* Rating-Berechnung nach Dresdner Methode auslösen: POST /api/Turnier/5/matchups/8/dresden */
    @PostMapping("/{turnierId}/matchups/{matchId}/dresden")
    public ResponseEntity<Void> calculateDresden(@PathVariable Integer turnierId, @PathVariable Integer matchId){
        turnierService.DresdenCalculator(turnierId, matchId);
        return ResponseEntity.noContent().build();
    }

    /* Rating-Berechnung nach Elo auslösen: POST /api/Turnier/5/matchups/8/elo */
    @PostMapping("/{turnierId}/matchups/{matchId}/elo")
    public ResponseEntity<Void> calculateElo(@PathVariable Integer turnierId, @PathVariable Integer matchId){
        turnierService.EloBerehcnung(turnierId, matchId);
        return ResponseEntity.noContent().build();
    }

}
