package io.github.fenzeldino.schachdatenverwaltung.securityTest;

import io.github.fenzeldino.schachdatenverwaltung.security.JwtService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private static final String SECRET = "test-secret-key-mindestens-32-bytes-lang-fuer-hs256";

    @Test
    void generateToken_shouldProduceValidToken() {
        JwtService jwtService = new JwtService(SECRET, 3600000);

        String token = jwtService.generateToken("admin");

        assertTrue(jwtService.isValid(token));
        assertEquals("admin", jwtService.extractUsername(token));
    }

    @Test
    void isValid_shouldReturnFalse_forMalformedToken() {
        JwtService jwtService = new JwtService(SECRET, 3600000);

        assertFalse(jwtService.isValid("kaputt.token.hier"));
    }

    @Test
    void isValid_shouldReturnFalse_forExpiredToken() throws InterruptedException {
        JwtService jwtService = new JwtService(SECRET, 1);

        String token = jwtService.generateToken("admin");
        Thread.sleep(10);

        assertFalse(jwtService.isValid(token));
    }

    @Test
    void isValid_shouldReturnFalse_forTokenSignedWithDifferentSecret() {
        JwtService jwtServiceA = new JwtService(SECRET, 3600000);
        JwtService jwtServiceB = new JwtService("anderer-geheimer-schluessel-mindestens-32-bytes-lang", 3600000);

        String token = jwtServiceA.generateToken("admin");

        assertFalse(jwtServiceB.isValid(token));
    }
}
