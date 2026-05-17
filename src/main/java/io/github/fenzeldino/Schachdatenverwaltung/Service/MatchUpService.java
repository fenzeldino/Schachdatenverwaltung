package io.github.fenzeldino.Schachdatenverwaltung.Service;

import io.github.fenzeldino.Schachdatenverwaltung.DTO.MatchUpDTO;
import io.github.fenzeldino.Schachdatenverwaltung.Mapper.MatchUpMapper;
import io.github.fenzeldino.Schachdatenverwaltung.Model.MatchUp;
import io.github.fenzeldino.Schachdatenverwaltung.Model.Spieler;
import io.github.fenzeldino.Schachdatenverwaltung.Repository.MatchUpRepository;
import io.github.fenzeldino.Schachdatenverwaltung.Repository.SpielerRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class MatchUpService {

    private MatchUpRepository matchUpRepository;
    private SpielerRepository spielerRepository;

    public MatchUpService(MatchUpRepository matchUpRepository,SpielerRepository spielerRepository){
        this.matchUpRepository = matchUpRepository;
        this.spielerRepository =spielerRepository;
    }

    @Transactional
    public MatchUpDTO createMatUp(MatchUpDTO match){
        MatchUp matchUp = new MatchUp();
        String spielerNameEins = match.SpielerNameEins();
        String spielerNameZwei = match.SpielerNameZwei();
        Spieler SpielerEins;
        Spieler SpielerZwei;
        SpielerEins = spielerRepository.findByNameContainingIgnoreCase(match.SpielerNameEins());
        SpielerZwei = spielerRepository.findByNameContainingIgnoreCase(match.SpielerNameZwei());
        matchUp.setSpieler1(SpielerEins);
        matchUp.setSpieler1(SpielerEins);

        MatchUp saved = matchUpRepository.save(matchUp);
        return MatchUpMapper.toDto(saved);
    }
}
