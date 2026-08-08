package io.github.fenzeldino.schachdatenverwaltung.repository;

import io.github.fenzeldino.schachdatenverwaltung.model.Spieler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpielerRepository extends JpaRepository<Spieler,Integer> {

    Spieler findByNameContainingIgnoreCase(String name);

    /**
     * Alle Spieler eines Vereins. Spring Data leitet daraus den Join über die
     * Fremdschlüsselspalte verein_id ab — der Unterstrich trennt dabei explizit
     * die Beziehung (verein) vom Feld der Zielentity (vereinId).
     */
    List<Spieler> findByVerein_VereinId(Integer vereinId);

    /** Spieler ohne Vereinszuordnung — nach der Migration zunächst alle bestehenden. */
    List<Spieler> findByVereinIsNull();
}
