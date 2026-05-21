package io.github.fenzeldino.Schachdatenverwaltung.Service;

import io.github.fenzeldino.Schachdatenverwaltung.Model.MatchUp;
import io.github.fenzeldino.Schachdatenverwaltung.Model.Spieler;
import io.github.fenzeldino.Schachdatenverwaltung.Model.Turnier;
import io.github.fenzeldino.Schachdatenverwaltung.Repository.TurnierRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class TurnierService {

    private TurnierRepository turnierRepository;

    public TurnierService(TurnierRepository turnierRepository){
        this.turnierRepository = turnierRepository;
    }




}
