package io.github.fenzeldino.Schachdatenverwaltung.Model;

import jakarta.persistence.*;

import java.util.HashMap;

@Entity
public class MatchUp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int MatchUpId;

    @ManyToOne
    @JoinColumn(name="spieler1_fk")
    private Spieler Spieler1;

    @ManyToOne
    @JoinColumn(name="spieler2_fk")
    private Spieler Spieler2;

    @ManyToOne
    @JoinColumn(name="turnierId_fk")
    private Turnier turnier;

    @OneToOne
    @JoinColumn(name="GewinnerId_fk")
    private Spieler Gewinner;

    @Column(name="status")
    private GewOdVer status;

    private static int MatchUpIdCount = 0;
    public MatchUp(Spieler Spieler1, Spieler Spieler2){
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
