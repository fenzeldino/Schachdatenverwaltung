package io.github.fenzeldino.Schachdatenverwaltung.Service;

import io.github.fenzeldino.Schachdatenverwaltung.DTO.MatchUpDTO;
import io.github.fenzeldino.Schachdatenverwaltung.DTO.Request.MatchUp.MatchUpCreateDTO;
import io.github.fenzeldino.Schachdatenverwaltung.DTO.Request.MatchUp.MatchUpUpdateDTO;
import io.github.fenzeldino.Schachdatenverwaltung.DTO.Response.MatchUp.MatchUpResponseDTO;
import io.github.fenzeldino.Schachdatenverwaltung.DTO.SpielerDTO;
import io.github.fenzeldino.Schachdatenverwaltung.Mapper.MatchUpMapper;
import io.github.fenzeldino.Schachdatenverwaltung.Model.MatchUp;
import io.github.fenzeldino.Schachdatenverwaltung.Model.Spieler;
import io.github.fenzeldino.Schachdatenverwaltung.Model.Turnier;
import io.github.fenzeldino.Schachdatenverwaltung.Repository.MatchUpRepository;
import io.github.fenzeldino.Schachdatenverwaltung.Repository.SpielerRepository;
import io.github.fenzeldino.Schachdatenverwaltung.Repository.TurnierRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MatchUpService {

    private final MatchUpRepository matchUpRepository;
    private final TurnierRepository turnierRepository;
    private final SpielerRepository spielerRepository;

    public MatchUpService(MatchUpRepository matchUpRepository, TurnierRepository turnierRepository,SpielerRepository spielerRepository){
        this.matchUpRepository = matchUpRepository;
        this.turnierRepository = turnierRepository;
        this.spielerRepository = spielerRepository;
    }

    @Transactional
    public MatchUpResponseDTO createMatchUp(MatchUpCreateDTO match){
        MatchUp matchUp = new MatchUp();
        matchUp.setSpieler1(match.spielerEins());
        matchUp.setSpieler2(match.spielerZwei());
        Turnier turnier = turnierRepository.findById(match.turnierId())
                        .orElseThrow(() -> new RuntimeException("Turnier nicht vorhanden"));

        matchUp.setTurnier(turnier);
        MatchUp saved = matchUpRepository.save(matchUp);
        return MatchUpMapper.toDto(saved);
    }

    @Transactional
    public List<MatchUpResponseDTO> getAllMatchUpsFromDb(){
        return matchUpRepository.findAll()
                .stream()
                .map(MatchUpMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public MatchUpResponseDTO updateMatchUp(int Id, MatchUpUpdateDTO match){
        MatchUp existing = matchUpRepository.findById(Id)
                .orElseThrow(() -> new IllegalArgumentException("MatchUp mit Id: " + Id + " nicht vorhanden"));

        existing.setSpieler1(spielerRepository.findById(match.spielerEinsId())
                .orElseThrow(() ->new IllegalArgumentException("Spieler1 nicht gefunden"))); //update Spieler1
        existing.setSpieler2(spielerRepository.findById(match.spielerZweiId())
                .orElseThrow(() ->new IllegalArgumentException("Spieler2 nicht gefunden"))); //update Spieler2
        existing.setTurnier(turnierRepository.findById(match.turnierId())
                .orElseThrow(() -> new IllegalArgumentException("Turnier nicht gefunden"))); //update Turnier
        existing.setGewinner(spielerRepository.findById(match.gewinnerId())
                .orElseThrow(() -> new IllegalArgumentException("Gewinner id nicht gefunden"))); //update Gewinner

        MatchUp saved = matchUpRepository.save(existing);
        return MatchUpMapper.toDto(saved);
    }

    @Transactional
    public void deleteMatchUpById(Integer Id){

        if(!matchUpRepository.existsById(Id)){
            throw new IllegalArgumentException("MatchUp nicht gefunden");
        }
        matchUpRepository.deleteById(Id);
    }

    @Transactional
    public void addGewinner(MatchUp match, Spieler gewinner){
        if(gewinner.getSpielerId() == match.getSpieler1().getSpielerId() || gewinner.getSpielerId() == match.getSpieler2().getSpielerId()) {
            match.setGewinner(gewinner);
            matchUpRepository.save(match);
        }else {
            System.out.println("Spieler nicht im Matchup gefunden");
            return;
        }
    }

    @Transactional
    public void addVerlierer(MatchUp match, Spieler verlierer){
        if(verlierer.getSpielerId() == match.getSpieler1().getSpielerId() || verlierer.getSpielerId() == match.getSpieler2().getSpielerId()) {
            match.setGewinner(verlierer);
            matchUpRepository.save(match);
        }else {
            System.out.println("Spieler nicht im Matchup gefunden");
            return;
        }
    }

}
