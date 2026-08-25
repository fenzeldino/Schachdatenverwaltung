package io.github.fenzeldino.schachdatenverwaltung.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Zentrales Exception Handling: bisher fielen "nicht gefunden"-Fälle als
 * unbehandelte Exception bis zum generischen 500 durch, Validierungsfehler
 * wurden teils gar nicht als Fehler signalisiert (Silent-Failure mit
 * System.out.println + return null). Jetzt sauberes 404 bzw. 400 mit
 * JSON-Fehlerbody.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    public record ErrorResponse(String message) {
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequest(InvalidRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }
}
