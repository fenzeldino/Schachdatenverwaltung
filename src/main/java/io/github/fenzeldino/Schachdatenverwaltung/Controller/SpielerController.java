package io.github.fenzeldino.Schachdatenverwaltung.Controller;

import io.github.fenzeldino.Schachdatenverwaltung.DTO.Request.Spieler.SpielerCreateDTO;
import io.github.fenzeldino.Schachdatenverwaltung.DTO.Request.Spieler.SpielerDeleteDTO;
import io.github.fenzeldino.Schachdatenverwaltung.DTO.Response.Spieler.SpielerResponseDTO;
import io.github.fenzeldino.Schachdatenverwaltung.Service.SpielerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<SpielerResponseDTO> create(@RequestBody SpielerCreateDTO spielerDTO){
        SpielerResponseDTO created = spielerService.createSpieler(spielerDTO);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/getAllSpieler")
    public ResponseEntity<List<SpielerResponseDTO>> getAllSpieler(){
        return ResponseEntity.ok(spielerService.getAllSpieler());
    }

    @GetMapping("/{id}")
    public SpielerResponseDTO getSpieler(@PathVariable Integer id){
        return spielerService.getSpieler(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpielerResponseDTO> updateSpieler(@RequestBody SpielerCreateDTO spielerDTO,@PathVariable Integer id){
        SpielerResponseDTO updated = spielerService.updateSpieler(id,spielerDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpieler(@PathVariable Integer id){
        spielerService.deleteSpieler(id);
        return ResponseEntity.noContent().build();
    }

}
