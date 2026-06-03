package io.github.fenzeldino.Schachdatenverwaltung.Service;

import io.github.fenzeldino.Schachdatenverwaltung.DTO.MatchUpDTO;
import io.github.fenzeldino.Schachdatenverwaltung.DTO.SpielerDTO;
import io.github.fenzeldino.Schachdatenverwaltung.Mapper.MatchUpMapper;
import io.github.fenzeldino.Schachdatenverwaltung.Mapper.SpielerMapper;
import io.github.fenzeldino.Schachdatenverwaltung.Mapper.TurnierMapper;
import io.github.fenzeldino.Schachdatenverwaltung.Model.*;
import io.github.fenzeldino.Schachdatenverwaltung.Repository.MatchUpRepository;
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

    @Transactional
    void AddMatchUpToDB(int TurnierId,Spieler spieler1,Spieler spieler2){
        Turnier turnier = turnierRepository.findById(TurnierId)
                        .orElseThrow(() -> new IllegalArgumentException("Turnier nicht gefunden"));

        turnier.createMatchUo(spieler1,spieler2);
        turnierRepository.save(turnier);

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
