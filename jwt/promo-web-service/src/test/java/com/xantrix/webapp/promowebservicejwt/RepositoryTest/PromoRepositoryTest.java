package com.xantrix.webapp.promowebservicejwt.RepositoryTest;


import com.xantrix.webapp.Application;
import com.xantrix.webapp.entity.DettPromo;
import com.xantrix.webapp.entity.Promo;
import com.xantrix.webapp.entity.TipoPromo;
import com.xantrix.webapp.repository.PrezziPromoRepository;
import com.xantrix.webapp.repository.PromoRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("SpringBootApplicationProperties")
@SpringBootTest()
@ContextConfiguration(classes = Application.class)
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("Test PromoRepository")
@TestPropertySource(properties = {"profilo = test", "seq = 1", "ramo = main"})
class PromoRepositoryTest {

    @Autowired
    private PromoRepository promoRepository;
    @Autowired
    private PrezziPromoRepository prezziPromoRepository;

    int anno = Year.now().getValue();

    String idPromo = "";
    String codice = "TEST01";
    String descrizione = "PROMO TEST1";

    private static boolean isInitialized = false;
    private static boolean isTerminated = false;

    @BeforeEach
    public void setUp() {
        if (isInitialized) return;

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
        dettPromo.setRiga(4);
        dettPromo.setTipoPromo(new TipoPromo(1));

        dettPromo.setPromo(promo);

        prezziPromoRepository.save(dettPromo);

        isInitialized = true;
    }

    @Test
    @Order(1)
    @DisplayName("Test prezzo per codice articolo")
    void selByCodArt() {
        assertThat(prezziPromoRepository.selByCodArtAndPromoAttiva("049477701").get(0))
                .extracting(DettPromo::getOggetto)
                .isEqualTo("1.10");
    }

    @Test
    @Order(2)
    @DisplayName("Test prezzo per codice articolo and fidelity attiva")
    void selByCodArtAndFid() {
        assertThat(prezziPromoRepository.selByCodArtAndFidAndPromoAttiva("004590201").get(0))
                .extracting(DettPromo::getOggetto)
                .isEqualTo("1.99");
    }

    @Test
    @Order(3)
    @DisplayName("Test prezzo per codice articolo and codice fidelity")
    void selByCodArtAndCodFid() {
        assertThat(prezziPromoRepository.selByCodArtAndCodFidAndPromoAttiva("008071001", "67000076").get(0))
                .extracting(DettPromo::getOggetto)
                .isEqualTo("2.19");
    }

    @Test
    @Order(4)
    @DisplayName("Test prezzo per codice articolo and promozione scaduta")
    void selPromoScad() {
        assertThat(prezziPromoRepository.selByCodArtAndPromoAttiva("002001601"))
                .isEmpty();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    @Order(5)
    @DisplayName("Test descrizione promo per anno, codice e idPromo")
    void findById() {
        String idPromo = promoRepository.findByAnnoAndCodice(anno, codice).getIdPromo();

        assertThat(promoRepository.findById(idPromo).get())
                .extracting(Promo::getDescrizione)
                .isEqualTo(descrizione);
    }

    @Test
    @Order(6)
    @DisplayName("Test descrizione promo per anno e codice")
    void findByAnnoAndCodice() {
        assertThat(promoRepository.findByAnnoAndCodice(anno, codice))
                .extracting(Promo::getDescrizione)
                .isEqualTo(descrizione);
    }

    @Test
    @Order(7)
    @DisplayName("Test promo attive")
    void selPromoActive() {
        assertThat(promoRepository.selPromoActive()).hasSize(1);

        //Se si aggiungono altri test spostare nell'ultimo in linea cronologica
        isTerminated = true;
    }

    @AfterEach
    void delPromo() {
        if (isTerminated)
            promoRepository.delete(promoRepository.findByAnnoAndCodice(anno, codice));
    }
}
