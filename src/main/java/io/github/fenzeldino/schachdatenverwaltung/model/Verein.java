package io.github.fenzeldino.schachdatenverwaltung.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Persistierter Schachverein.
 *
 * Bewusst getrennt von {@link SchachVerein}: jene Klasse ist ein reines
 * In-Memory-Domänenobjekt (Domain-Events, Mitglieder-Liste) ohne JPA-Annotationen
 * und ohne Repository. Sie kann nicht direkt zur Entity gemacht werden, weil sie
 * eine {@code List<Mitglied>} hält und Mitglied/Person ebenfalls nicht persistiert
 * sind.
 *
 * Offener Punkt: Die Vereinszugehörigkeit existiert damit an zwei Stellen —
 * hier über die Spieler-Beziehung und in {@link Mitglied#ZPSCodeVerein}.
 * Diese Doppelung ist bekannt und bewusst in Kauf genommen; die Zusammenführung
 * von Spieler und Mitglied ist ein eigener, größerer Umbau.
 */
@Entity
public class Verein {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int vereinId;

    @Column(name = "VereinName", nullable = false)
    private String name;

    /** ZPS-Code des Vereins (Zentrale Personenstammdatei des DSB), z. B. "C0327". */
    @Column(name = "ZPSCode")
    private String zpsCode;

    /**
     * Gegenstück zu {@link Spieler#getVerein()}. {@code mappedBy} bedeutet:
     * die Fremdschlüsselspalte liegt auf der Spieler-Tabelle, nicht hier.
     */
    @OneToMany(mappedBy = "verein")
    private List<Spieler> spieler = new ArrayList<>();

    public Verein() {
    }

    public Verein(String name) {
        this.name = name;
    }

    public Verein(String name, String zpsCode) {
        this.name = name;
        this.zpsCode = zpsCode;
    }

    public int getVereinId() {
        return vereinId;
    }

    public void setVereinId(int vereinId) {
        this.vereinId = vereinId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZpsCode() {
        return zpsCode;
    }

    public void setZpsCode(String zpsCode) {
        this.zpsCode = zpsCode;
    }

    public List<Spieler> getSpieler() {
        return spieler;
    }

    public void setSpieler(List<Spieler> spieler) {
        this.spieler = spieler;
    }

    @Override
    public String toString() {
        return "Verein{" +
                "vereinId=" + vereinId +
                ", name='" + name + '\'' +
                ", zpsCode='" + zpsCode + '\'' +
                '}';
    }
}
