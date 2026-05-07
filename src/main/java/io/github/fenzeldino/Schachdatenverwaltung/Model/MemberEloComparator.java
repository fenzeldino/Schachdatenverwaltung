package io.github.fenzeldino.Schachdatenverwaltung.Model;

import java.util.Comparator;

public class MemberEloComparator implements Comparator<Mitglied> {

    @Override
    public int compare(Mitglied m1, Mitglied m2){
        return Integer.compare(m1.getElo(),m2.getElo());
    }
}
