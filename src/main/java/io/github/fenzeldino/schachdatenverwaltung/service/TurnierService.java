package io.github.fenzeldino.schachdatenverwaltung.service;

import io.github.fenzeldino.schachdatenverwaltung.dto.request.turnier.TurnierCreateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.request.turnier.TurnierUpdateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.matchUp.MatchUpResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.spieler.SpielerResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.turnier.TurnierResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.turnier.VereinImTurnierDTO;
import io.github.fenzeldino.schachdatenverwaltung.mapper.MatchUpMapper;
import io.github.fenzeldino.schachdatenverwaltung.mapper.SpielerMapper;
import io.github.fenzeldino.schachdatenverwaltung.mapper.TurnierMapper;
import io.github.fenzeldino.schachdatenverwaltung.model.*;
import io.github.fenzeldino.schachdatenverwaltung.repository.MatchUpRepository;
import io.github.fenzeldino.schachdatenverwaltung.repository.SpielerRepository;
import io.github.fenzeldino.schachdatenverwaltung.repository.TurnierRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Transactional
public class TurnierService {

    private final SpielerRepository spielerRepository;
    private final TurnierRepository turnierRepository;
    private final MatchUpRepository matchUpRepository;
    private final RatingService ratingService;

    public TurnierService(TurnierRepository turnierRepository, SpielerRepository spielerRepository,MatchUpRepository matchUpRepository, RatingService ratingService){
        this.turnierRepository = turnierRepository;
        this.spielerRepository = spielerRepository;
        this.matchUpRepository = matchUpRepository;
        this.ratingService = ratingService;
    }

    @Transactional
    public Turnier findTurnierById(int turnierId){
        return turnierRepository.findById(turnierId)
                .orElseThrow(() -> new IllegalArgumentException("Turnier wurde nicht gefunden"));
    }

    @Transactional
    public TurnierResponseDTO createTurnier(TurnierCreateDTO turnierDto){

        if(turnierDto == null){
            System.out.println("Leere Argument kann nicht verarbeitet werden");
            return null;
        }

        Turnier turnier = new Turnier();
        turnier.setName(turnierDto.name());
        turnier.setDatum(turnierDto.datum());
        turnier.setOrt(turnierDto.ort());
        // Status wird bei der Erstellung nicht vom Client vorgegeben — ein neues
        // Turnier ist per Definition geplant, nicht laufend oder abgeschlossen.
        turnier.setStatus(TurnierStatus.GEPLANT);

        if(turnierDto.spielerIds() != null){
            List<Spieler> spieler = spielerRepository.findAllById(turnierDto.spielerIds());
            turnier.setSpieler(spieler);
        }

        Turnier saved = turnierRepository.save(turnier);
        return TurnierMapper.toDto(saved);
    }

    @Transactional
    public List<TurnierResponseDTO> getAllTurniere(){
        return turnierRepository.findAll()
                .stream()
                .map(TurnierMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public TurnierResponseDTO getTurnier(int id){
        return TurnierMapper.toDto(findTurnierById(id));
    }

    @Transactional
    public TurnierResponseDTO updateTurnier(int id, TurnierUpdateDTO turnierDto){
        Turnier existing = findTurnierById(id);

        if(!turnierDto.turnierId().equals(existing.getTunierId())){
            System.out.println("Turnier Ids stimmen nicht überein");
            return null;
        }

        existing.setName(turnierDto.name());
        existing.setDatum(turnierDto.datum());
        existing.setOrt(turnierDto.ort());
        existing.setStatus(turnierDto.status());

        if(turnierDto.spielerIds() != null){
            List<Spieler> spieler = spielerRepository.findAllById(turnierDto.spielerIds());
            existing.setSpieler(spieler);
        }

        Turnier saved = turnierRepository.save(existing);
        return TurnierMapper.toDto(saved);
    }

    @Transactional
    public void deleteTurnier(int id){
        if(!turnierRepository.existsById(id)){
            throw new IllegalArgumentException("Turnier nicht gefunden");
        }
        turnierRepository.deleteById(id);
    }


    @Transactional
    public List<SpielerResponseDTO> showAllTurnierSpieler(int turnierId, Set<Integer> spielerIds){

        Turnier turnier = findTurnierById(turnierId);
        List<Spieler> turnierSpieler = turnier.getSpieler().stream()//Spieler Liste einzeln durchgehen
            .filter(spieler -> spielerIds.contains(spieler.getSpielerId())) // Schauen ob der Spieler eine SpielerId hat die im Set drinnen ist
           .toList();

        return turnierSpieler.stream().map(SpielerMapper::toDto).toList();
    }
    @Transactional
    public List<MatchUpResponseDTO> showAllMatchUps(int turnierId, Set<Integer> MatchUpIds){

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

    /**
     * Vereine, die im Turnier vertreten sind, mit Teilnehmerzahl je Verein.
     * Spieler ohne Verein tragen nicht bei (siehe Spieler.verein, nullable —
     * die Vereins-Zuordnung ist noch nicht für alle Spieler nachgepflegt).
     *
     * Gruppierung nach vereinId statt nach der Verein-Entity selbst: Verein
     * hat kein eigenes equals()/hashCode(), Gruppierung über den
     * Entity-Identitätsvergleich wäre also nicht verlässlich.
     */
    @Transactional
    public List<VereinImTurnierDTO> getVereineImTurnier(int turnierId){
        Turnier turnier = findTurnierById(turnierId);

        Map<Integer, List<Spieler>> spielerProVerein = turnier.getSpieler().stream()
                .filter(spieler -> spieler.getVerein() != null)
                .collect(Collectors.groupingBy(spieler -> spieler.getVerein().getVereinId()));

        return spielerProVerein.values().stream()
                .map(gruppe -> new VereinImTurnierDTO(
                        gruppe.get(0).getVerein().getVereinId(),
                        gruppe.get(0).getVerein().getName(),
                        gruppe.size()
                ))
                .sorted(Comparator.comparing(VereinImTurnierDTO::name))
                .toList();
    }

    /* Verknüpft ein bereits existierendes MatchUp per ID mit dem Turnier. */
    @Transactional
    public void addMatchUpToTurnier(int turnierId, int matchUpId){
        Turnier turnier = findTurnierById(turnierId);
        MatchUp matchUp = getMatchUpById(matchUpId);
        turnier.setMatchups(matchUp);
        turnierRepository.save(turnier);
    }

    /* Fügt einen bereits existierenden Spieler per ID zum Turnier hinzu. */
    @Transactional
    public void addSpielerToTurnier(int turnierId, int spielerId){
        Turnier turnier = findTurnierById(turnierId);
        Spieler spieler = spielerRepository.findById(spielerId)
                .orElseThrow(() -> new IllegalArgumentException("Spieler nicht gefunden"));
        turnier.setTunierspieler(spieler);
        turnierRepository.save(turnier);
    }

    /* Erstellt ein neues MatchUp zwischen zwei bereits existierenden Spielern per ID. */
    @Transactional
    public void addMatchUpToDB(int turnierId, int spieler1Id, int spieler2Id){
        Turnier turnier = turnierRepository.findById(turnierId)
                        .orElseThrow(() -> new IllegalArgumentException("Turnier nicht gefunden"));
        Spieler spieler1 = spielerRepository.findById(spieler1Id)
                .orElseThrow(() -> new IllegalArgumentException("Spieler1 nicht gefunden"));
        Spieler spieler2 = spielerRepository.findById(spieler2Id)
                .orElseThrow(() -> new IllegalArgumentException("Spieler2 nicht gefunden"));

        turnier.createMatchUo(spieler1,spieler2);
        turnierRepository.save(turnier);
    }

    @Transactional
    public RatingResult DresdenCalculator(int TurnierId,int MatchId) {
        MatchUp matchUp = getMatchUpById(MatchId);

        Spieler Gewinner = matchUp.getGewinner();
        Spieler Verlierer = getVerlierer(MatchId);

        if(Gewinner == null){
            throw new IllegalArgumentException("Gewinner wurde noch nicht gesetzt");
        }

        RatingResult result = ratingService.dresden(Gewinner.getRating(), Gewinner.getAge(), Verlierer.getRating());

        Gewinner.setRating(result.neuesGewinnerRating());
        Verlierer.setRating(result.neuesVerliererRating());

        spielerRepository.save(Gewinner);
        spielerRepository.save(Verlierer);

        return result;
    }

    public RatingResult EloBerehcnung(int TurnierId,int MatchUpId) {
        MatchUp matchUp = getMatchUpById(MatchUpId);

        Spieler Gewinner = matchUp.getGewinner();
        Spieler Verlierer = getVerlierer(MatchUpId);

        RatingResult result = ratingService.elo(Gewinner.getRating(), Verlierer.getRating());

        Gewinner.setRating(result.neuesGewinnerRating());
        Verlierer.setRating(result.neuesVerliererRating());

        spielerRepository.save(Gewinner);
        spielerRepository.save(Verlierer);

        return result;
    }

    public Spieler getVerlierer(int MatchId){
        MatchUp match = getMatchUpById(MatchId);

        Spieler Gewinner;
        Spieler Verlierer;

        Gewinner = match.getGewinner();
        if(Gewinner == match.getSpieler1()){
            Verlierer = match.getSpieler2();
        }else{
            Verlierer = match.getSpieler1();
        }
        return Verlierer;
    }

    @Transactional
    public MatchUp getMatchUpById(int MatchUpId){

        return matchUpRepository.findById(MatchUpId)
                .orElseThrow(() -> new IllegalArgumentException("MatchUp nicht gefunden"));

    }

}
