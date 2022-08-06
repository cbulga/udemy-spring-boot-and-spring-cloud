package com.xantrix.webapp.UnitTest.controller;

import com.xantrix.webapp.Application;
import com.xantrix.webapp.repository.ListinoRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test: config server needs to be started
 */
@ContextConfiguration(classes = Application.class)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = {"profilo = list100", "seq = 1"})
public class ListiniControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ListinoRepository listinoRepository;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    String JsonData = "{\n" +
            "    \"id\": \"100\",\n" +
            "    \"descrizione\": \"Listino Test 100\",\n" +
            "    \"obsoleto\": \"No\",\n" +
            "    \"dettListini\": [\n" +
            "        {\n" +
            "            \"id\": -1,\n" +
            "            \"codArt\": \"002000301\",\n" +
            "            \"prezzo\": 1.00\n" +
            "        }]\n" +
            "}";

    @Test
    @Order(1)
    public void testInsListino() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/listino/inserisci")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonData)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("200 OK"))
                .andExpect(jsonPath("$.message").value("Inserimento Listino 100 Eseguito Con Successo"))
                .andDo(print());

        assertThat(listinoRepository.findById("100"))
                .isNotEmpty();
    }

    @Test
    @Order(2)
    public void testGetListById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/listino/cerca/id/100")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value("100"))
                .andExpect(jsonPath("$.descrizione").value("Listino Test 100"))
                .andExpect(jsonPath("$.obsoleto").value("No"))

                .andExpect(jsonPath("$.dettListini[0].id").exists())
                .andExpect(jsonPath("$.dettListini[0].codArt").value("002000301"))
                .andExpect(jsonPath("$.dettListini[0].prezzo").value("1.0"))

                .andReturn();
    }

    @Test
    @Order(3)
    public void testDelListino() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/listino/elimina/100")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200 OK"))
                .andExpect(jsonPath("$.message").value("Eliminazione Listino 100 Eseguita Con Successo"))
                .andDo(print());
    }

    @Test
    @Order(4)
    public void testErrDelListino() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/listino/elimina/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codice").value("404"))
                .andExpect(jsonPath("$.messaggio").value("Listino 999 non presente in anagrafica!"))
                .andDo(print());
    }
}
