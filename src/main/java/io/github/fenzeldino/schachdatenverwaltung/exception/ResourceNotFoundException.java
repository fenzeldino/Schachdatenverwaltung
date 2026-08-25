package io.github.fenzeldino.schachdatenverwaltung.exception;

/**
 * Eine angeforderte Ressource (Turnier, Spieler, MatchUp, Verein, ...)
 * existiert nicht. Wird vom GlobalExceptionHandler auf 404 gemappt.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
