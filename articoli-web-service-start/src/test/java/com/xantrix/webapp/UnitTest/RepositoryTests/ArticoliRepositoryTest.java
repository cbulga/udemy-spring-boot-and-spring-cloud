package com.xantrix.webapp.UnitTest.RepositoryTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.xantrix.webapp.entities.Articoli;
import com.xantrix.webapp.entities.Barcode;
import com.xantrix.webapp.entities.FamAssort;
import com.xantrix.webapp.repository.ArticoliRepository;

import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
//import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
//@ContextConfiguration(classes = Application.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ArticoliRepositoryTest {
    @Autowired
    private ArticoliRepository articoliRepository;

    @Test
    public void A_TestInsArticolo() {
        Articoli articolo = new Articoli("123Test", "Articolo di Test", 6, 1.75, "1");

        FamAssort famAssort = new FamAssort();
        famAssort.setId(1);
        articolo.setFamAssort(famAssort);

        Set<Barcode> Eans = new HashSet<>();
        Eans.add(new Barcode(articolo, "12345678", "CP"));

        articolo.setBarcode(Eans);

        articoliRepository.save(articolo);

        assertThat(articoliRepository.findByCodArt("123Test"))
                .extracting(Articoli::getDescrizione)
                .isEqualTo("Articolo di Test");
    }

    @Test
    public void B_TestSelByDescrizioneLike() {
        List<Articoli> items = articoliRepository.findByDescrizioneLike("Articolo di Test");
        assertEquals(1, items.size());
    }

    @Test
    public void C_TestfindByEan() throws Exception {
        assertThat(articoliRepository.SelByEan("12345678"))
                .extracting(Articoli::getDescrizione)
                .isEqualTo("Articolo di Test");

    }

    @Test
    public void D_TestDelArticolo() {
        Articoli articolo = articoliRepository.findByCodArt("123Test");

        articoliRepository.delete(articolo);

    }


}