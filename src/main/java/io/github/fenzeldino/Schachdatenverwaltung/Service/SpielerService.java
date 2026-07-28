package io.github.fenzeldino.Schachdatenverwaltung.Service;


import io.github.fenzeldino.Schachdatenverwaltung.DTO.Request.Spieler.SpielerCreateDTO;
import io.github.fenzeldino.Schachdatenverwaltung.DTO.Request.Spieler.SpielerDeleteDTO;
import io.github.fenzeldino.Schachdatenverwaltung.DTO.Response.Spieler.SpielerResponseDTO;
import io.github.fenzeldino.Schachdatenverwaltung.DTO.SpielerDTO;
import io.github.fenzeldino.Schachdatenverwaltung.Mapper.SpielerMapper;
import io.github.fenzeldino.Schachdatenverwaltung.Model.Mitglied;
import io.github.fenzeldino.Schachdatenverwaltung.Model.Person;
import io.github.fenzeldino.Schachdatenverwaltung.Model.Spieler;
import io.github.fenzeldino.Schachdatenverwaltung.Model.Turnier;
import io.github.fenzeldino.Schachdatenverwaltung.Repository.SpielerRepository;
import io.github.fenzeldino.Schachdatenverwaltung.Repository.TurnierRepository;
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

    public SpielerService(SpielerRepository spielerRepository,TurnierRepository turnierRepository){
        this.spielerRepository = spielerRepository;
        this.turnierRepository = turnierRepository;
    }

    @Transactional
    public SpielerResponseDTO createSpieler(SpielerCreateDTO spielerDto){
        Spieler spieler = SpielerMapper.toEntity(spielerDto);
        List<Turnier> Turniere = turnierRepository.findAllById(spielerDto.turnierIds());
        spieler.setTurnier(Turniere);
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
                .orElseThrow(() -> new IllegalArgumentException("Spieler mit Id: " + Id + "wurde nicht gefunden"));
    }

    @Transactional
    public SpielerResponseDTO updateSpieler(int Id,SpielerCreateDTO spieler){//update dto fehlt
        Spieler existing = spielerRepository.findById(Id)
                .orElseThrow(() -> new IllegalArgumentException("Spieler mit Id: " + Id + "wurde nicht gefunden"));

        if(!spieler.spielerId().equals(existing.getSpielerId())){
            System.out.println("Spieler Ids stimmen nicht überein");
            return null;
        }

       existing.setName(spieler.Name());
       existing.setRating(spieler.rating());

       Spieler saved = spielerRepository.save(existing);
        return SpielerMapper.toDto(saved);
    }

    @Transactional
    public void deleteSpieler(int Id){
        if(!spielerRepository.existsById(Id)){
            throw new IllegalArgumentException("Spieler nicht gefunden");
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
