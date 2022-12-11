package com.xantrix.webapp;

import com.xantrix.webapp.entity.Ordini;
import com.xantrix.webapp.repository.OrdiniRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest()
@TestPropertySource(properties = {"profilo = test", "seq = 1", "ramo = main"})
@ContextConfiguration(classes = Application.class)
@TestMethodOrder(OrderAnnotation.class)
class OrdiniWebServiceTests {

    private MockMvc mockMvc;
    private static String idOrdine = "";

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private OrdiniRepository ordiniRepository;

    private final String apiBaseUrl = "/api/ordini";

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .build();
    }

    private String getJsonData() {
        UUID uuid = UUID.randomUUID();
        idOrdine = (idOrdine.length() == 0) ? uuid.toString() : idOrdine;

        return "{\n" +
                "    \"id\": \"" + idOrdine + "\",\n" +
                "    \"idpdv\": 525,\n" +
                "    \"codfid\": \"65000000\",\n" +
                "    \"stato\": 1,\n" +
                "    \"dettOrdine\": [\n" +
                "        {\n" +
                "            \"id\": -1,\n" +
                "            \"codArt\": \"000020026\",\n" +
                "            \"qta\": 24.0,\n" +
                "            \"prezzo\": 0.29\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": -1,\n" +
                "            \"codArt\": \"000022601\",\n" +
                "            \"qta\": 2.0,\n" +
                "            \"prezzo\": 0.49\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": -1,\n" +
                "            \"codArt\": \"000035110\",\n" +
                "            \"qta\": 1.0,\n" +
                "            \"prezzo\": 6.49\n" +
                "        }\n" +
                "    ],\n" +
                "    \"data\": \"2019-11-25\"\n" +
                "}";
    }

    @Test
    @Order(1)
    void testInsOrder() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(apiBaseUrl + "/inserisci")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getJsonData())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("200 OK"));

        assertThat(ordiniRepository.findById(idOrdine).get())
                .extracting(Ordini::getCodfid)
                .isEqualTo("65000000");
    }

    @Test
    @Order(2)
    void listOrdByCode() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(apiBaseUrl + "/cerca/codice/" + idOrdine)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                //ordine
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(idOrdine))
                .andExpect(jsonPath("$.idpdv").exists())
                .andExpect(jsonPath("$.idpdv").value("525"))
                .andExpect(jsonPath("$.codfid").exists())
                .andExpect(jsonPath("$.codfid").value("65000000"))
                .andExpect(jsonPath("$.stato").exists())
                .andExpect(jsonPath("$.stato").value(1))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").value("2019-11-25"))
                //dettaglio ordine
                .andExpect(jsonPath("$.dettOrdine[0].id").exists())
                .andExpect(jsonPath("$.dettOrdine[0].codArt").exists())
                .andExpect(jsonPath("$.dettOrdine[0].codArt").value("000035110"))
                .andExpect(jsonPath("$.dettOrdine[0].qta").exists())
                .andExpect(jsonPath("$.dettOrdine[0].qta").value(1.0))
                .andExpect(jsonPath("$.dettOrdine[0].prezzo").exists())
                .andExpect(jsonPath("$.dettOrdine[0].prezzo").value(6.49))

                .andExpect(jsonPath("$.dettOrdine[1].id").exists())
                .andExpect(jsonPath("$.dettOrdine[1].codArt").exists())
                .andExpect(jsonPath("$.dettOrdine[1].codArt").value("000022601"))
                .andExpect(jsonPath("$.dettOrdine[1].qta").exists())
                .andExpect(jsonPath("$.dettOrdine[1].qta").value(2.0))
                .andExpect(jsonPath("$.dettOrdine[1].prezzo").exists())
                .andExpect(jsonPath("$.dettOrdine[1].prezzo").value(0.49))

                .andExpect(jsonPath("$.dettOrdine[2].id").exists())
                .andExpect(jsonPath("$.dettOrdine[2].codArt").exists())
                .andExpect(jsonPath("$.dettOrdine[2].codArt").value("000020026"))
                .andExpect(jsonPath("$.dettOrdine[2].qta").exists())
                .andExpect(jsonPath("$.dettOrdine[2].qta").value(24.0))
                .andExpect(jsonPath("$.dettOrdine[2].prezzo").exists())
                .andExpect(jsonPath("$.dettOrdine[2].prezzo").value(0.29));
    }

    @Test
    @Order(3)
    void testDelOrd() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(apiBaseUrl + "/elimina/" + idOrdine)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200 OK"))
                .andExpect(jsonPath("$.message").value(String.format("Eliminazione Ordine %s Eseguita Con Successo", idOrdine)))
                .andDo(print());
    }
}
