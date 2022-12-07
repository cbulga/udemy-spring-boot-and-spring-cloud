package com.xantrix.webapp.tests.RepositoryTests;

import com.xantrix.webapp.Application;
import com.xantrix.webapp.entity.Articoli;
import com.xantrix.webapp.entity.Barcode;
import com.xantrix.webapp.entity.FamAssort;
import com.xantrix.webapp.repository.ArticoliRepository;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest()
@ContextConfiguration(classes = Application.class)
@TestMethodOrder(OrderAnnotation.class)
class ArticoliRepositoryTest {

    public static final String COD_ART_TEST = "500123456789";
    @Autowired
    private ArticoliRepository articoliRepository;

    @Test
    @Order(1)
    void testInsArticolo() {
        Articoli articolo = Articoli.builder()
                .codArt(COD_ART_TEST)
                .descrizione("Articolo di Test")
                .pzCart(6)
                .pesoNetto(1.75)
                .idStatoArt("1")
                .codStat("TESTTEST")
                .build();

        FamAssort famAssort = new FamAssort();
        famAssort.setId(1);
        articolo.setFamAssort(famAssort);

        Set<Barcode> eans = new HashSet<>();
        eans.add(new Barcode("12345678", "CP", articolo));

        articolo.setBarcode(eans);

        articoliRepository.save(articolo);

        assertThat(articoliRepository.findByCodArt(COD_ART_TEST))
                .extracting(Articoli::getDescrizione)
                .isEqualTo("Articolo di Test");
    }

    @Test
    @Order(2)
    void testSelByDescrizioneLike() {
        List<Articoli> items = articoliRepository.findByDescrizioneLike("Articolo di Test");
        assertEquals(1, items.size());
    }

    @Test
    @Order(3)
    void testFindByEan() {
        assertThat(articoliRepository.selByEan("12345678"))
                .extracting(Articoli::getDescrizione)
                .isEqualTo("Articolo di Test");
    }

    @Test
    @Order(4)
    void testDelArticolo() {
        Articoli articolo = articoliRepository.findByCodArt(COD_ART_TEST);
        articoliRepository.delete(articolo);
        assertThat(articoliRepository.findByCodArt(COD_ART_TEST)).isNull();
    }
}