package com.xantrix.webapp.tests.ControllerTest;

import com.xantrix.webapp.Application;
import com.xantrix.webapp.entities.Articoli;
import com.xantrix.webapp.repository.ArticoliRepository;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static com.xantrix.webapp.controller.ArticoliController.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = Application.class)
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class InsertArtTest {

    @Autowired
    private ArticoliRepository articoliRepository;

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @BeforeEach
    public void setup() throws JSONException, IOException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .build();
    }

    private String ApiBaseUrl = "/api/articoli";

    String JsonData =
            "{\r\n" +
                    "    \"codArt\": \"123Test\",\r\n" +
                    "    \"descrizione\": \"Articoli Unit Test Inserimento\",\r\n" +
                    "    \"um\": \"PZ\",\r\n" +
                    "    \"codStat\": \"TESTART\",\r\n" +
                    "    \"pzCart\": 6,\r\n" +
                    "    \"pesoNetto\": 1.75,\r\n" +
                    "    \"idStatoArt\": \"1 \",\r\n" +
                    "    \"dataCreaz\": \"2019-05-14\",\r\n" +
                    "    \"barcode\": [\r\n" +
                    "        {\r\n" +
                    "            \"barcode\": \"12345678\",\r\n" +
                    "            \"idTipoArt\": \"CP\"\r\n" +
                    "        }\r\n" +
                    "    ],\r\n" +
                    "    \"ingredienti\": null,\r\n" +
                    "    \"iva\": {\r\n" +
                    "        \"idIva\": 22,\r\n" +
                    "        \"descrizione\": \"IVA RIVENDITA 22%\",\r\n" +
                    "        \"aliquota\": 22\r\n" +
                    "    },\r\n" +
                    "    \"famAssort\": {\r\n" +
                    "        \"id\": 1,\r\n" +
                    "        \"descrizione\": \"DROGHERIA ALIMENTARE\"\r\n" +
                    "    }\r\n" +
                    "}";

    @Test
    @Order(1)
    public void testInsArticolo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(ApiBaseUrl + "/inserisci")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonData)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.code").value("200 OK"))
                .andExpect(jsonPath("$.message").value(String.format(INSERIMENTO_ARTICOLO_ESEGUITO_CON_SUCCESSO, "123Test")))

                .andDo(print());

        assertThat(articoliRepository.findByCodArt("123Test"))
                .extracting(Articoli::getCodArt)
                .isEqualTo("123Test");
    }

    @Test
    @Order(2)
    public void testErrInsArticolo2() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(ApiBaseUrl + "/inserisci")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonData)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.codice").value(406))
                .andExpect(jsonPath("$.messaggio").value(String.format(ARTICOLO_DUPLICATO_IMPOSSIBILE_UTILIZZARE_IL_METODO_POST, "123Test")))
                .andDo(print());
    }

    String ErrJsonData =
            "{\r\n" +
                    "    \"codArt\": \"123Test\",\r\n" +
                    "    \"descrizione\": \"\",\r\n" +  //<<< Articolo privo di descrizione
                    "    \"um\": \"PZ\",\r\n" +
                    "    \"codStat\": \"TESTART\",\r\n" +
                    "    \"pzCart\": 6,\r\n" +
                    "    \"pesoNetto\": 1.75,\r\n" +
                    "    \"idStatoArt\": \"1 \",\r\n" +
                    "    \"dataCreaz\": \"2019-05-14\",\r\n" +
                    "    \"barcode\": [\r\n" +
                    "        {\r\n" +
                    "            \"barcode\": \"12345678\",\r\n" +
                    "            \"idTipoArt\": \"CP\"\r\n" +
                    "        }\r\n" +
                    "    ],\r\n" +
                    "    \"ingredienti\": null,\r\n" +
                    "    \"iva\": {\r\n" +
                    "        \"idIva\": 22,\r\n" +
                    "        \"descrizione\": \"IVA RIVENDITA 22%\",\r\n" +
                    "        \"aliquota\": 22\r\n" +
                    "    },\r\n" +
                    "    \"famAssort\": {\r\n" +
                    "        \"id\": 1,\r\n" +
                    "        \"descrizione\": \"DROGHERIA ALIMENTARE\"\r\n" +
                    "    }\r\n" +
                    "}";

    @Test
    @Order(3)
    public void testErrInsArticolo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(ApiBaseUrl + "/inserisci")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ErrJsonData)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codice").value(400))
                .andExpect(jsonPath("$.messaggio").value("Il campo Descrizione deve avere un numero di caratteri compreso tra 6 e 80"))
                .andDo(print());
    }

    String JsonDataMod =
            "{\r\n" +
                    "    \"codArt\": \"123Test\",\r\n" +
                    "    \"descrizione\": \"Articoli Unit Test Inserimento\",\r\n" +
                    "    \"um\": \"PZ\",\r\n" +
                    "    \"codStat\": \"TESTART\",\r\n" +
                    "    \"pzCart\": 6,\r\n" +
                    "    \"pesoNetto\": 1.75,\r\n" +
                    "    \"idStatoArt\": \"2 \",\r\n" + //<<< Modifica Stato Articolo a 2
                    "    \"dataCreaz\": \"2019-05-14\",\r\n" +
                    "    \"barcode\": [\r\n" +
                    "        {\r\n" +
                    "            \"barcode\": \"12345678\",\r\n" +
                    "            \"idTipoArt\": \"CP\"\r\n" +
                    "        }\r\n" +
                    "    ],\r\n" +
                    "    \"ingredienti\": null,\r\n" +
                    "    \"iva\": {\r\n" +
                    "        \"idIva\": 22,\r\n" +
                    "        \"descrizione\": \"IVA RIVENDITA 22%\",\r\n" +
                    "        \"aliquota\": 22\r\n" +
                    "    },\r\n" +
                    "    \"famAssort\": {\r\n" +
                    "        \"id\": 1,\r\n" +
                    "        \"descrizione\": \"DROGHERIA ALIMENTARE\"\r\n" +
                    "    }\r\n" +
                    "}";

    @Test
    @Order(4)
    public void testUpdArticolo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(ApiBaseUrl + "/modifica")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonDataMod)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("200 OK"))
                .andExpect(jsonPath("$.message").value("Modifica Articolo 123Test Eseguita Con Successo"))
                .andDo(print());

        assertThat(articoliRepository.findByCodArt("123Test"))
                .extracting(Articoli::getIdStatoArt)
                .isEqualTo("2");
    }

    String ErrJsonDataMod =
            "{\r\n" +
                    "    \"codArt\": \"pippo123\",\r\n" +
                    "    \"descrizione\": \"Articoli Unit Test Inserimento\",\r\n" +
                    "    \"um\": \"PZ\",\r\n" +
                    "    \"codStat\": \"TESTART\",\r\n" +
                    "    \"pzCart\": 6,\r\n" +
                    "    \"pesoNetto\": 1.75,\r\n" +
                    "    \"idStatoArt\": \"2 \",\r\n" + //<<< Modifica Stato Articolo a 2
                    "    \"dataCreaz\": \"2019-05-14\",\r\n" +
                    "    \"barcode\": [\r\n" +
                    "        {\r\n" +
                    "            \"barcode\": \"12345678\",\r\n" +
                    "            \"idTipoArt\": \"CP\"\r\n" +
                    "        }\r\n" +
                    "    ],\r\n" +
                    "    \"ingredienti\": null,\r\n" +
                    "    \"iva\": {\r\n" +
                    "        \"idIva\": 22,\r\n" +
                    "        \"descrizione\": \"IVA RIVENDITA 22%\",\r\n" +
                    "        \"aliquota\": 22\r\n" +
                    "    },\r\n" +
                    "    \"famAssort\": {\r\n" +
                    "        \"id\": 1,\r\n" +
                    "        \"descrizione\": \"DROGHERIA ALIMENTARE\"\r\n" +
                    "    }\r\n" +
                    "}";

    @Test
    @Order(5)
    public void testErrUpdArticolo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(ApiBaseUrl + "/modifica")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ErrJsonDataMod)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codice").value(404))
                .andExpect(jsonPath("$.messaggio").value("Articolo pippo123 non presente in anagrafica! Impossibile utilizzare il metodo PUT"))
                .andDo(print());
    }

    @Test
    @Order(6)
    public void testDelArticolo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(ApiBaseUrl + "/elimina/123Test")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200 OK"))
                .andExpect(jsonPath("$.message").value(String.format(ELIMINAZIONE_ARTICOLO_ESEGUITA_CON_SUCCESSO, "123Test")))
                .andDo(print());
    }

    @Test
    @Order(7)
    public void testErrDelArticolo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(ApiBaseUrl + "/elimina/123Test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ErrJsonDataMod)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codice").value(404))
                .andExpect(jsonPath("$.messaggio").value(String.format(ARTICOLO_NON_PRESENTE_IN_ANAGRAFICA, "123Test")))
                .andDo(print());
    }
}
