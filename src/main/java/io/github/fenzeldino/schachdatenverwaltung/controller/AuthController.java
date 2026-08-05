package io.github.fenzeldino.schachdatenverwaltung.controller;

import io.github.fenzeldino.schachdatenverwaltung.dto.request.auth.LoginRequestDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.auth.LoginResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.security.AdminAuthenticationService;
import io.github.fenzeldino.schachdatenverwaltung.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AdminAuthenticationService adminAuthenticationService;
    private final JwtService jwtService;

    public AuthController(AdminAuthenticationService adminAuthenticationService, JwtService jwtService) {
        this.adminAuthenticationService = adminAuthenticationService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginDTO) {
        if (!adminAuthenticationService.authenticate(loginDTO.username(), loginDTO.password())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtService.generateToken(loginDTO.username());
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }
}
