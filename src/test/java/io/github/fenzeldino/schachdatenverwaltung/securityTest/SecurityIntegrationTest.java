package io.github.fenzeldino.schachdatenverwaltung.securityTest;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prüft die tatsächliche Security-Filterkette über den echten HTTP-Stack
 * (nicht nur die einzelnen Klassen isoliert) — das ist der Beleg, den der
 * CSO-Block auf PR #8 (Frontend-Repo) verlangt hat.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllSpieler_shouldReturnUnauthorized_withoutToken() throws Exception {
        mockMvc.perform(get("/api/Spieler/getAllSpieler"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllSpieler_shouldReturnUnauthorized_withMalformedToken() throws Exception {
        mockMvc.perform(get("/api/Spieler/getAllSpieler")
                        .header("Authorization", "Bearer kaputt.token.hier"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_shouldReturnUnauthorized_forWrongPassword() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"admin","password":"falsch"}"""))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_shouldReturnToken_andAllowAccessToProtectedEndpoint() throws Exception {
        String responseBody = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"admin","password":"changeme123"}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn().getResponse().getContentAsString();

        String token = JsonPath.read(responseBody, "$.token");

        mockMvc.perform(get("/api/Spieler/getAllSpieler")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
