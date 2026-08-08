package io.github.fenzeldino.schachdatenverwaltung.repository;

import io.github.fenzeldino.schachdatenverwaltung.model.Verein;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VereinRepository extends JpaRepository<Verein, Integer> {

    Optional<Verein> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}
