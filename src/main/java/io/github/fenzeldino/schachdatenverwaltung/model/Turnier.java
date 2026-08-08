package io.github.fenzeldino.schachdatenverwaltung.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Turnier {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int TurnierId;

    // Alle drei nullable: es gibt bereits produktive Turnier-Datensätze ohne
    // diese Felder, und ddl-auto=update kann eine NOT-NULL-Spalte nicht
    // nachträglich auf eine befüllte Tabelle legen (siehe Identity-Problem
    // vom 04.08. und dieselbe Entscheidung bei Spieler.verein).
    @Column(name = "TurnierName")
    private String name;

    private LocalDate datum;

    private String ort;

    @Enumerated(EnumType.STRING)
    private TurnierStatus status;

    @ManyToMany()
    private List<Spieler> Spieler = new ArrayList<Spieler>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="turnierId")
    private List<MatchUp> Matchups = new ArrayList<MatchUp>();

    static int idCount = 0;



    public Turnier(){

    }

    public Turnier(int turnierId){
        this.TurnierId = turnierId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public void setDatum(LocalDate datum) {
        this.datum = datum;
    }

    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    public TurnierStatus getStatus() {
        return status;
    }

    public void setStatus(TurnierStatus status) {
        this.status = status;
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
