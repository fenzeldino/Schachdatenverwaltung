package io.github.fenzeldino.Schachdatenverwaltung.Model;

import java.time.LocalDate;

public class Mitglied extends Person{

    protected String ZPS;
    protected int mitgliedsnummer;
    protected String ZPSCodeVerein;
    protected LocalDate eintrittsdatum;
    protected LocalDate austrittsdatum;
    protected Status status;
    protected long DWZ;
    protected int elo;
    protected String FIDEtitel;

    public Mitglied(){
    }

    public Mitglied(String vorname,String name, int elo){
        super(vorname,name);
        this.elo = elo;
    }

    public Mitglied(String vorname, String name, Geschlecht geschlecht, LocalDate geburtsdatum, Adresse adresse, String email, String telefonnummer, String ZPS, int mitgliedsnummer, String ZPSCodeVerein, LocalDate eintrittsdatum, LocalDate austrittsdatum, Status status, long DWZ, int elo, String FIDEtitel) {
        super(vorname,name,geschlecht,geburtsdatum,adresse,email,telefonnummer);
        this.ZPS = ZPSCodeVerein + "-" + mitgliedsnummer;
        this.mitgliedsnummer = mitgliedsnummer;
        this.ZPSCodeVerein = ZPSCodeVerein;
        this.eintrittsdatum = LocalDate.now();
        this.austrittsdatum = austrittsdatum;
        this.status = status;
        this.DWZ = DWZ;
        this.elo = elo;
        this.FIDEtitel = FIDEtitel;
    }

    public int getMitgliedsnummer() {
        return mitgliedsnummer;
    }

    public String getMemberZPS(){
        return ZPS;
    }

    public void setMitgliedsnummer(int mitgliedsnummer) {
        mitgliedsnummer = mitgliedsnummer;
    }

    public LocalDate getEintrittsdatum() {
        return eintrittsdatum;
    }

    public void setEintrittsdatum(LocalDate eintrittsdatum) {
        this.eintrittsdatum = eintrittsdatum;
    }

    public LocalDate getAustrittsdatum() {
        return austrittsdatum;
    }

    public void setAustrittsdatum(LocalDate austrittsdatum) {
        this.austrittsdatum = austrittsdatum;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getDWZ() {
        return DWZ;
    }

    public void setDWZ(long DWZ) {
        this.DWZ = DWZ;
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public String getFIDEtitel() {
        return FIDEtitel;
    }

    public void setFIDEtitel(String FIDEtitel) {
        this.FIDEtitel = FIDEtitel;
    }

    @Override
    public String
    toString() {
        return super.toString() +
                "ZPS='" + ZPS + '\'' +
                ", ZPSCodeVerein=" + ZPSCodeVerein +
                ", eintrittsdatum=" + eintrittsdatum +
                ", austrittsdatum=" + austrittsdatum +
                ", DWZ=" + DWZ +
                ", elo=" + elo +
                '}';
    }
}
