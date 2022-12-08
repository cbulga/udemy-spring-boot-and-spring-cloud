package com.xantrix.webapp.UnitTest.repository;

import com.xantrix.webapp.entity.DettListini;
import com.xantrix.webapp.entity.Listini;
import com.xantrix.webapp.repository.ListinoRepository;
import com.xantrix.webapp.repository.PrezziRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


//@TestPropertySource(locations="classpath:application-list1.properties")
//@ContextConfiguration(classes = Application.class)
@TestPropertySource(properties = {"profilo = std2", "seq = 1", "ramo="})
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PrezziRepositoryTest {

    @Autowired
    private PrezziRepository prezziRepository;

    @Autowired
    private ListinoRepository listinoRepository;

    String IdList = "100";
    String CodArt = "002000301";
    Double Prezzo = 1.00;

    @Test
    @Order(1)
    void testInsListino() {
        Listini listinoTest = new Listini(IdList, "Listino Test 100", "No");

        Set<DettListini> dettListini = new HashSet<>();
        DettListini dettListTest = new DettListini(CodArt, Prezzo, listinoTest);
        dettListini.add(dettListTest);

        listinoTest.setDettListini(dettListini);

        listinoRepository.save(listinoTest);

        assertThat(listinoRepository
                .findById(IdList))
                .isNotEmpty();
    }

    @Test
    @Order(2)
    void testfindByCodArtAndIdList1() {

        assertThat(prezziRepository
                .selByCodArtAndListinoId(CodArt, IdList))
                .extracting(DettListini::getPrezzo)
                .isEqualTo(Prezzo);
    }

    @Test
    @Transactional
    @Order(3)
    void testDeletePrezzo() {

        prezziRepository.delRowDettList(CodArt, IdList);

        assertThat(prezziRepository
                .selByCodArtAndListinoId(CodArt, IdList))
                .isNull();
    }

    @Test
    @Order(4)
    void testDeleteListino() {
        Optional<Listini> listinoTest = listinoRepository.findById(IdList);

        listinoRepository.delete(listinoTest.get());

        assertThat(prezziRepository
                .selByCodArtAndListinoId(CodArt, IdList))
                .isNull();
    }
}




















