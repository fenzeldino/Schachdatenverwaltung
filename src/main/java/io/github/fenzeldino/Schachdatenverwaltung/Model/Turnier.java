package io.github.fenzeldino.Schachdatenverwaltung.Model;

import io.github.fenzeldino.Schachdatenverwaltung.DTO.MatchUpDTO;
import jakarta.persistence.*;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
@Entity
public class Turnier {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int TurnierId;

    @ManyToMany()
    private List<Spieler> Spieler = new ArrayList<Spieler>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="turnierId")
    private List<MatchUp> Matchups = new ArrayList<MatchUp>();

    static int idCount = 0;



    public Turnier(){

    }

    public List<Spieler> getSpieler() {
        return Spieler;
    }

    public void setSpieler(List<Spieler> spieler) {
        Spieler = spieler;
    }

    public List<MatchUp> getMatchups() {
        return Matchups;
    }

    public void setMatchups(MatchUp matchup) {
        Matchups.add(matchup);
    }

    public int getTunierId() {
        return TurnierId;
    }

    public void setTunierId(int turnierId) {
        TurnierId = turnierId;
    }


    public void setTunierspieler(Spieler spieler) {
        Spieler.add(spieler);
    }

    public static int getIdCount() {
        return idCount;
    }

    public static void setIdCount(int idCount) {
        Turnier.idCount = idCount;
    }

    public void createMatchUo(Spieler spieler1,Spieler spieler2) {
        MatchUp matchUp = new MatchUp();
        matchUp.setSpieler1(spieler1);
        matchUp.setSpieler2(spieler2);

        Matchups.add(matchUp);
    }


}
