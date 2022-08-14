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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = Application.class)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestSecurityRoleUser {

    private static final String apiBaseUrl = "/api/articoli";
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .defaultRequest(get("/")
                        .with(user("cristian").roles("USER"))) //Attivato Ruolo User
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
    void testErrRoleInsArticolo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(apiBaseUrl + "/inserisci")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden()) //Non sei autorizzato ad usare l'endpoint
                .andDo(print());
    }
}
