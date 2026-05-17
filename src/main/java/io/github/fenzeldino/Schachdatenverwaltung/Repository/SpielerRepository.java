package io.github.fenzeldino.Schachdatenverwaltung.Repository;

import io.github.fenzeldino.Schachdatenverwaltung.Model.Spieler;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpielerRepository extends JpaRepository<Spieler,Integer> {

    Spieler findByNameContainingIgnoreCase(String name);
}
