package io.github.fenzeldino.Schachdatenverwaltung.Event;

import io.github.fenzeldino.Schachdatenverwaltung.Model.Person;
import io.github.fenzeldino.Schachdatenverwaltung.Model.SchachVerein;
import io.github.fenzeldino.Schachdatenverwaltung.Service.ReversalDomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public class JoinEvent extends ReversalDomainEvent{

    private static final Logger log = LoggerFactory.getLogger(JoinEvent.class);

    private Person person;
    private SchachVerein verein;
    private int Elo;

    public JoinEvent(LocalDate date, Person person, SchachVerein verein, int Elo){
        super(date);
        log.info("created at "+date.toString()+" für Verein " + verein.getName()+ "person: " + person.getName());
        this.person = person;
        this.verein = verein;
    }

    /*
    @Override
    public void reversal(){
        verein.MitgliedAusClubEntfernen(this);
    }
    */
    @Override
    public void process(){
        log.info("process");
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

