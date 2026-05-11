package io.github.fenzeldino.Schachdatenverwaltung.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;


public class Person implements Comparable<Person>{


    protected int PersonenId;
    protected String vorname;
    protected String name;
    protected Geschlecht geschlecht;
    protected LocalDate geburtsdatum;
    protected Adresse adresse;
    protected String email;
    protected String telefonnummer;


    public Person(int PersonenId,String vorname, String name, Geschlecht geschlecht, LocalDate geburtdsatum, Adresse adresse, String email, String telefonnummer) {
        this.PersonenId = PersonenId;
        this.vorname = vorname;
        this.name = name;
        this.geschlecht = geschlecht;
        this.geburtsdatum = geburtdsatum;
        this.adresse = adresse;
        this.email = email;
        this.telefonnummer = telefonnummer;
    }

    public Person(String vorname, String name, Geschlecht geschlecht, LocalDate geburtdsatum, Adresse adresse, String email, String telefonnummer) {
        this.vorname = vorname;
        this.name = name;
        this.geschlecht = geschlecht;
        this.geburtsdatum = geburtdsatum;
        this.adresse = adresse;
        this.email = email;
        this.telefonnummer = telefonnummer;
    }
    public Person(){

    }

    public Person(String vorname, String name){
        this.vorname = vorname;
        this.name = name;
    }

    public Person(String vorname, LocalDate geburtsdatum){
        this.vorname = vorname;
        this.geburtsdatum = geburtsdatum;
    }

    public int getPersonenId() {
        return PersonenId;
    }

    public void setPersonenId(int personenId) {
        PersonenId = personenId;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Geschlecht getGeschlecht() {
        return geschlecht;
    }

    public void setGeschlecht(Geschlecht geschlecht) {
        this.geschlecht = geschlecht;
    }

    public LocalDate getGeburtsatum() {
        return geburtsdatum;
    }

    public void setGeburtsatum(LocalDate geburtsdatum) {
        this.geburtsdatum = geburtsdatum;
    }

    public Adresse getAdresse() {
        return adresse;
    }

    public void setAdresse(Adresse adresse) {
        this.adresse = adresse;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefonnummer() {
        return telefonnummer;
    }

    public void setTelefonnummer(String telefonnummer) {
        this.telefonnummer = telefonnummer;
    }

    public int compareTo(Person person){
        return this.geburtsdatum.compareTo(person.geburtsdatum);
    }

    @Override
    public String toString() {
        return "Person:" +
                "vorname='" + vorname + '\'' +
                ", name='" + name + '\'' +
                ", geschlecht=" + geschlecht +
                ", geburtsatum=" + geburtsdatum +
                ", adresse=" + adresse +
                ", email='" + email + '\'' +
                ", telefonnummer=" + telefonnummer +
                '}';
    }
}
