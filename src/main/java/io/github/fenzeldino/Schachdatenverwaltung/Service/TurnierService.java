package io.github.fenzeldino.Schachdatenverwaltung.Service;

import io.github.fenzeldino.Schachdatenverwaltung.DTO.MatchUpDTO;
import io.github.fenzeldino.Schachdatenverwaltung.DTO.SpielerDTO;
import io.github.fenzeldino.Schachdatenverwaltung.Mapper.MatchUpMapper;
import io.github.fenzeldino.Schachdatenverwaltung.Mapper.SpielerMapper;
import io.github.fenzeldino.Schachdatenverwaltung.Mapper.TurnierMapper;
import io.github.fenzeldino.Schachdatenverwaltung.Model.MatchUp;
import io.github.fenzeldino.Schachdatenverwaltung.Model.Spieler;
import io.github.fenzeldino.Schachdatenverwaltung.Model.Turnier;
import io.github.fenzeldino.Schachdatenverwaltung.Repository.SpielerRepository;
import io.github.fenzeldino.Schachdatenverwaltung.Repository.TurnierRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


@Service
@Transactional
public class TurnierService {

    private final SpielerRepository spielerRepository;
    private final TurnierRepository turnierRepository;

    public TurnierService(TurnierRepository turnierRepository, SpielerRepository spielerRepository){
        this.turnierRepository = turnierRepository;
        this.spielerRepository = spielerRepository;
    }

    @Transactional
    public Turnier findTurnierById(int turnierId){
        return turnierRepository.findById(turnierId)
                .orElseThrow(() -> new IllegalArgumentException("Turnier wurde nicht gefunden"));
    }


    @Transactional
    public List<SpielerDTO> showAllTurnierSpieler(int turnierId,Set<Integer> spielerIds){

        Turnier turnier = findTurnierById(turnierId);
        List<Spieler> turnierSpieler = turnier.getSpieler().stream()//Spieler Liste einzeln durchgehen
            .filter(spieler -> spielerIds.contains(spieler.getSpielerId())) // Schauen ob der Spieler eine SpielerId hat die im Set drinnen ist
           .toList();

        return turnierSpieler.stream().map(SpielerMapper::toDto).toList();
    }
    @Transactional
    public List<MatchUpDTO> showAllMatchUps(int turnierId,Set<Integer> MatchUpIds){

        Turnier turnier = findTurnierById(turnierId);
        List<MatchUp> TurnierMatchUps = turnier.getMatchups().
                stream()
                .filter(MatchUp -> MatchUpIds.contains(MatchUp.getMatchUpId()))
                .toList();

        return TurnierMatchUps
                .stream()
                .map(MatchUpMapper::toDto)
                .toList();
    }

    @Transactional
    public void addMatchUpToTurnier(int TurnierId,MatchUp match){
        Turnier turnier = findTurnierById(TurnierId);
        turnier.setMatchups(match);
        turnierRepository.save(turnier);

    }

    @Transactional
    public void addSpielerToTurnier(int TurnierId,Spieler spieler){
        Turnier turnier = findTurnierById(TurnierId);
        turnier.setTunierspieler(spieler);
        turnierRepository.save(turnier);
    }




}
