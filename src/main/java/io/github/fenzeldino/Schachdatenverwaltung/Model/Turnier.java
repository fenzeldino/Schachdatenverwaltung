package io.github.fenzeldino.Schachdatenverwaltung.Model;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Scanner;

public class Turnier implements RatingCalculator{

    Scanner meinScanner = new Scanner(System.in);


    private ArrayList<Spieler> TurnierSpieler = new ArrayList<Spieler>();
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

    public void addErgebnis(int MatchUpId,int wahl){
        MatchUp matchup = getMatchUpById(MatchUpId);

        if (matchup == null) {
            System.out.println("Fehler: Kein Match mit der ID " + MatchUpId + " gefunden.");
            return;
        }
        System.out.println("Match gefunden");

        switch(wahl){
            case 1: matchup.setErg(GewOdVer.Gewinner,matchup.getSpieler1());
                    matchup.setErg(GewOdVer.Verlierer,matchup.getSpieler2());
                    System.out.println("Setzen des Gewinners und Verlieres");
                    break;
            case 2: matchup.setErg(GewOdVer.Gewinner,matchup.getSpieler2());
                    matchup.setErg(GewOdVer.Verlierer,matchup.getSpieler1());
                    break;
            case 3: matchup.setErg(GewOdVer.Remie,matchup.getSpieler1());
                    matchup.setErg(GewOdVer.Remie,matchup.getSpieler2());
        }
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

            if(matchUp.getErg().isEmpty()){
                System.out.println("Ergebnis wurde noch nicht gesetzt");
                return;
            }
        Spieler Gewinner;
        Spieler Verlierer;

        Gewinner = matchUp.getErg().get(GewOdVer.Gewinner);
        Verlierer = matchUp.getErg().get(GewOdVer.Verlierer);

        if(Gewinner == null || Verlierer == null){
            Gewinner = matchUp.getErg().get(GewOdVer.Remie);
            Verlierer = matchUp.getErg().get(GewOdVer.Remie);
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

            Spieler a = matchUp.getSpieler1();
            Spieler b = matchUp.getSpieler2();

            double ra = a.getRating();
            double rb = b.getRating();

            // Erwartungswert für A
            double ea = 1.0 / (1.0 + Math.pow(10, (rb - ra) / 400.0));
            double eb = 1.0 - ea; // Erwartungswert B ist immer der Rest zu 1

            // Ergebnis bestimmen
            double sa = (matchUp.getErg().get(GewOdVer.Gewinner) == a) ? 1.0 : 0.0;
            if (matchUp.getErg().get(GewOdVer.Remie) != null) sa = 0.5;
            double sb = 1.0 - sa;

            int k = 20; // Dein gewählter K-Faktor

            // Neue Ratings setzen
            a.setRating(ra + k * (sa - ea));
            b.setRating(rb + k * (sb - eb));
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
