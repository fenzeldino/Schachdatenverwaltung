package io.github.fenzeldino.Schachdatenverwaltung.Model;

import java.util.ArrayList;
import java.util.List;

public class SchachVerein {

    private int id;
    private String name;
    private List<Mitglied> mitglieder;
    private int MitgliederAnzahl;

    public SchachVerein(int id, String name) {
        this.id = id;
        this.name = name;
        this.mitglieder = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Mitglied> getMitglieder() {
        return mitglieder;
    }

    public void addMember(Mitglied mitglied) {
        members.add(mitglied);
    }

    public void removeMember(Mitglied member) {
        members.remove(member);
    }

    public int getMitgliederAnzahl() {
        return MitgliederAnzahl;
    }

    public void setMitgliederAnzahl(int mitgliederAnzahl) {
        MitgliederAnzahl = mitgliederAnzahl;
    }

    @Override
    public String toString() {
        return "ChessClub{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", members=" + members +
                '}';
    }
}