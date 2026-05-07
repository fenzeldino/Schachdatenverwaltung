package io.github.fenzeldino.Schachdatenverwaltung.Model;

import java.util.Comparator;

public class MemberNameComparator implements Comparator<Mitglied> {


    @Override
    public int compare(Mitglied o1, Mitglied o2) {

        int RueckgabeWert = o1.vorname.compareTo(o2.vorname);

        if (RueckgabeWert == 0) {
            RueckgabeWert = o1.name.compareTo(o2.name);
            return RueckgabeWert;
        }
        return RueckgabeWert;
    }
}