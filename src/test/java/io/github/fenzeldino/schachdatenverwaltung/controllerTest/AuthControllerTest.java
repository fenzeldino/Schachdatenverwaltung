package io.github.fenzeldino.schachdatenverwaltung.controllerTest;

import io.github.fenzeldino.schachdatenverwaltung.controller.AuthController;
import io.github.fenzeldino.schachdatenverwaltung.dto.request.auth.LoginRequestDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.auth.LoginResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.security.AdminAuthenticationService;
import io.github.fenzeldino.schachdatenverwaltung.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AdminAuthenticationService adminAuthenticationService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthController authController;

    @Test
    void login_shouldReturnToken_whenCredentialsValid() {
        LoginRequestDTO request = new LoginRequestDTO("admin", "changeme123");

        when(adminAuthenticationService.authenticate("admin", "changeme123")).thenReturn(true);
        when(jwtService.generateToken("admin")).thenReturn("ein.generiertes.token");

        ResponseEntity<LoginResponseDTO> result = authController.login(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("ein.generiertes.token", result.getBody().token());
        verify(jwtService).generateToken("admin");
    }

    @Test
    void login_shouldReturnUnauthorized_whenCredentialsInvalid() {
        LoginRequestDTO request = new LoginRequestDTO("admin", "falsch");

        when(adminAuthenticationService.authenticate("admin", "falsch")).thenReturn(false);

        ResponseEntity<LoginResponseDTO> result = authController.login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertNull(result.getBody());
        verify(jwtService, never()).generateToken(org.mockito.ArgumentMatchers.anyString());
    }
}
