package io.github.fenzeldino.Schachdatenverwaltung.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.HashMap;

@Entity
public class MatchUp {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int MatchUpId;
    private Spieler Spieler1;
    private Spieler Spieler2;
    private Turnier tunier;
    private Spieler Gewinner;
    private GewOdVer status;

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

    public Spieler getGewinner() {
        return Gewinner;
    }

    public void setGewinner(Spieler spieler) {
        this.Gewinner = spieler;
    }


    @Override
    public String toString() {
        return "MatchUp Id: " + MatchUpId + " " + Spieler1.getName() + "( Alter: " + Spieler1.getAge() + ") "  + "(" + Spieler1.getRating() + ") " + " vs " + Spieler2.getName() + " (Alter: " + Spieler2.getAge() + ") " +  "(" +Spieler2.getRating() + ") ";
    }
}
