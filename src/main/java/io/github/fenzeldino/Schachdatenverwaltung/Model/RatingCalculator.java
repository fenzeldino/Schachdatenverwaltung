package io.github.fenzeldino.Schachdatenverwaltung.Model;

public interface RatingCalculator {

    public void EloBerehcnung(int MatchId);
    public void DresdenCalculator(int MatchId);
    public MatchUp getMatchUpById(int id);
}
