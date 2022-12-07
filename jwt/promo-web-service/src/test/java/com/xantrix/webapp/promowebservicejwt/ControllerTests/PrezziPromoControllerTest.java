package com.xantrix.webapp.promowebservicejwt.ControllerTests;

import com.xantrix.webapp.Application;
import com.xantrix.webapp.entity.DettPromo;
import com.xantrix.webapp.entity.Promo;
import com.xantrix.webapp.entity.TipoPromo;
import com.xantrix.webapp.repository.PrezziPromoRepository;
import com.xantrix.webapp.repository.PromoRepository;
import com.xantrix.webapp.service.PromoServiceImpl;
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

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SuppressWarnings("SpringBootApplicationProperties")
@ContextConfiguration(classes = Application.class)
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("Test Classe Controller PrezziPromo")
@TestPropertySource(properties = {"profilo = test", "seq = 1", "ramo = main"})
class PrezziPromoControllerTest {

    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private PromoRepository promoRepository;
    @Autowired
    private PromoServiceImpl promoService;
    @Autowired
    private PrezziPromoRepository prezziPromoRepository;
    int anno = Year.now().getValue();
    String idPromo = "";
    String codice = "TEST01";
    String descrizione = "PROMO TEST1";
    private static boolean isInitialized = false;
    private static boolean isTerminated = false;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

        if (isInitialized) return;

        //Nota: Tutte le promo vengono eliminate
        promoRepository.deleteAll();

        UUID uuid = UUID.randomUUID();
        idPromo = uuid.toString();

        Promo promo = new Promo(idPromo, anno, codice, descrizione);
        promoRepository.save(promo);

        //La promo sarà valida l'intero anno corrente
        LocalDate inizio = LocalDate.of(anno, Month.JANUARY, 1);
        LocalDate fine = LocalDate.of(anno, Month.DECEMBER, 31);

        DettPromo dettPromo = new DettPromo();

        promo = promoRepository.findByAnnoAndCodice(anno, codice);

        //riga 1 promozione standard
        dettPromo.setId(-1L);
        dettPromo.setInizio(inizio);
        dettPromo.setFine(fine);
        dettPromo.setCodArt("049477701");
        dettPromo.setOggetto("1.10");
        dettPromo.setIsFid("No");
        dettPromo.setRiga(1);
        dettPromo.setTipoPromo(new TipoPromo(1));

        dettPromo.setPromo(promo);

        prezziPromoRepository.save(dettPromo);

        //riga 2 promozione fidelity
        dettPromo.setId(-1L);
        dettPromo.setInizio(inizio);
        dettPromo.setFine(fine);
        dettPromo.setCodArt("004590201");
        dettPromo.setOggetto("1.99");
        dettPromo.setIsFid("Si");
        dettPromo.setRiga(2);
        dettPromo.setTipoPromo(new TipoPromo(1));

        dettPromo.setPromo(promo);

        prezziPromoRepository.save(dettPromo);

        //riga 3 promozione fidelity Only You
        dettPromo.setId(-1L);
        dettPromo.setInizio(inizio);
        dettPromo.setFine(fine);
        dettPromo.setCodArt("008071001");
        dettPromo.setOggetto("2.19");
        dettPromo.setIsFid("Si");
        dettPromo.setCodFid("67000076");
        dettPromo.setRiga(3);
        dettPromo.setTipoPromo(new TipoPromo(1));

        dettPromo.setPromo(promo);

        prezziPromoRepository.save(dettPromo);

        anno = anno - 1; //assicuriamoci che la promo sia scaduta

        inizio = LocalDate.of(anno, Month.JANUARY, 1);
        fine = LocalDate.of(anno, Month.DECEMBER, 31);

        //riga 4 promozione standard scaduta
        dettPromo.setId(-1L);
        dettPromo.setInizio(inizio);
        dettPromo.setFine(fine);
        dettPromo.setCodArt("002001601");
        dettPromo.setOggetto("0.99");
        dettPromo.setIsFid("No");
        dettPromo.setRiga(4);
        dettPromo.setTipoPromo(new TipoPromo(1));

        dettPromo.setPromo(promo);

        prezziPromoRepository.save(dettPromo);

        isInitialized = true;
    }

    private void insertPromo2() {
        String codice = "TEST02";
        String descrizione = "PROMO TEST2";

        Promo promo = new Promo(idPromo, anno, codice, descrizione);
        promoRepository.save(promo);

        //La promo sarà valida l'intero anno corrente
        LocalDate Inizio = LocalDate.of(anno, Month.JANUARY, 1);
        LocalDate Fine = LocalDate.of(anno, Month.DECEMBER, 31);

        DettPromo dettPromo = new DettPromo();

        promo = promoRepository.findByAnnoAndCodice(anno, codice);

        //riga 1 promozione standard
        dettPromo.setId(-1L);
        dettPromo.setInizio(Inizio);
        dettPromo.setFine(Fine);
        dettPromo.setCodArt("049477701");
        dettPromo.setOggetto("0.89"); //Prezzo inferiore a parità di condizioni
        dettPromo.setIsFid("No");
        dettPromo.setRiga(1);
        dettPromo.setTipoPromo(new TipoPromo(1));

        dettPromo.setPromo(promo);

        prezziPromoRepository.save(dettPromo);
    }

    @Test
    @Order(1)
    @DisplayName("Test Prezzo per Codice Articolo")
    void getPromoCodArt() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/promo/prezzo/049477701")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value("1.1"))
                .andReturn();
    }

    @Test
    @Order(2)
    @DisplayName("Test Prezzo per Codice Articolo riservato a fidelity")
    void getPromoCodArtFid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/promo/prezzo/fidelity/004590201")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value("1.99"))
                .andReturn();
    }

    @Test
    @Order(3)
    @DisplayName("Test Prezzo per Codice Articolo e fidelity")
    void getPromoCodArtCodFid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/promo/prezzo/008071001/67000076")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value("2.19"))
                .andReturn();
    }

    @Test
    @Order(4)
    @DisplayName("Test Prezzo Promo Scaduta")
    void getPromoScad() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/promo/prezzo/002001601")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value("0.0"))
                .andReturn();
    }

    @Test
    @Order(5)
    @DisplayName("Test Prezzo per Codice Articolo con due promo attive")
    void getPromoCodArt2() throws Exception {
        //Eliminiamo la cache
        promoService.cleanCaches();

        this.insertPromo2();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/promo/prezzo/049477701")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value("0.89")) //E' necessario ottenere il prezzo minore a parità di condizioni
                .andReturn();

        isTerminated = true;
    }

    @AfterEach
    public void delPromo() {
        if (isTerminated)
            promoRepository.deleteAll();
    }
}
