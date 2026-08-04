package io.github.fenzeldino.schachdatenverwaltung.service;

import io.github.fenzeldino.schachdatenverwaltung.dto.request.matchUp.MatchUpCreateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.request.matchUp.MatchUpUpdateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.matchUp.MatchUpResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.mapper.MatchUpMapper;
import io.github.fenzeldino.schachdatenverwaltung.model.MatchUp;
import io.github.fenzeldino.schachdatenverwaltung.model.Spieler;
import io.github.fenzeldino.schachdatenverwaltung.model.Turnier;
import io.github.fenzeldino.schachdatenverwaltung.repository.MatchUpRepository;
import io.github.fenzeldino.schachdatenverwaltung.repository.SpielerRepository;
import io.github.fenzeldino.schachdatenverwaltung.repository.TurnierRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
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

    /* Für den Controller: setzt den Gewinner per ID statt roher Entities. */
    @Transactional
    public void addGewinner(int matchUpId, int gewinnerId){
        MatchUp match = matchUpRepository.findById(matchUpId)
                .orElseThrow(() -> new IllegalArgumentException("MatchUp nicht gefunden"));
        Spieler gewinner = spielerRepository.findById(gewinnerId)
                .orElseThrow(() -> new IllegalArgumentException("Spieler nicht gefunden"));
        addGewinner(match, gewinner);
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

    /* Für den Controller: setzt den Verlierer per ID statt roher Entities. */
    @Transactional
    public void addVerlierer(int matchUpId, int verliererId){
        MatchUp match = matchUpRepository.findById(matchUpId)
                .orElseThrow(() -> new IllegalArgumentException("MatchUp nicht gefunden"));
        Spieler verlierer = spielerRepository.findById(verliererId)
                .orElseThrow(() -> new IllegalArgumentException("Spieler nicht gefunden"));
        addVerlierer(match, verlierer);
    }

}
