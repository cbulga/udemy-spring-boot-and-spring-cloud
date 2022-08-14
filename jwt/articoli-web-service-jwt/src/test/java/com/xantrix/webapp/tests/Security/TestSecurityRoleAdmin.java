package com.xantrix.webapp.tests.Security;

import com.xantrix.webapp.Application;
import com.xantrix.webapp.utility.JsonData;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ContextConfiguration(classes = Application.class)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestSecurityRoleAdmin {

    private static final String apiBaseUrl = "/api/articoli";
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .defaultRequest(get("/")
                        .with(user("Admin").roles("USER", "ADMIN"))) //Ruoli Attivati
                .apply(springSecurity()) //Attiva la sicurezza
                .build();
    }

    @Test
    @Order(1)
    void listArtByCodArt() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(apiBaseUrl + "/cerca/codice/002000301")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(JsonData.getArtData()))
                .andReturn();
    }

    @Test
    @Order(2)
    public void testInsArticolo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(apiBaseUrl + "/inserisci")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonData.getTestArtData())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("200 OK"))
                .andExpect(jsonPath("$.message").value("Inserimento Articolo 5001234949 Eseguito Con Successo"))

                .andDo(print());
    }

    @Test
    @Order(3)
    public void testDelArticolo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(apiBaseUrl + "/elimina/5001234949")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200 OK"))
                .andExpect(jsonPath("$.message").value("Eliminazione Articolo 5001234949 Eseguita Con Successo"))
                .andDo(print());
    }
}
