package io.github.fenzeldino.Schachdatenverwaltung.Service;

import io.github.fenzeldino.Schachdatenverwaltung.DTO.SpielerDTO;
import io.github.fenzeldino.Schachdatenverwaltung.Mapper.SpielerMapper;
import io.github.fenzeldino.Schachdatenverwaltung.Model.Spieler;
import io.github.fenzeldino.Schachdatenverwaltung.Repository.SpielerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SpielerService {

    private final SpielerRepository spielerRepository;


    public SpielerService(SpielerRepository spielerRepository){
        this.spielerRepository = spielerRepository;
    }

    @Transactional
    public SpielerDTO createSpieler(SpielerDTO spielerDto){
        Spieler spieler = new Spieler();
        spieler.setName(spielerDto.Name());
        spieler.setRating(Objects.requireNonNull(spielerDto).rating());

        spielerRepository.save(spieler);
        Spieler saved = spielerRepository.save(spieler); //saved -> Spieler mit Auto generated Id durch Speichern in DB
        return SpielerMapper.toDto(saved);
    }

    @Transactional(readOnly = true) // Dirty Checking wird ausgeschalten -> schneller
    public List<SpielerDTO> getAllSpieler(){
        return spielerRepository.findAll()
                .stream()//Jedes Objekt wird einzeln durchgereicht
                .map(SpielerMapper::toDto)//Objekt aus DB wird auf DTO gemappt
                .collect(Collectors.toList()); //sammelt DTOs in einer neun Liste
    }

    @Transactional(readOnly = true)
    public SpielerDTO getSpieler(int Id){
        return spielerRepository.findById(Id)
                .map(SpielerMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Spieler mit Id: " + Id + "wurde nicht gefunden"));
    }

}
