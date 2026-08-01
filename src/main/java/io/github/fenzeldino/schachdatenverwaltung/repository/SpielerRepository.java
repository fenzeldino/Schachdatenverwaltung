package io.github.fenzeldino.schachdatenverwaltung.repository;

import io.github.fenzeldino.schachdatenverwaltung.model.Spieler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpielerRepository extends JpaRepository<Spieler,Integer> {

    Spieler findByNameContainingIgnoreCase(String name);
}
