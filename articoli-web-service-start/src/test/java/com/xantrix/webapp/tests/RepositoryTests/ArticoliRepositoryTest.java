package com.xantrix.webapp.tests.RepositoryTests;

import com.xantrix.webapp.Application;
import com.xantrix.webapp.entities.Articoli;
import com.xantrix.webapp.entities.Barcode;
import com.xantrix.webapp.entities.FamAssort;
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
public class ArticoliRepositoryTest {

    @Autowired
    private ArticoliRepository articoliRepository;

    @Test
    @Order(1)
    public void testInsArticolo() {
        Articoli articolo = new Articoli("123Test", "Articolo di Test", 6, 1.75, "1");

        FamAssort famAssort = new FamAssort();
        famAssort.setId(1);
        articolo.setFamAssort(famAssort);

        Set<Barcode> eans = new HashSet<>();
        eans.add(new Barcode("12345678", "CP", articolo));

        articolo.setBarcode(eans);

        articoliRepository.save(articolo);

        assertThat(articoliRepository.findByCodArt("123Test"))
                .extracting(Articoli::getDescrizione)
                .isEqualTo("Articolo di Test");
    }

    @Test
    @Order(2)
    public void testSelByDescrizioneLike() {
        List<Articoli> items = articoliRepository.findByDescrizioneLike("Articolo di Test");
        assertEquals(1, items.size());
    }

    @Test
    @Order(3)
    public void testFindByEan() throws Exception {
        assertThat(articoliRepository.selByEan("12345678"))
                .extracting(Articoli::getDescrizione)
                .isEqualTo("Articolo di Test");
    }

    @Test
    @Order(4)
    public void testDelArticolo() {
        Articoli articolo = articoliRepository.findByCodArt("123Test");
        articoliRepository.delete(articolo);
    }
}