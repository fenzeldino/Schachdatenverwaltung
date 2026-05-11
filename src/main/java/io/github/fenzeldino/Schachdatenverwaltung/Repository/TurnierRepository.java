package io.github.fenzeldino.Schachdatenverwaltung.Repository;

import io.github.fenzeldino.Schachdatenverwaltung.Model.Turnier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TurnierRepository extends JpaRepository<Turnier,Integer> {
}
