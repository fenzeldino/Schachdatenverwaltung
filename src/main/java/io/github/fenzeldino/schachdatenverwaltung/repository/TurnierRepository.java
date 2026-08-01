package io.github.fenzeldino.schachdatenverwaltung.repository;

import io.github.fenzeldino.schachdatenverwaltung.model.Turnier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TurnierRepository extends JpaRepository<Turnier,Integer> {
}
