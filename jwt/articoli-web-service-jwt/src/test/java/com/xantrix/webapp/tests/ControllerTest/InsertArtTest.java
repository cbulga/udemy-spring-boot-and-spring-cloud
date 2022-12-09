package com.xantrix.webapp.tests.ControllerTest;

import com.xantrix.webapp.Application;
import com.xantrix.webapp.entity.Articoli;
import com.xantrix.webapp.repository.ArticoliRepository;
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

import static com.xantrix.webapp.controller.ArticoliController.ELIMINAZIONE_ARTICOLO_ESEGUITA_CON_SUCCESSO;
import static com.xantrix.webapp.controller.ArticoliController.INSERIMENTO_ARTICOLO_ESEGUITO_CON_SUCCESSO;
import static com.xantrix.webapp.service.ArticoliServiceImpl.ARTICOLO_DUPLICATO_IMPOSSIBILE_UTILIZZARE_IL_METODO_POST;
import static com.xantrix.webapp.service.ArticoliServiceImpl.ARTICOLO_NON_PRESENTE_IN_ANAGRAFICA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = Application.class)
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
@TestPropertySource(properties = {"profilo = test", "seq = 1", "ramo = main"})
class InsertArtTest {

    @Autowired
    private ArticoliRepository articoliRepository;

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .build();
    }

    private final String ApiBaseUrl = "/api/articoli";

    String JsonData =
            """
                    {\r
                        "codArt": "500123456789",\r
                        "descrizione": "Articoli Unit Test Inserimento",\r
                        "um": "PZ",\r
                        "codStat": "TESTART",\r
                        "pzCart": 6,\r
                        "pesoNetto": 1.75,\r
                        "idStatoArt": "1 ",\r
                        "dataCreaz": "2019-05-14",\r
                        "barcode": [\r
                            {\r
                                "barcode": "12345678",\r
                                "idTipoArt": "CP"\r
                            }\r
                        ],\r
                        "ingredienti": null,\r
                        "iva": {\r
                            "idIva": 22,\r
                            "descrizione": "IVA RIVENDITA 22%",\r
                            "aliquota": 22\r
                        },\r
                        "famAssort": {\r
                            "id": 1,\r
                            "descrizione": "DROGHERIA ALIMENTARE"\r
                        }\r
                    }""";

    @Test
    @Order(1)
    void testInsArticolo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(ApiBaseUrl + "/inserisci")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonData)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.code").value("200 OK"))
                .andExpect(jsonPath("$.message").value(String.format(INSERIMENTO_ARTICOLO_ESEGUITO_CON_SUCCESSO, "500123456789")))

                .andDo(print());

        assertThat(articoliRepository.findByCodArt("500123456789"))
                .extracting(Articoli::getCodArt)
                .isEqualTo("500123456789");
    }

    @Test
    @Order(2)
    void testErrInsArticolo2() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(ApiBaseUrl + "/inserisci")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonData)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.codice").value(406))
                .andExpect(jsonPath("$.messaggio").value(String.format(ARTICOLO_DUPLICATO_IMPOSSIBILE_UTILIZZARE_IL_METODO_POST, "500123456789")))
                .andDo(print());
    }

    //<<< Articolo privo di descrizione
    String ErrJsonData =
            """
                    {\r
                        "codArt": "500123456789",\r
                        "descrizione": "",\r
                        "um": "PZ",\r
                        "codStat": "TESTART",\r
                        "pzCart": 6,\r
                        "pesoNetto": 1.75,\r
                        "idStatoArt": "1 ",\r
                        "dataCreaz": "2019-05-14",\r
                        "barcode": [\r
                            {\r
                                "barcode": "12345678",\r
                                "idTipoArt": "CP"\r
                            }\r
                        ],\r
                        "ingredienti": null,\r
                        "iva": {\r
                            "idIva": 22,\r
                            "descrizione": "IVA RIVENDITA 22%",\r
                            "aliquota": 22\r
                        },\r
                        "famAssort": {\r
                            "id": 1,\r
                            "descrizione": "DROGHERIA ALIMENTARE"\r
                        }\r
                    }""";

    @Test
    @Order(3)
    void testErrInsArticolo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(ApiBaseUrl + "/inserisci")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ErrJsonData)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codice").value(400))
                .andExpect(jsonPath("$.messaggio").value("Il campo Descrizione deve avere un numero di caratteri compreso tra 6 e 80"))
                .andDo(print());
    }

    //<<< Modifica Stato Articolo a 2
    String JsonDataMod =
            """
                    {\r
                        "codArt": "500123456789",\r
                        "descrizione": "Articoli Unit Test Inserimento",\r
                        "um": "PZ",\r
                        "codStat": "TESTART",\r
                        "pzCart": 6,\r
                        "pesoNetto": 1.75,\r
                        "idStatoArt": "2 ",\r
                        "dataCreaz": "2019-05-14",\r
                        "barcode": [\r
                            {\r
                                "barcode": "12345678",\r
                                "idTipoArt": "CP"\r
                            }\r
                        ],\r
                        "ingredienti": null,\r
                        "iva": {\r
                            "idIva": 22,\r
                            "descrizione": "IVA RIVENDITA 22%",\r
                            "aliquota": 22\r
                        },\r
                        "famAssort": {\r
                            "id": 1,\r
                            "descrizione": "DROGHERIA ALIMENTARE"\r
                        }\r
                    }""";

    @Test
    @Order(4)
    void testUpdArticolo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(ApiBaseUrl + "/modifica")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonDataMod)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("200 OK"))
                .andExpect(jsonPath("$.message").value("Modifica Articolo 500123456789 Eseguita Con Successo"))
                .andDo(print());

        assertThat(articoliRepository.findByCodArt("500123456789"))
                .extracting(Articoli::getIdStatoArt)
                .isEqualTo("2");
    }

    //<<< Modifica Stato Articolo a 2
    String ErrJsonDataMod =
            """
                    {\r
                        "codArt": "500999888777",\r
                        "descrizione": "Articoli Unit Test Inserimento",\r
                        "um": "PZ",\r
                        "codStat": "TESTART",\r
                        "pzCart": 6,\r
                        "pesoNetto": 1.75,\r
                        "idStatoArt": "2 ",\r
                        "dataCreaz": "2019-05-14",\r
                        "barcode": [\r
                            {\r
                                "barcode": "12345678",\r
                                "idTipoArt": "CP"\r
                            }\r
                        ],\r
                        "ingredienti": null,\r
                        "iva": {\r
                            "idIva": 22,\r
                            "descrizione": "IVA RIVENDITA 22%",\r
                            "aliquota": 22\r
                        },\r
                        "famAssort": {\r
                            "id": 1,\r
                            "descrizione": "DROGHERIA ALIMENTARE"\r
                        }\r
                    }""";

    @Test
    @Order(5)
    void testErrUpdArticolo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(ApiBaseUrl + "/modifica")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ErrJsonDataMod)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codice").value(404))
                .andExpect(jsonPath("$.messaggio").value("Articolo 500999888777 non presente in anagrafica! Impossibile utilizzare il metodo PUT"))
                .andDo(print());
    }

    @Test
    @Order(6)
    void testDelArticolo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(ApiBaseUrl + "/elimina/500123456789")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200 OK"))
                .andExpect(jsonPath("$.message").value(String.format(ELIMINAZIONE_ARTICOLO_ESEGUITA_CON_SUCCESSO, "500123456789")))
                .andDo(print());
    }

    @Test
    @Order(7)
    void testErrDelArticolo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(ApiBaseUrl + "/elimina/500123456789")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ErrJsonDataMod)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codice").value(404))
                .andExpect(jsonPath("$.messaggio").value(String.format(ARTICOLO_NON_PRESENTE_IN_ANAGRAFICA, "500123456789")))
                .andDo(print());
    }
}
