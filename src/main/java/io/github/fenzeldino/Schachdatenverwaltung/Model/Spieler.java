package io.github.fenzeldino.Schachdatenverwaltung.Model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Spieler {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int spielerId;

    @Column(name="SpielerName")
    private String name;
    @Column(name="Rating")
    private double rating;
    @Column(name="Alter")
    private int age;
    @ManyToMany(mappedBy = "Spieler")
    private List<Turnier> turnier = new ArrayList<>();

    public Spieler(String name, double rating, int age) {
        this.name = name;
        this.rating = rating;
        this.age = age;
    }

    public List<Turnier> getTurnier() {
        return turnier;
    }

    public void setTurnier(List<Turnier> turnier) {
        this.turnier = turnier;
    }

    public Spieler(){}

    public int getSpielerId(){
        return spielerId;
    }

    public void setSpielerId(int spielerId){
        this.spielerId = spielerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Spieler{" +
                "spielerId=" + spielerId +
                ", name='" + name + '\'' +
                ", rating=" + rating +
                ", age=" + age +
                '}';
    }
}
