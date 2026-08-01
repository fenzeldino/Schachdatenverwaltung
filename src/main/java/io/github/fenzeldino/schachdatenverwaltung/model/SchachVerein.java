package io.github.fenzeldino.schachdatenverwaltung.model;

import io.github.fenzeldino.schachdatenverwaltung.event.JoinEvent;
import io.github.fenzeldino.schachdatenverwaltung.event.LeaveEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class SchachVerein {

    private static final Logger log = LoggerFactory.getLogger(SchachVerein.class);


    private int id;
    private String name;
    private List<Mitglied> mitglieder;
    private int MitgliederAnzahl;

    public SchachVerein(int id, String name) {
        this.id = id;
        this.name = name;
        this.mitglieder = new ArrayList<>();
    }

    public Mitglied createMitglied(Person p){
        Mitglied mitglied = new Mitglied();

        mitglied.setName(p.getName());
        LocalDate Geburtstag =  p.getGeburtsatum();
        LocalDate heute = LocalDate.now();
        int alter = Period.between(Geburtstag, heute).getYears();
        mitglied.setAlter(alter);
        //mitglied.setElo(elo);

        return mitglied;
    }

    public void handleAnmeldung(JoinEvent joinEvent){
        log.info("Handhabung Anmeldung des join Event "+joinEvent+ " in Verein" + this);
        Person person = joinEvent.getPerson();
        mitglieder.add(createMitglied(joinEvent.getPerson()));
        MitgliederAnzahl++;
    }

    public Long activeMembers(){
       return mitglieder.stream().
               filter(mitglied -> mitglied.getAustrittsdatum() == null)
               .count();
    }

    public void MitgliedAusClubEntfernen(LeaveEvent leaveEvent){
        removeMember(leaveEvent.getMitglied());
        leaveEvent.getMitglied().setAustrittsdatum(leaveEvent.getOccuredDate());
        MitgliederAnzahl--;
    }

    public void handleReversalJoin(JoinEvent e){ // Stand zurück setzten
        SchachVerein priorState = e.getPriorVereinStand();
        this.mitglieder = priorState.getMitglieder();
        this.MitgliederAnzahl = priorState.getMitgliederAnzahl();
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Mitglied> getMitglieder() {
        return mitglieder;
    }

    public void addMember(Mitglied mitglied) {
        mitglieder.add(mitglied);
    }

    public void removeMember(Mitglied member) {
        mitglieder.remove(member);
    }

    public int getMitgliederAnzahl() {
        return MitgliederAnzahl;
    }

    public void setMitgliederAnzahl(int mitgliederAnzahl) {
        MitgliederAnzahl = mitgliederAnzahl;
    }

    @Override
    public String toString() {
        return "ChessClub{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", members=" + mitglieder +
                '}';
    }
}