package io.github.fenzeldino.Schachdatenverwaltung.Model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.HashMap;

public class MatchUp {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int MatchUpId;
    private Spieler Spieler1;
    private Spieler Spieler2;
    HashMap<GewOdVer, Spieler> erg = new HashMap<GewOdVer, Spieler>();

    private static int MatchUpIdCount = 0;
    public MatchUp(Spieler Spieler1, Spieler Spieler2){
        this.MatchUpId = MatchUpIdCount;
        MatchUpIdCount++;
        this.Spieler1 = Spieler1;
        this.Spieler2 = Spieler2;
    }

    public int getMatchUpId() {
        return MatchUpId;
    }

    public void setMatchUpId(int matchUpId) {
        MatchUpId = matchUpId;
    }

    public Spieler getSpieler1() {
        return Spieler1;
    }

    public Spieler getSpieler2() {
        return Spieler2;
    }

    public void setSpieler1(Spieler spieler1) {
        Spieler1 = spieler1;
    }

    public void setSpieler2(Spieler spieler2) {
        Spieler2 = spieler2;
    }

    public HashMap<GewOdVer, Spieler> getErg() {
        return erg;
    }

    public void setErg(GewOdVer GewinnerOderVerliererOderRemie, Spieler spieler) {
        erg.put(GewinnerOderVerliererOderRemie,spieler);
    }


    @Override
    public String toString() {
        return "MatchUp Id: " + MatchUpId + " " + Spieler1.getName() + "( Alter: " + Spieler1.getAge() + ") "  + "(" + Spieler1.getRating() + ") " + " vs " + Spieler2.getName() + " (Alter: " + Spieler2.getAge() + ") " +  "(" +Spieler2.getRating() + ") ";
    }
}
