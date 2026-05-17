package io.github.fenzeldino.Schachdatenverwaltung.Controller;

import io.github.fenzeldino.Schachdatenverwaltung.Repository.SpielerRepository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/Spieler")
@Validated

public class SpielerController {

    private final SpielerRepository spielerRepository;

    public SpielerController(SpielerRepository spielerRepository){
        this.spielerRepository = spielerRepository;
    }


}
