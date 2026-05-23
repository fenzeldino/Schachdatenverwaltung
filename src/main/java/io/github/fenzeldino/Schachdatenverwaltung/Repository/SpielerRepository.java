package io.github.fenzeldino.Schachdatenverwaltung.Repository;

import io.github.fenzeldino.Schachdatenverwaltung.Model.Spieler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpielerRepository extends JpaRepository<Spieler,Integer> {

    Spieler findByNameContainingIgnoreCase(String name);
}
