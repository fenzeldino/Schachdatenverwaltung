package io.github.fenzeldino.Schachdatenverwaltung.Event;

import io.github.fenzeldino.Schachdatenverwaltung.Model.Mitglied;
import io.github.fenzeldino.Schachdatenverwaltung.Model.SchachVerein;
import org.hibernate.event.spi.LoadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public class LeaveEvent extends DomainEvent{

    private static final Logger log = LoggerFactory.getLogger(LoadEvent.class);


    private SchachVerein verein;
    private Mitglied mitglied;

    public LeaveEvent(LocalDate date, SchachVerein verein, Mitglied mitglied){
        super(date);

    }


    public void process(){

    }
}
