package io.github.fenzeldino.Schachdatenverwaltung.Service;

import io.github.fenzeldino.Schachdatenverwaltung.DTO.MatchUpDTO;
import io.github.fenzeldino.Schachdatenverwaltung.Mapper.MatchUpMapper;
import io.github.fenzeldino.Schachdatenverwaltung.Model.MatchUp;
import io.github.fenzeldino.Schachdatenverwaltung.Model.Spieler;
import io.github.fenzeldino.Schachdatenverwaltung.Repository.MatchUpRepository;
import io.github.fenzeldino.Schachdatenverwaltung.Repository.SpielerRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchUpService {

    private final MatchUpRepository matchUpRepository;
    private final SpielerRepository spielerRepository;

    public MatchUpService(MatchUpRepository matchUpRepository,SpielerRepository spielerRepository){
        this.matchUpRepository = matchUpRepository;
        this.spielerRepository =spielerRepository;
    }

    @Transactional
    public MatchUpDTO createMatUp(MatchUpDTO match){
        MatchUp matchUp = new MatchUp();


        matchUp.setSpieler1(match.SpielerEins());
        matchUp.setSpieler1(match.SpielerEins());

        MatchUp saved = matchUpRepository.save(matchUp);
        return MatchUpMapper.toDto(saved);
    }

    @Transactional
    public List<MatchUpDTO> getAllMatchUps(){
        return matchUpRepository.findAll()
                .stream()
                .map(MatchUpMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public MatchUpDTO updateMatchUp(int Id,MatchUpDTO match){
        MatchUp existing = matchUpRepository.findById(Id)
                .orElseThrow(() -> new IllegalArgumentException("MatchUp mit Id: " + Id + " nicht vorhanden"));

        if(!match.MatchUpId().equals(existing.getMatchUpId())){
            System.out.println("MatchUp mit Id: " + Id + " passt nicht zu der MatchUpId von: " + existing.getMatchUpId());
            return null;
        }

        existing.setSpieler1(match.SpielerEins());
        existing.setSpieler2(match.SpielerZwei());

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


}
