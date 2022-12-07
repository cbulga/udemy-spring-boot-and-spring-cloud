package com.xantrix.webapp.promowebservicejwt.ControllerTests;

import com.xantrix.webapp.Application;
import com.xantrix.webapp.repository.PromoRepository;
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

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SuppressWarnings("SpringBootApplicationProperties")
@ContextConfiguration(classes = Application.class)
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("Test Validazione Inserimento Promo")
@TestPropertySource(properties = {"profilo = test", "seq = 1", "ramo = main"})
class PromoValidationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private PromoRepository promoRepository;

    private static boolean isInitialized = false;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

        if (isInitialized) return;

        //Eliminiamo tutte le promozioni eventualmente esistenti
        promoRepository.deleteAll();

        isInitialized = true;
    }

    //codice promo mancante
    String jsonData =
            """
                    {\r
                    	"idPromo": "",\r
                    	"anno": 2022,\r
                    	"codice": "",\r
                    	"descrizione": "PROMO TEST01",\r
                    	"dettPromos": [\r
                    		{\r
                    			"id": -1,\r
                    			"riga": 1,\r
                    			"codArt": "049477701",\r
                    			"codFid": "",\r
                    			"inizio": "2022-01-01",\r
                    			"fine": "2022-12-31",\r
                    			"oggetto": "1.59",\r
                    			"isFid": "No",\r
                    			"tipoPromo": {\r
                    				"idTipoPromo": "1"\r
                    			}\r
                    		}\r
                    	]\r
                    }""";

    @Test
    @Order(1)
    @DisplayName("TEST ERRORE VALIDAZIONE CODICE")
    void errInsPromoById1() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/promo/inserisci?lang=it")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonData)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Il codice della promozione deve avere un numero di caratteri compreso fra 3 e 10"))
                .andDo(print());
    }

    //DESCRIZIONE MANCANTE
    String jsonData2 = """
            {\r
            	"idPromo": "",\r
            	"anno": 2022,\r
            	"codice": "TEST01",\r
            	"descrizione": "",\r
            	"dettPromos": [\r
            		{\r
            			"id": -1,\r
            			"riga": 1,\r
            			"codArt": "049477701",\r
            			"codFid": "",\r
            			"inizio": "2022-01-01",\r
            			"fine": "2022-12-31",\r
            			"oggetto": "1.59",\r
            			"isFid": "No",\r
            			"tipoPromo": {\r
            				"idTipoPromo": "1"\r
            			}\r
            		}\r
            	]\r
            }""";

    @Test
    @Order(2)
    @DisplayName("TEST ERRORE VALIDAZIONE DESCRIZIONE")
    void errInsPromoById2() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/promo/inserisci?lang=it")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonData2)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("E' necessario inserire la descrizione della promozione"))
                .andDo(print());
    }

    //CODICE ARTICOLO MANCANTE
    String jsonData3 = """
            {\r
            	"idPromo": "",\r
            	"anno": 2022,\r
            	"codice": "TEST01",\r
            	"descrizione": "PROMO TEST01",\r
            	"dettPromos": [\r
            		{\r
            			"id": -1,\r
            			"riga": 1,\r
            			"codArt": "",\r
            			"codFid": "",\r
            			"inizio": "2022-01-01",\r
            			"fine": "2022-12-31",\r
            			"oggetto": "1.59",\r
            			"isFid": "No",\r
            			"tipoPromo": {\r
            				"idTipoPromo": "1"\r
            			}\r
            		}\r
            	]\r
            }""";

    @Test
    @Order(3)
    @DisplayName("TEST ERRORE VALIDAZIONE CODICE ARTICOLO")
    void errInsPromoById3() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/promo/inserisci?lang=it")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonData3)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Il codice articolo deve avere un numero di caratteri compreso tra 5 e 20"))
                .andDo(print());
    }

    //OGGETTO MANCANTE
    String jsonData4 = """
            {\r
            	"idPromo": "",\r
            	"anno": 2022,\r
            	"codice": "TEST01",\r
            	"descrizione": "PROMO TEST01",\r
            	"dettPromos": [\r
            		{\r
            			"id": -1,\r
            			"riga": 1,\r
            			"codArt": "049477701",\r
            			"codFid": "",\r
            			"inizio": "2022-01-01",\r
            			"fine": "2022-12-31",\r
            			"oggetto": "",\r
            			"isFid": "No",\r
            			"tipoPromo": {\r
            				"idTipoPromo": "1"\r
            			}\r
            		}\r
            	]\r
            }""";

    @Test
    @Order(4)
    @DisplayName("TEST ERRORE VALIDAZIONE OGGETTO")
    void errInsPromo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/promo/inserisci?lang=it")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonData4)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("E' necessario inserire l'oggetto della promozione"))
                .andDo(print());
    }

    String jsonData5 = """
            {\r
            	"idPromo": "",\r
            	"anno": 2022,\r
            	"codice": "TEST01",\r
            	"descrizione": "PROMO TEST01",\r
            	"dettPromos": [\r
            		{\r
            			"id": -1,\r
            			"riga": 1,\r
            			"codArt": "049477701",\r
            			"codFid": "",\r
            			"inizio": "2022-01-01",\r
            			"fine": "2022-12-31",\r
            			"oggetto": "1.89",\r
            			"isFid": "No" \r
            		}\r
            	]\r
            }""";

    @Test
    @Order(4)
    @DisplayName("TEST ERRORE VALIDAZIONE TIPO PROMOZIONE")
    void errInsPromo2() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/promo/inserisci?lang=it")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonData5)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Il tipo promozione della riga promo non può essere null"))
                .andDo(print());
    }
}
