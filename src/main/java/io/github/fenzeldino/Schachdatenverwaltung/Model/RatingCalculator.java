package io.github.fenzeldino.Schachdatenverwaltung.Model;

public interface RatingCalculator {

    public void EloBerehcnung(int TurnierId,int MatchId);
    public void DresdenCalculator(int TurnierId,int MatchId);
    public MatchUp getMatchUpById(int id);
}
