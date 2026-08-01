package io.github.fenzeldino.schachdatenverwaltung.controller;

import io.github.fenzeldino.schachdatenverwaltung.dto.request.matchUp.MatchUpCreateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.matchUp.MatchUpResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.service.MatchUpService;
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
    public ResponseEntity<MatchUpResponseDTO> create(@RequestBody MatchUpCreateDTO matchUpDTO){
        MatchUpResponseDTO created = matchUpService.createMatchUp(matchUpDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

}
