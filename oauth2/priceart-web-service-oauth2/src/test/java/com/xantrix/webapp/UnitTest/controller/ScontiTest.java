package com.xantrix.webapp.UnitTest.controller;

import com.xantrix.webapp.Application;
import com.xantrix.webapp.entity.DettListini;
import com.xantrix.webapp.entity.Listini;
import com.xantrix.webapp.repository.ListinoRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("SpringBootApplicationProperties")
@TestPropertySource(properties = {"profilo = list100", "seq = 1", "ramo = list3"})
@ContextConfiguration(classes = Application.class)
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class ScontiTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ListinoRepository listinoRepository;

    String idList = "100";
    String codArt = "002000301";
    Double prezzo = 1.00;

    @BeforeAll
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

        //Inserimento Dati Listino 100
        insertDatiListino(idList, "Listino Test 100", codArt, prezzo);
    }

    @SuppressWarnings("SameParameterValue")
    private void insertDatiListino(String idList, String descrizione, String codArt, double prezzo) {
        Listini listinoTest = new Listini(idList, descrizione, "No");

        Set<DettListini> dettListini = new HashSet<>();
        DettListini dettListTest = new DettListini(codArt, prezzo, listinoTest);
        dettListini.add(dettListTest);

        listinoTest.setDettListini(dettListini);

        listinoRepository.save(listinoTest);
    }

    @Test
    @Order(1)
    public void testGetPrzCodArt() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/prezzi/" + codArt)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value("0.9")) //<-- Prezzo con applicato lo sconto del 10%
                .andReturn();
    }

    @Test
    @Order(2)
    public void testDelPrezzo() throws Exception {
        String url = String.format("/api/prezzi/elimina/%s/%s/", codArt, idList);

        mockMvc.perform(MockMvcRequestBuilders.delete(url)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200 OK"))
                .andExpect(jsonPath("$.message").value("Eliminazione Prezzo Eseguita Con Successo"))
                .andDo(print());
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @AfterAll
    public void ClearData() {
        Optional<Listini> listinoTest = listinoRepository.findById(idList);
        listinoRepository.delete(listinoTest.get());
    }
}