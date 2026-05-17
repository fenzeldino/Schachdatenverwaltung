package io.github.fenzeldino.Schachdatenverwaltung.Service;

import io.github.fenzeldino.Schachdatenverwaltung.DTO.SpielerDTO;
import io.github.fenzeldino.Schachdatenverwaltung.Mapper.SpielerMapper;
import io.github.fenzeldino.Schachdatenverwaltung.Model.Spieler;
import io.github.fenzeldino.Schachdatenverwaltung.Repository.SpielerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
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

}
