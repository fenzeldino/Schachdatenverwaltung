package io.github.fenzeldino.schachdatenverwaltung.service;


import io.github.fenzeldino.schachdatenverwaltung.dto.request.spieler.SpielerCreateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.request.spieler.SpielerUpdateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.spieler.SpielerResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.exception.InvalidRequestException;
import io.github.fenzeldino.schachdatenverwaltung.exception.ResourceNotFoundException;
import io.github.fenzeldino.schachdatenverwaltung.mapper.SpielerMapper;
import io.github.fenzeldino.schachdatenverwaltung.model.Mitglied;
import io.github.fenzeldino.schachdatenverwaltung.model.Person;
import io.github.fenzeldino.schachdatenverwaltung.model.Spieler;
import io.github.fenzeldino.schachdatenverwaltung.model.Turnier;
import io.github.fenzeldino.schachdatenverwaltung.model.Verein;
import io.github.fenzeldino.schachdatenverwaltung.repository.SpielerRepository;
import io.github.fenzeldino.schachdatenverwaltung.repository.TurnierRepository;
import io.github.fenzeldino.schachdatenverwaltung.repository.VereinRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class SpielerService {

    private final SpielerRepository spielerRepository;
    private final TurnierRepository turnierRepository;
    private final VereinRepository vereinRepository;

    public SpielerService(SpielerRepository spielerRepository,
                          TurnierRepository turnierRepository,
                          VereinRepository vereinRepository) {
        this.spielerRepository = spielerRepository;
        this.turnierRepository = turnierRepository;
        this.vereinRepository = vereinRepository;
    }

    @Transactional
    public SpielerResponseDTO createSpieler(SpielerCreateDTO spielerDto){

        if (spielerDto == null) {
            throw new InvalidRequestException("Spieler-Daten dürfen nicht leer sein");
        }
        if (spielerDto.Name() == null || spielerDto.Name().isBlank()) {
            throw new InvalidRequestException("Ein Spieler braucht einen Namen");
        }
        if (spielerDto.rating() != null && spielerDto.rating() < 0) {
            throw new InvalidRequestException("Rating darf nicht negativ sein");
        }
        if (spielerDto.alter() != null && spielerDto.alter() < 0) {
            throw new InvalidRequestException("Alter darf nicht negativ sein");
        }

        Spieler spieler = SpielerMapper.toEntity(spielerDto);
        List<Turnier> Turniere = turnierRepository.findAllById(spielerDto.turnierIds());
        spieler.setTurnier(Turniere);

        if (spielerDto.vereinId() != null) {
            Verein verein = vereinRepository.findById(spielerDto.vereinId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Verein mit Id: " + spielerDto.vereinId() + " wurde nicht gefunden"));
            spieler.setVerein(verein);
        }

        spielerRepository.save(spieler);
        return SpielerMapper.toDto(spieler);

    }

    @Transactional(readOnly = true) // Dirty Checking wird ausgeschalten -> schneller
    public List<SpielerResponseDTO> getAllSpieler(){
        return spielerRepository.findAll()
                .stream()//Jedes Objekt wird einzeln durchgereicht
                .map(SpielerMapper::toDto)//Objekt aus DB wird auf DTO gemappt
                .collect(Collectors.toList()); //sammelt DTOs in einer neun Liste
    }

    @Transactional(readOnly = true)
    public SpielerResponseDTO getSpieler(int Id){
        return spielerRepository.findById(Id)
                .map(SpielerMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Spieler mit Id: " + Id + "wurde nicht gefunden"));
    }


    @Transactional
    public SpielerResponseDTO updateSpieler(int Id, SpielerUpdateDTO spieler){//update dto fehlt
        Spieler existing = spielerRepository.findById(Id)
                .orElseThrow(() -> new ResourceNotFoundException("Spieler mit Id: " + Id + "wurde nicht gefunden"));

        if(!spieler.spielerId().equals(existing.getSpielerId())){
            System.out.println("Spieler Ids stimmen nicht überein");
            return null;
        }

       existing.setName(spieler.Name());
       existing.setRating(spieler.rating());
       existing.setAge(spieler.alter());
       Spieler saved = spielerRepository.save(existing);
        return SpielerMapper.toDto(saved);
    }

    @Transactional
    public void deleteSpieler(int Id){
        if(!spielerRepository.existsById(Id)){
            throw new ResourceNotFoundException("Spieler nicht gefunden");
        }
       spielerRepository.deleteById(Id);
    }

    @Transactional
      public void createSpieler(Person p){
        Spieler spieler = new Spieler();

        spieler.setName(p.getName());
        LocalDate Geburtstag =  p.getGeburtsatum();
        LocalDate heute = LocalDate.now();
        int alter = Period.between(Geburtstag, heute).getYears();
        spieler.setAge(alter);

        if(p instanceof Mitglied){
            spieler.setRating(((Mitglied) p).getElo());
        }else{
            spieler.setRating(1800);
        }
        spielerRepository.save(spieler);
    }

}
