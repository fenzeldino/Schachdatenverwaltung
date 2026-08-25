package io.github.fenzeldino.schachdatenverwaltung.exception;

/**
 * Der Request selbst ist ungültig (null-DTO, ID-Mismatch, Spieler nicht
 * Teil des Matches, ...). Wird vom GlobalExceptionHandler auf 400 gemappt.
 */
public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
    }
}
