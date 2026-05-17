package io.github.fenzeldino.Schachdatenverwaltung.Controller;

import io.github.fenzeldino.Schachdatenverwaltung.DTO.MatchUpDTO;
import io.github.fenzeldino.Schachdatenverwaltung.Service.MatchUpService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matchup")
@Validated

public class MatchUpController {

    private final MatchUpService matchUpService;

    public MatchUpController(MatchUpService matchUpService) {
        this.matchUpService = matchUpService;
    }

    @PostMapping
    public ResponseEntity<MatchUpDTO> create(@RequestBody MatchUpDTO matchUpDTO){
        MatchUpDTO created = matchUpService.createMatUp(matchUpDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

}
