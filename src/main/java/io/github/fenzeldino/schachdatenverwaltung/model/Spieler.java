package io.github.fenzeldino.schachdatenverwaltung.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Spieler {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int spielerId;
    @Column(name="SpielerName")
    private String name;
    @Column(name="Rating")
    private double rating;
    @Column(name="Alter")
    private int age;
    @ManyToMany(mappedBy = "Spieler")
    private List<Turnier> turnier = new ArrayList<>();

    /**
     * Verein, dem dieser Spieler angehört. Bewusst nullable: die bestehenden
     * Spieler-Datensätze in der Produktiv-DB haben noch keinen Verein, und
     * {@code ddl-auto=update} kann eine NOT-NULL-Spalte nicht nachträglich auf
     * eine befüllte Tabelle legen.
     *
     * Fetch-Typ bewusst EAGER (JPA-Default für ManyToOne): der SpielerMapper
     * liest den Vereinsnamen mit und wird auch außerhalb einer Transaktion
     * aufgerufen (z. B. in Mapper-Tests) — LAZY würde dort eine
     * LazyInitializationException auslösen.
     */
    @ManyToOne
    @JoinColumn(name = "verein_id")
    private Verein verein;

    public Spieler(String name, double rating, int age) {
        this.name = name;
        this.rating = rating;
        this.age = age;
    }

    public Spieler(int spielerId,String name, double rating, int age,List<Turnier> turnier) {
        this.spielerId = spielerId;
        this.name = name;
        this.rating = rating;
        this.age = age;
        this.turnier = turnier;
    }

    public Spieler(String name, double rating, int age,List<Turnier> turnier) {
        this.name = name;
        this.rating = rating;
        this.age = age;
        this.turnier = turnier;
    }

    public List<Turnier> getTurnier() {
        return turnier;
    }



    public void setTurnier(List<Turnier> turnier) {
        this.turnier = turnier;
    }

    public Spieler(){}

    public Verein getVerein() {
        return verein;
    }

    public void setVerein(Verein verein) {
        this.verein = verein;
    }

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
