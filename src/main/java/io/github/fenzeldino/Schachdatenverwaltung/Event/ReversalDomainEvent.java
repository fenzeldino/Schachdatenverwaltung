package io.github.fenzeldino.Schachdatenverwaltung.Service;

import io.github.fenzeldino.Schachdatenverwaltung.Event.DomainEvent;

import java.time.LocalDate;

public abstract class ReversalDomainEvent extends DomainEvent {

    public ReversalDomainEvent(LocalDate occuredDate){
        super(occuredDate);
    }

    public abstract void reversal();
}