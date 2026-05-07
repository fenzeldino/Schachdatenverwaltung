package io.github.fenzeldino.Schachdatenverwaltung.Repository;

import io.github.fenzeldino.Schachdatenverwaltung.Model.Spieler;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpielerRepository extends JpaRepository<Spieler,Integer> {
}
