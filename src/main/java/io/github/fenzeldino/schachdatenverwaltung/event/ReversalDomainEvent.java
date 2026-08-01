package io.github.fenzeldino.schachdatenverwaltung.service;

import io.github.fenzeldino.schachdatenverwaltung.event.DomainEvent;

import java.time.LocalDate;

public abstract class ReversalDomainEvent extends DomainEvent {

    public ReversalDomainEvent(LocalDate occuredDate){
        super(occuredDate);
    }

    public abstract void reversal();
}