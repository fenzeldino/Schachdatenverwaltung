package io.github.fenzeldino.schachdatenverwaltung.controller;

import io.github.fenzeldino.schachdatenverwaltung.dto.request.matchUp.MatchUpCreateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.request.matchUp.MatchUpUpdateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.matchUp.MatchUpResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.service.MatchUpService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matchup")
@Validated

public class MatchUpController {

    private final MatchUpService matchUpService;

    public MatchUpController(MatchUpService matchUpService) {
        this.matchUpService = matchUpService;
    }

    @PostMapping
    public ResponseEntity<MatchUpResponseDTO> create(@RequestBody MatchUpCreateDTO matchUpDTO){
        MatchUpResponseDTO created = matchUpService.createMatchUp(matchUpDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/getAllMatchUps")
    public ResponseEntity<List<MatchUpResponseDTO>> getAllMatchUps(){
        return ResponseEntity.ok(matchUpService.getAllMatchUpsFromDb());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MatchUpResponseDTO> updateMatchUp(@RequestBody MatchUpUpdateDTO matchUpDTO, @PathVariable Integer id){
        MatchUpResponseDTO updated = matchUpService.updateMatchUp(id, matchUpDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatchUp(@PathVariable Integer id){
        matchUpService.deleteMatchUpById(id);
        return ResponseEntity.noContent().build();
    }

    /* Gewinner setzen: POST /api/matchup/8/gewinner/1 */
    @PostMapping("/{matchUpId}/gewinner/{spielerId}")
    public ResponseEntity<Void> addGewinner(@PathVariable Integer matchUpId, @PathVariable Integer spielerId){
        matchUpService.addGewinner(matchUpId, spielerId);
        return ResponseEntity.noContent().build();
    }

    /* Verlierer setzen: POST /api/matchup/8/verlierer/2 */
    @PostMapping("/{matchUpId}/verlierer/{spielerId}")
    public ResponseEntity<Void> addVerlierer(@PathVariable Integer matchUpId, @PathVariable Integer spielerId){
        matchUpService.addVerlierer(matchUpId, spielerId);
        return ResponseEntity.noContent().build();
    }

}
