package com.xantrix.webapp.promowebservicejwt.ControllerTests;

import com.xantrix.webapp.Application;
import com.xantrix.webapp.entity.Promo;
import com.xantrix.webapp.repository.PromoRepository;
import lombok.extern.java.Log;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("SpringBootApplicationProperties")
@ContextConfiguration(classes = Application.class)
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("Test Classe Controller Promo")
@TestPropertySource(properties = {"profilo = test", "seq = 1", "ramo = main"})
@Log
class PromoControllerTest {

    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private PromoRepository promoRepository;
    private static boolean isInitialized = false;
    int anno = Year.now().getValue();
    String codice = "TEST01";
    String inizio = anno + "-01-01";
    String fine = anno + "-12-31";
    String inizioScad = anno - 1 + "-01-01";
    String fineScad = anno - 1 + "-12-31";

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

        if (isInitialized) return;

        //Eliminiamo tutte le promozioni eventualmente esistenti
        promoRepository.deleteAll();

        log.info(String.format("****** Numero Promozioni Esistenti: %s *******", promoRepository.findAll().size()));

        isInitialized = true;
    }

    String jsonData =
            "{\r\n"
                    + "	\"idPromo\": \"\",\r\n"
                    + "	\"anno\": " + anno + ",\r\n"
                    + "	\"codice\": \"" + codice + "\",\r\n"
                    + "	\"descrizione\": \"PROMO TEST01\",\r\n"
                    + "	\"dettPromos\": [\r\n"
                    + "		{\r\n"
                    + "			\"id\": -1,\r\n"
                    + "			\"riga\": 1,\r\n"
                    + "			\"codArt\": \"049477701\",\r\n"
                    + "			\"codFid\": \"\",\r\n"
                    + "			\"inizio\": \"" + inizio + "\",\r\n"
                    + "			\"fine\": \"" + fine + "\",\r\n"
                    + "			\"oggetto\": \"1.10\",\r\n"
                    + "			\"isFid\": \"No\",\r\n"
                    + "			\"tipoPromo\": {\r\n"
                    + "				\"idTipoPromo\": \"1\"\r\n"
                    + "			}\r\n"
                    + "		},\r\n"
                    + "		{\r\n"
                    + "			\"id\": -1,\r\n"
                    + "			\"riga\": 2,\r\n"
                    + "			\"codArt\": \"004590201\",\r\n"
                    + "			\"codFid\": \"\",\r\n"
                    + "			\"inizio\": \"" + inizio + "\",\r\n"
                    + "			\"fine\": \"" + fine + "\",\r\n"
                    + "			\"oggetto\": \"1.99\",\r\n"
                    + "			\"isFid\": \"Si\",\r\n"
                    + "			\"tipoPromo\": {\r\n"
                    + "				\"idTipoPromo\": \"1\"\r\n"
                    + "			}\r\n"
                    + "		},\r\n"
                    + "		{\r\n"
                    + "			\"id\": -1,\r\n"
                    + "			\"riga\": 3,\r\n"
                    + "			\"codArt\": \"008071001\",\r\n"
                    + "			\"codFid\": \"67000076\",\r\n"
                    + "			\"inizio\": \"" + inizio + "\",\r\n"
                    + "			\"fine\": \"" + fine + "\",\r\n"
                    + "			\"oggetto\": \"2.19\",\r\n"
                    + "			\"isFid\": \"Si\",\r\n"
                    + "			\"tipoPromo\": {\r\n"
                    + "				\"idTipoPromo\": \"1\"\r\n"
                    + "			}\r\n"
                    + "		},\r\n"
                    + "		{\r\n"
                    + "			\"id\": -1,\r\n"
                    + "			\"riga\": 4,\r\n"
                    + "			\"codArt\": \"002001601\",\r\n"
                    + "			\"codFid\": \"\",\r\n"
                    + "			\"inizio\": \"" + inizioScad + "\",\r\n"
                    + "			\"fine\": \"" + fineScad + "\",\r\n"
                    + "			\"oggetto\": \"0.99\",\r\n"
                    + "			\"isFid\": \"No\",\r\n"
                    + "			\"tipoPromo\": {\r\n"
                    + "				\"idTipoPromo\": \"1\"\r\n"
                    + "			}\r\n"
                    + "		}\r\n"
                    + "	],\r\n"
                    + "	\"depRifPromos\": [\r\n"
                    + "		{\r\n"
                    + "			\"id\": -1,\r\n"
                    + "			\"idDeposito\": 526\r\n"
                    + "		},\r\n"
                    + "		{\r\n"
                    + "			\"id\": -1,\r\n"
                    + "			\"idDeposito\": 525\r\n"
                    + "		}\r\n"
                    + "	]\r\n"
                    + "}";

    @Test
    @Order(1)
    @DisplayName("TEST CREAZIONE PROMO TEST01")
    void createPromo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/promo/inserisci")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonData)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        assertThat(promoRepository.findAll()).hasSize(1);

        log.info(String.format("****** Numero Promozioni Esistenti: %s *******", promoRepository.findAll().size()));
    }

    @Test
    @Order(2)
    @DisplayName("TEST SELEZIONE DI TUTTE LE PROMO")
    void listAllPromo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/promo/")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(promoRepository.findAll().size())))

                //Verifichiamo la terza riga della promozione
                .andExpect(jsonPath("$[0].dettPromos[2].id").exists())
                .andExpect(jsonPath("$[0].dettPromos[2].riga").value("3"))
                .andExpect(jsonPath("$[0].dettPromos[2].codArt").value("008071001"))
                .andExpect(jsonPath("$[0].dettPromos[2].codFid").value("67000076"))
                .andExpect(jsonPath("$[0].dettPromos[2].isFid").value("Si"))
                .andExpect(jsonPath("$[0].dettPromos[2].inizio").value(inizio))
                .andExpect(jsonPath("$[0].dettPromos[2].fine").value(fine))
                .andExpect(jsonPath("$[0].dettPromos[2].idTipoPromo").value("1"))
                .andExpect(jsonPath("$[0].dettPromos[2].oggetto").value("2.19"));
    }

    @Test
    @Order(3)
    @DisplayName("TEST SELEZIONE PROMO PER ID")
    void listPromoById() throws Exception {
        Promo promo = promoRepository.findByAnnoAndCodice(anno, codice);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/promo/id/" + promo.getIdPromo())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.idPromo").exists())
                .andExpect(jsonPath("$.idPromo").value(promo.getIdPromo()))

                //Verifichiamo la prima riga della promozione
                .andExpect(jsonPath("$.dettPromos[0].id").exists())
                .andExpect(jsonPath("$.dettPromos[0].riga").value("1"))
                .andExpect(jsonPath("$.dettPromos[0].codArt").value("049477701"))
                .andExpect(jsonPath("$.dettPromos[0].codFid").value(""))
                .andExpect(jsonPath("$.dettPromos[0].isFid").value("No"))
                .andExpect(jsonPath("$.dettPromos[0].inizio").value(inizio))
                .andExpect(jsonPath("$.dettPromos[0].fine").value(fine))
                .andExpect(jsonPath("$.dettPromos[0].idTipoPromo").value("1"))
                .andExpect(jsonPath("$.dettPromos[0].oggetto").value("1.10"))

                //Verifichiamo la quarta riga della promozione
                .andExpect(jsonPath("$.dettPromos[3].id").exists())
                .andExpect(jsonPath("$.dettPromos[3].riga").value("4"))
                .andExpect(jsonPath("$.dettPromos[3].codArt").value("002001601"))
                .andExpect(jsonPath("$.dettPromos[3].codFid").value(""))
                .andExpect(jsonPath("$.dettPromos[3].isFid").value("No"))
                .andExpect(jsonPath("$.dettPromos[3].inizio").value(inizioScad))
                .andExpect(jsonPath("$.dettPromos[3].fine").value(fineScad))
                .andExpect(jsonPath("$.dettPromos[3].idTipoPromo").value("1"))
                .andExpect(jsonPath("$.dettPromos[3].oggetto").value("0.99"))

                .andReturn();
    }

    private String getNewData() {
        String idPromo = promoRepository.findByAnnoAndCodice(anno, codice).getIdPromo();

        return "{\r\n"
                + "	\"idPromo\": \"" + idPromo + "\",\r\n"
                + "	\"anno\": " + anno + ",\r\n"
                + "	\"codice\": \"" + codice + "\",\r\n"
                + "	\"descrizione\": \"PROMO TEST01 MODIFICA\",\r\n" //Modificata intestazione
                + "	\"dettPromos\": [\r\n"
                + "		{\r\n"
                + "			\"id\": -1,\r\n"
                + "			\"riga\": 1,\r\n"
                + "			\"codArt\": \"049477701\",\r\n"
                + "			\"codFid\": \"\",\r\n"
                + "			\"inizio\": \"" + inizio + "\",\r\n"
                + "			\"fine\": \"" + fine + "\",\r\n"
                + "			\"oggetto\": \"1.19\",\r\n" //Modificato prezzo promo
                + "			\"isFid\": \"No\",\r\n"
                + "			\"tipoPromo\": {\r\n"
                + "				\"idTipoPromo\": \"1\"\r\n"
                + "			}\r\n"
                + "		},\r\n"
                + "		{\r\n"
                + "			\"id\": -1,\r\n"
                + "			\"riga\": 2,\r\n"
                + "			\"codArt\": \"004590201\",\r\n"
                + "			\"codFid\": \"\",\r\n"
                + "			\"inizio\": \"" + inizio + "\",\r\n"
                + "			\"fine\": \"" + fine + "\",\r\n"
                + "			\"oggetto\": \"1.99\",\r\n"
                + "			\"isFid\": \"No\",\r\n" //Modificato stato isfid
                + "			\"tipoPromo\": {\r\n"
                + "				\"idTipoPromo\": \"1\"\r\n"
                + "			}\r\n"
                + "		},\r\n"
                + "		{\r\n"
                + "			\"id\": -1,\r\n"
                + "			\"riga\": 3,\r\n"
                + "			\"codArt\": \"008071001\",\r\n"
                + "			\"codFid\": \"67000076\",\r\n"
                + "			\"inizio\": \"" + inizio + "\",\r\n"
                + "			\"fine\": \"" + fine + "\",\r\n"
                + "			\"oggetto\": \"2.19\",\r\n"
                + "			\"isFid\": \"Si\",\r\n"
                + "			\"tipoPromo\": {\r\n"
                + "				\"idTipoPromo\": \"1\"\r\n"
                + "			}\r\n"
                + "		},\r\n"
                + "		{\r\n"
                + "			\"id\": -1,\r\n"
                + "			\"riga\": 4,\r\n"
                + "			\"codArt\": \"002001601\",\r\n"
                + "			\"codFid\": \"\",\r\n"
                + "			\"inizio\": \"" + inizioScad + "\",\r\n"
                + "			\"fine\": \"" + fineScad + "\",\r\n"
                + "			\"oggetto\": \"0.99\",\r\n"
                + "			\"isFid\": \"No\",\r\n"
                + "			\"tipoPromo\": {\r\n"
                + "				\"idTipoPromo\": \"1\"\r\n"
                + "			}\r\n"
                + "		}\r\n"
                + "	],\r\n"
                + "	\"depRifPromos\": [\r\n"
                + "		{\r\n"
                + "			\"id\": -1,\r\n"
                + "			\"idDeposito\": 526\r\n"
                + "		},\r\n"
                + "		{\r\n"
                + "			\"id\": -1,\r\n"
                + "			\"idDeposito\": 525\r\n"
                + "		}\r\n"
                + "	]\r\n"
                + "}";
    }

    @Test
    @Order(4)
    @DisplayName("TEST MODIFICA PROMO TEST01")
    void updatePromo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/promo/aggiorna")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getNewData())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        assertThat(promoRepository.findByAnnoAndCodice(anno, codice))
                .extracting(Promo::getDescrizione)
                .isEqualTo("PROMO TEST01 MODIFICA");
    }

    @Test
    @Order(5)
    @DisplayName("TEST SELEZIONE PROMO PER ANNO E CODICE")
    void listPromoByCodice() throws Exception {
        String url = String.format("/api/promo/codice?anno=%s&codice=%s", anno, codice);
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.idPromo").value(promoRepository.findByAnnoAndCodice(anno, codice).getIdPromo()))

                .andExpect(jsonPath("$.dettPromos[0].riga").value("1"))
                .andExpect(jsonPath("$.dettPromos[0].codArt").value("049477701"))
                .andExpect(jsonPath("$.dettPromos[0].oggetto").value("1.19"))
                .andExpect(jsonPath("$.dettPromos[0].isFid").value("No"))

                .andExpect(jsonPath("$.dettPromos[1].riga").value("2"))
                .andExpect(jsonPath("$.dettPromos[1].codArt").value("004590201"))
                .andExpect(jsonPath("$.dettPromos[1].oggetto").value("1.99"))
                .andExpect(jsonPath("$.dettPromos[1].isFid").value("No"))

                .andDo(print());
    }

    String jsonData2 =
            "{\r\n"
                    + "	\"idPromo\": \"\",\r\n"
                    + "	\"anno\": " + anno + ",\r\n"
                    + "	\"codice\": \"TEST02\",\r\n"
                    + "	\"descrizione\": \"PROMO TEST02 SCADUTA\",\r\n"
                    + "	\"dettPromos\": [\r\n"
                    + "		{\r\n"
                    + "			\"id\": -1,\r\n"
                    + "			\"riga\": 1,\r\n"
                    + "			\"codArt\": \"049477701\",\r\n"
                    + "			\"codFid\": \"\",\r\n"
                    + "			\"inizio\": \"" + inizioScad + "\",\r\n"
                    + "			\"fine\": \"" + fineScad + "\",\r\n"
                    + "			\"oggetto\": \"1.19\",\r\n"
                    + "			\"isFid\": \"No\",\r\n"
                    + "			\"tipoPromo\": {\r\n"
                    + "				\"idTipoPromo\": \"1\"\r\n"
                    + "			}\r\n"
                    + "		},\r\n"
                    + "		{\r\n"
                    + "			\"id\": -1,\r\n"
                    + "			\"riga\": 2,\r\n"
                    + "			\"codArt\": \"004590201\",\r\n"
                    + "			\"codFid\": \"\",\r\n"
                    + "			\"inizio\": \"" + inizioScad + "\",\r\n"
                    + "			\"fine\": \"" + fineScad + "\",\r\n"
                    + "			\"oggetto\": \"1.99\",\r\n"
                    + "			\"isFid\": \"No\",\r\n"
                    + "			\"tipoPromo\": {\r\n"
                    + "				\"idTipoPromo\": \"1\"\r\n"
                    + "			}\r\n"
                    + "		}\r\n"
                    + "	]\r\n"
                    + "}";

    @Test
    @Order(6)
    @DisplayName("TEST CREAZIONE PROMO TEST02 SCADUTA")
    void createPromo2() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/promo/inserisci")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonData2)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @Order(7)
    @DisplayName("TEST SELEZIONE PROMO ATTIVE")
    void listPromoActive() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/promo/active")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @Order(8)
    @DisplayName("TEST ELIMINAZIONE PROMO")
    void deletePromo() throws Exception {
        Promo promo = promoRepository.findByAnnoAndCodice(anno, codice);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/promo/elimina/" + promo.getIdPromo())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @Order(9)
    @DisplayName("TEST ERRORE RICERCA PROMO")
    void errListPromoById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/promo/id/ABC")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonData)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("The promo ABC was not found"));
    }

    @Test
    @Order(10)
    @DisplayName("TEST ELIMINAZIONE GENERALE PROMO")
    void delAllPromo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/promo/elimina/all")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        assertThat(promoRepository.findAll()).isEmpty();
    }
}
