package io.github.fenzeldino.Schachdatenverwaltung.Model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class Spieler {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int spielerId;
    private String name;
    private double rating;
    private int age;

    public Spieler(String name, double rating, int age) {
        this.name = name;
        this.rating = rating;
        this.age = age;
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
