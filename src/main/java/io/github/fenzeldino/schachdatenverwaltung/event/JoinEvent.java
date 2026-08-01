package io.github.fenzeldino.schachdatenverwaltung.event;

import io.github.fenzeldino.schachdatenverwaltung.model.Person;
import io.github.fenzeldino.schachdatenverwaltung.model.SchachVerein;
import io.github.fenzeldino.schachdatenverwaltung.service.ReversalDomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public class JoinEvent extends ReversalDomainEvent{

    private static final Logger log = LoggerFactory.getLogger(JoinEvent.class);

    private Person person;
    private SchachVerein verein;
    private int Elo;
    private SchachVerein priorState; // Vorheriger stnadn

    public JoinEvent(LocalDate date, Person person, SchachVerein verein, int Elo){
        super(date);
        log.info("created at "+date.toString()+" für Verein " + verein.getName()+ "person: " + person.getName());
        this.person = person;
        this.verein = verein;
    }

    @Override
    public void reversal(){
        verein.handleReversalJoin(this);
    }


    @Override
    public void process(){
        log.info("process");
        this.priorState = verein; // vorheriger stand zugewiesen, dann erst änderungen vornehmen
        verein.handleAnmeldung(this);
    }

    public JoinEvent setPerson(Person person){
        this.person = person;
        return this;
    }

    public JoinEvent setVerein(SchachVerein verein){
        this.verein = verein;
        return this;
    }

    public SchachVerein getPriorVereinStand(){
        return priorState;
    }

    public Person getPerson() {
        return person;
    }

    public SchachVerein getVerein() {
        return verein;
    }

    public int getElo() {
        return Elo;
    }

    public void setElo(int elo) {
        Elo = elo;
    }
}

