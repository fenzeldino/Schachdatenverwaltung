package io.github.fenzeldino.schachdatenverwaltung.service;

import io.github.fenzeldino.schachdatenverwaltung.event.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class EventProcessor {

    private static final Logger log = LoggerFactory.getLogger(EventProcessor.class);

    List logList = new ArrayList<DomainEvent>();

    public void process(DomainEvent domainEvent){
        log.info("process "+domainEvent);
        domainEvent.process();
        logList.add(domainEvent);
    }
}
