package io.github.fenzeldino.schachdatenverwaltung.model;

public interface RatingCalculator {

    public void EloBerehcnung(int TurnierId,int MatchId);
    public void DresdenCalculator(int TurnierId,int MatchId);
    public MatchUp getMatchUpById(int id);
}
