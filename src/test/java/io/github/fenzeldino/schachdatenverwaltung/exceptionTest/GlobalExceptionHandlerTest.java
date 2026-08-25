package io.github.fenzeldino.schachdatenverwaltung.exceptionTest;

import io.github.fenzeldino.schachdatenverwaltung.controller.TurnierController;
import io.github.fenzeldino.schachdatenverwaltung.exception.InvalidRequestException;
import io.github.fenzeldino.schachdatenverwaltung.exception.ResourceNotFoundException;
import io.github.fenzeldino.schachdatenverwaltung.security.JwtService;
import io.github.fenzeldino.schachdatenverwaltung.service.TurnierService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifiziert das tatsächliche HTTP-Mapping des GlobalExceptionHandlers
 * (@RestControllerAdvice wird von @WebMvcTest automatisch mit eingebunden)
 * über einen echten MockMvc-Roundtrip: 404/400 plus JSON-Fehlerbody. Die
 * Service-Tests prüfen nur, dass die Exceptions geworfen werden — nicht,
 * dass sie tatsächlich auf den richtigen HTTP-Status gemappt werden.
 *
 * Security-Filterkette bewusst deaktiviert (addFilters = false): dieser
 * Test prüft Exception-Mapping, nicht Auth — die Security-Filterkette
 * selbst deckt SecurityIntegrationTest bereits ab. JwtAuthFilter wird als
 * Bean trotzdem instanziiert (@WebMvcTest zieht Filter-Beans mit ein),
 * braucht also einen JwtService-Mock, auch wenn er nie aufgerufen wird.
 */
@WebMvcTest(controllers = TurnierController.class)
@AutoConfigureMockMvc(addFilters = false)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TurnierService turnierService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void resourceNotFoundException_shouldMapTo404_withErrorBody() throws Exception {
        when(turnierService.getTurnier(99)).thenThrow(new ResourceNotFoundException("Turnier wurde nicht gefunden"));

        mockMvc.perform(get("/api/Turnier/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Turnier wurde nicht gefunden"));
    }

    @Test
    void invalidRequestException_shouldMapTo400_withErrorBody() throws Exception {
        when(turnierService.getTurnier(1)).thenThrow(new InvalidRequestException("Ungültige Anfrage"));

        mockMvc.perform(get("/api/Turnier/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ungültige Anfrage"));
    }
}
