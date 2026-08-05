package io.github.fenzeldino.schachdatenverwaltung.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Prüft Login-Credentials gegen den einen konfigurierten Admin-Zugang.
 * Bewusst minimal gehalten: es gibt aktuell keine Nutzerverwaltung, nur
 * einen gemeinsamen Zugang fürs Frontend. Passwort wird als BCrypt-Hash
 * konfiguriert, niemals im Klartext.
 */
@Service
public class AdminAuthenticationService {

    private final String adminUsername;
    private final String adminPasswordHash;
    private final PasswordEncoder passwordEncoder;

    public AdminAuthenticationService(@Value("${admin.username}") String adminUsername,
                                       @Value("${admin.password-hash}") String adminPasswordHash,
                                       PasswordEncoder passwordEncoder) {
        this.adminUsername = adminUsername;
        this.adminPasswordHash = adminPasswordHash;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean authenticate(String username, String rawPassword) {
        if (username == null || rawPassword == null) {
            return false;
        }
        return adminUsername.equals(username) && passwordEncoder.matches(rawPassword, adminPasswordHash);
    }
}
