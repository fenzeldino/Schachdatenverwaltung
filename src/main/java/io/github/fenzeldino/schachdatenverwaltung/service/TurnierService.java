package io.github.fenzeldino.schachdatenverwaltung.service;

import io.github.fenzeldino.schachdatenverwaltung.dto.request.turnier.TurnierCreateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.request.turnier.TurnierUpdateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.matchUp.MatchUpResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.spieler.SpielerResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.turnier.TurnierResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.mapper.MatchUpMapper;
import io.github.fenzeldino.schachdatenverwaltung.mapper.SpielerMapper;
import io.github.fenzeldino.schachdatenverwaltung.mapper.TurnierMapper;
import io.github.fenzeldino.schachdatenverwaltung.model.*;
import io.github.fenzeldino.schachdatenverwaltung.repository.MatchUpRepository;
import io.github.fenzeldino.schachdatenverwaltung.repository.SpielerRepository;
import io.github.fenzeldino.schachdatenverwaltung.repository.TurnierRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Transactional
public class TurnierService implements RatingCalculator {

    private final SpielerRepository spielerRepository;
    private final TurnierRepository turnierRepository;
    private final MatchUpRepository matchUpRepository;

    public TurnierService(TurnierRepository turnierRepository, SpielerRepository spielerRepository,MatchUpRepository matchUpRepository){
        this.turnierRepository = turnierRepository;
        this.spielerRepository = spielerRepository;
        this.matchUpRepository = matchUpRepository;
    }

    private static final int[][] PUNKTE_TABELLE = {
            {5,2,0}, // 0-50 Punkte
            {6,3,0}, //51-100Punkte
            {7,4,0}, //101-200Punkte
            {8,5,0}  //>200 Punkte
    };

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

    @Transactional
    public void addMatchUpToTurnier(int TurnierId,MatchUp match){
        Turnier turnier = findTurnierById(TurnierId);
        turnier.setMatchups(match);
        turnierRepository.save(turnier);

    }

    /* Für den Controller: verknüpft ein bereits existierendes MatchUp per ID mit dem Turnier. */
    @Transactional
    public void addMatchUpToTurnier(int turnierId, int matchUpId){
        MatchUp matchUp = getMatchUpById(matchUpId);
        addMatchUpToTurnier(turnierId, matchUp);
    }

    @Transactional
    public void addSpielerToTurnier(int TurnierId,Spieler spieler){
        Turnier turnier = findTurnierById(TurnierId);
        turnier.setTunierspieler(spieler);
        turnierRepository.save(turnier);
    }

    /* Für den Controller: fügt einen bereits existierenden Spieler per ID zum Turnier hinzu. */
    @Transactional
    public void addSpielerToTurnier(int turnierId, int spielerId){
        Spieler spieler = spielerRepository.findById(spielerId)
                .orElseThrow(() -> new IllegalArgumentException("Spieler nicht gefunden"));
        addSpielerToTurnier(turnierId, spieler);
    }

    @Transactional
    public void AddMatchUpToDB(int TurnierId,Spieler spieler1,Spieler spieler2){
        Turnier turnier = turnierRepository.findById(TurnierId)
                        .orElseThrow(() -> new IllegalArgumentException("Turnier nicht gefunden"));

        turnier.createMatchUo(spieler1,spieler2);
        turnierRepository.save(turnier);

    }

    /* Für den Controller: erstellt ein neues MatchUp zwischen zwei bereits existierenden Spielern per ID. */
    @Transactional
    public void addMatchUpToDB(int turnierId, int spieler1Id, int spieler2Id){
        Spieler spieler1 = spielerRepository.findById(spieler1Id)
                .orElseThrow(() -> new IllegalArgumentException("Spieler1 nicht gefunden"));
        Spieler spieler2 = spielerRepository.findById(spieler2Id)
                .orElseThrow(() -> new IllegalArgumentException("Spieler2 nicht gefunden"));
        AddMatchUpToDB(turnierId, spieler1, spieler2);
    }

    @Transactional
    @Override
    public void DresdenCalculator(int TurnierId,int MatchId) {
        MatchUp matchUp = getMatchUpById(MatchId);

        Spieler Gewinner;
        Spieler Verlierer;

        Gewinner = matchUp.getGewinner();
        Verlierer = getVerlierer(MatchId);

        if(Gewinner == null){
            System.out.println("Gewinner wurde noch nicht gesetzt");
            return;
        }


        double RatingGewinner = Gewinner.getRating();
        System.out.println("Rating Gewinner: " + RatingGewinner);
        double RatingVerlierer = Verlierer.getRating();
        System.out.println("Rating Verlierer: " + RatingVerlierer);

        double diff = Math.abs(RatingGewinner - RatingVerlierer); //Differenz bestimmen für Punkte_Tabelle
        boolean gewinnerWarFavorit = RatingGewinner >= RatingVerlierer; //Favorit bestimmen

        int zeile;
        if (diff <= 50) zeile = 0;
        else if (diff <= 100) zeile = 1;
        else if (diff <= 150) zeile = 2;
        else zeile = 3;

        // 4. Basispunkte aus deiner PUNKTE_TABELLE holen
        // Spalte 0 = Favoritensieg, Spalte 2 = Außenseitersieg
        int spalte = gewinnerWarFavorit ? 0 : 2;
        int basisPunkte = PUNKTE_TABELLE[zeile][spalte];

        // 5. Faktor aus der DWZ-Matrix (aus dem Bild) holen
        // Hier nutzt du die Methode, die wir zuvor besprochen haben
        double faktor = DwzMatrix.getFactor((int)Gewinner.getRating(), Gewinner.getAge());

        // 6. Finale Berechnung
        double punktZuwachs = basisPunkte * faktor;

        // 7. Ratings aktualisieren
        Gewinner.setRating(RatingGewinner + punktZuwachs);
        System.out.println("Gewinner Rating nach änderung: " + Gewinner.getRating());
        Verlierer.setRating(RatingVerlierer - punktZuwachs); // Bei Dresden meist symmetrisch
        System.out.println("Verlierer Rating nach änderung: " + Verlierer.getRating());

        System.out.println("Berechnung abgeschlossen: " + Gewinner.getName() + " erhält +" + punktZuwachs);

        spielerRepository.save(Gewinner);
        spielerRepository.save(Verlierer);

    }

    @Override
    public void EloBerehcnung(int TurnierId,int MatchUpId) {
        MatchUp matchUp = getMatchUpById(MatchUpId);

        Spieler Gewinner = matchUp.getGewinner();
        Spieler Verlierer = getVerlierer(MatchUpId);

        double ra = Gewinner.getRating();
        double rb = Verlierer.getRating();

        // Erwartungswert für A
        // 2. Erwartungswert berechnen (Ea)
        // Formel: 1 / (1 + 10^((RatingB - RatingA) / 400))
        double ea = 1.0 / (1.0 + Math.pow(10, (rb - ra) / 400.0));

        // 3. K-Faktor festlegen
        // Ein fixer K-Faktor von 20 ist Standard, könnte aber auch dynamisch sein
        int k = 20;

        // 4. Punkte berechnen
        // Da wir hier einen festen Gewinner haben, ist das Ergebnis (Sa) immer 1.0
        // Für ein Remis müsste die MatchUp-Klasse ein entsprechendes Status-Feld prüfen
        double sa = 1.0;
        double punktZuwachs = k * (sa - ea);

        // Neue Ratings setzen
        Gewinner.setRating(ra + punktZuwachs);
        Verlierer.setRating(rb - punktZuwachs);

        spielerRepository.save(Gewinner);
        spielerRepository.save(Verlierer);
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
