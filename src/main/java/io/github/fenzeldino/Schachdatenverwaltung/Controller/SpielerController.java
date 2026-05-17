package io.github.fenzeldino.Schachdatenverwaltung.Controller;

import io.github.fenzeldino.Schachdatenverwaltung.DTO.SpielerDTO;
import io.github.fenzeldino.Schachdatenverwaltung.Repository.SpielerRepository;
import io.github.fenzeldino.Schachdatenverwaltung.Service.SpielerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/Spieler")
@Validated

public class SpielerController {

    private final SpielerService spielerService;

    public SpielerController(SpielerService spielerService){
        this.spielerService = spielerService;
    }

    /* CREATE */

    @PostMapping
    public ResponseEntity<SpielerDTO> create(@RequestBody SpielerDTO spielerDto){
        SpielerDTO created = spielerService.createSpieler(spielerDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }


}
