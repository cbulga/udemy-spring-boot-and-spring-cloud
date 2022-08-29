package com.xantrix.webapp.UnitTest.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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

import com.xantrix.webapp.Application;
import com.xantrix.webapp.entity.DettListini;
import com.xantrix.webapp.entity.Listini;
import com.xantrix.webapp.repository.ListinoRepository;


//@TestPropertySource(locations="classpath:application-list100.properties")
@TestPropertySource(properties = {"profilo = std2", "seq = 1"})
@ContextConfiguration(classes = Application.class)
@SpringBootTest()
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
class OtherPrezziControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ListinoRepository listinoRepository;

    String idList = "100";
    String idList2 = "101";
    String codArt = "002000301";
    Double prezzo = 1.00;
    Double prezzo2 = 2.00;

    @BeforeAll
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

        //Inserimento Dati Listino 100
        insertDatiListino(idList, "Listino Test 100", codArt, prezzo);

        //Inserimento Dati Listino 101
        insertDatiListino(idList2, "Listino Test 101", codArt, prezzo2);
    }

    private void insertDatiListino(String idList, String descrizione, String codArt, Double prezzo) {
        Listini listinoTest = new Listini(idList, descrizione, "No", null);

        Set<DettListini> dettListini = new HashSet<>();
        DettListini dettListTest = new DettListini(codArt, prezzo, listinoTest);
        dettListini.add(dettListTest);

        listinoTest.setDettListini(dettListini);

        listinoRepository.save(listinoTest);
    }

    @Test
    @Order(1)
    void testGetPrzCodArt() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/prezzi/" + codArt)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value("0.96")) //<-- Prezzo con applicato lo sconto del 10%
                .andReturn();
    }

    @Test
    @Order(2)
    void testGetPrzCodArt2() throws Exception {
        String Url = String.format("/api/prezzi/%s/%s", codArt, idList2);

        mockMvc.perform(MockMvcRequestBuilders.get(Url)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value("1.8")) //<-- Prezzo con applicato lo sconto del 10%
                .andReturn();
    }

    @Test
    @Order(3)
    void testDelPrezzo() throws Exception {
        String url = String.format("/api/prezzi/elimina/%s/%s/", codArt, idList);

        mockMvc.perform(MockMvcRequestBuilders.delete(url)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200 OK"))
                .andExpect(jsonPath("$.message").value("Eliminazione Prezzo Eseguita Con Successo"))
                .andDo(print());
    }

    @AfterAll
    public void ClearData() {
        Optional<Listini> listinoTest = listinoRepository.findById(idList);
        listinoRepository.delete(listinoTest.get());

        listinoTest = listinoRepository.findById(idList2);
        listinoRepository.delete(listinoTest.get());
    }
}
