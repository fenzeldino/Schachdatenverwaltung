package io.github.fenzeldino.Schachdatenverwaltung.Model;

import jakarta.persistence.*;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Scanner;
@Entity
public class Turnier implements RatingCalculator{



    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="TunierId")
    private int TunierId;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="tunierId")
    private ArrayList<Spieler> TurnierSpieler = new ArrayList<Spieler>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="tunierId")
    private ArrayList<MatchUp> Matchups = new ArrayList<MatchUp>();

    static int idCount = 0;


    private static final int[][] PUNKTE_TABELLE = {
        {5,2,0}, // 0-50 Punkte
        {6,3,0}, //51-100Punkte
        {7,4,0}, //101-200Punkte
        {8,5,0}  //>200 Punkte
    };


    public Turnier(){

    }

    public void createSpieler(Person p){
        Spieler spieler = new Spieler();

        spieler.setSpielerId(idCount);
        spieler.setName(p.getName());
        LocalDate Geburtstag =  p.getGeburtsatum();
        LocalDate heute = LocalDate.now();
        int alter = Period.between(Geburtstag, heute).getYears();
        spieler.setAge(alter);

        if(p instanceof Mitglied){
            spieler.setRating(((Mitglied) p).getElo());
        }else{
            spieler.setRating(1800);
        }
        idCount++;
    }

    public void createMatchUp(Spieler spieler1, Spieler spieler2){
            MatchUp matchup = new MatchUp(spieler1, spieler2);
            Matchups.add(matchup);
    }

    public void addErgebnis(int MatchUpId,Spieler Gewinner){
        MatchUp matchup = getMatchUpById(MatchUpId);

        if (matchup == null) {
            System.out.println("Fehler: Kein Match mit der ID " + MatchUpId + " gefunden.");
            return;
        }
        System.out.println("Match gefunden");
        matchup.setGewinner(Gewinner);

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

    public void setTunierspieler(Spieler spieler){
        TurnierSpieler.add(spieler);

    }

    public MatchUp getMatchUpById(int id){
        for(MatchUp m: Matchups){
            if(m.getMatchUpId() == id){
                return m;
            }
        }
        return null;
    }

    @Override
    public void DresdenCalculator(int MatchId) {
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

    }

    @Override
    public void EloBerehcnung(int MatchUpId) {
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
    }


    public void anzeigenTunierSpieler(){
        for(Spieler tun : TurnierSpieler){
            System.out.println(tun.toString());
        }
    }

    public void anzeigenTunierGegner(){
        for(MatchUp matchup : Matchups){
            System.out.println(matchup.toString());
        }
    }


}
