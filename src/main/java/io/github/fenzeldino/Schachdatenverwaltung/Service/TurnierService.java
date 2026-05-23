package io.github.fenzeldino.Schachdatenverwaltung.Service;

import io.github.fenzeldino.Schachdatenverwaltung.DTO.SpielerDTO;
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
    public List<Spieler> showAllTurnierSpieler(int turnierId,Set<Integer> spielerIds){

        Turnier turnier = turnierRepository.findById(turnierId)
                .orElseThrow(() -> new IllegalArgumentException("Turnier wurde nicht gefunden"));


   List<Spieler> turnierSpieler = turnier.getSpieler().stream()//Spieler Liste einzeln durchgehen
            .filter(spieler -> spielerIds.contains(spieler.getSpielerId())) // Schauen ob der Spieler eine SpielerId hat die im Set drinnen ist
            .toList();

        return turnierSpieler;
    }




}
