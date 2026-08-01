package io.github.fenzeldino.schachdatenverwaltung.event;

import io.github.fenzeldino.schachdatenverwaltung.model.Mitglied;
import io.github.fenzeldino.schachdatenverwaltung.model.SchachVerein;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public class LeaveEvent extends DomainEvent{

    private static final Logger log = LoggerFactory.getLogger(LeaveEvent.class);


    private SchachVerein verein;
    private Mitglied mitglied;

    public LeaveEvent(LocalDate date, SchachVerein verein, Mitglied mitglied){
        super(date);
        log.info("created at "+date.toString()+" für Verein "+ verein.getName()+" Mitglied: cargo "+ mitglied.getName());
        this.verein = verein;
        this.mitglied = mitglied;
    }

    @Override
    public void process(){
        log.info("process");
        verein.MitgliedAusClubEntfernen(this);
    }

    public SchachVerein getVerein() {
        return verein;
    }

    public Mitglied getMitglied() {
        return mitglied;
    }

    public LeaveEvent setVerein(SchachVerein verein){
        this.verein = verein;
        return this;
    }

    public LeaveEvent setMitglied(Mitglied mitglied){
        this.mitglied = mitglied;
        return this;
    }
}
