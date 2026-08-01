package io.github.fenzeldino.schachdatenverwaltung.repository;

import io.github.fenzeldino.schachdatenverwaltung.model.MatchUp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchUpRepository extends JpaRepository<MatchUp,Integer> {
}
