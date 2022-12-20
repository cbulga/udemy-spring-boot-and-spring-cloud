package com.xantrix.webapp.tests;

import com.xantrix.webapp.Application;
import com.xantrix.webapp.repository.UtentiRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static com.xantrix.webapp.controller.UtentiController.INSERIMENTO_UTENTE_ESEGUITO_CON_SUCCESSO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * The following web-services have to be up & running:<ul>
 *     <li>config server</li>
 *     <li>eureka</li>
 * </ul>
 */
@SpringBootTest
@TestPropertySource(properties = {"profilo = test", "seq = 9", "ramo = main"})
@ContextConfiguration(classes = Application.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UtentiControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UtentiRepository utentiRepository;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    String jsonData =
            """
                    {
                        "userId": "Cristian",
                        "password": "123Stella",
                        "attivo": "Si",
                        "ruoli": [
                                "USER"
                            ]
                    }""";

    @Test
    @Order(1)
    void testInsUtente1() throws Exception {
        //Eliminiamo tutti gli utenti
        utentiRepository.deleteAll();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/utenti/inserisci")
                        .characterEncoding(StandardCharsets.UTF_8.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonData)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.toString()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(String.format(INSERIMENTO_UTENTE_ESEGUITO_CON_SUCCESSO, "Cristian")))
                .andDo(print());
    }

    @Test
    @Order(2)
    void testListUserByUserId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/utenti/cerca/userid/Cristian")
                        .characterEncoding(StandardCharsets.UTF_8.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.userId").value("Cristian"))
                .andExpect(jsonPath("$.password").exists())
                .andExpect(jsonPath("$.attivo").exists())
                .andExpect(jsonPath("$.attivo").value("Si"))

                .andExpect(jsonPath("$.ruoli[0]").exists())
                .andExpect(jsonPath("$.ruoli[0]").value("USER"));

        assertThat(passwordEncoder.matches("123Stella",
                utentiRepository.findByUserId("Cristian").getPassword()))
                .isTrue();
    }

    String jsonData2 =
            """
                    {
                        "userId": "Admin",
                        "password": "VerySecretPwd",
                        "attivo": "Si",
                        "ruoli": [
                                "USER",
                                "ADMIN"
                            ]
                    }""";

    @Test
    @Order(3)
    void testInsUtente2() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/utenti/inserisci")
                        .characterEncoding(StandardCharsets.UTF_8.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonData2)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    String jsonDataUsers =
            """
                    [
                    	{
                    	    "userId": "Cristian",
                    	    "password": "123Stella",
                    	    "attivo": "Si",
                    	    "ruoli": [
                    		    "USER"
                    		]
                    	},
                    	{
                    	    "userId": "Admin",
                    	    "password": "VerySecretPwd",
                    	    "attivo": "Si",
                    	    "ruoli": [
                    		    "USER",
                    		    "ADMIN"
                    		]
                    	}
                    ]""";

    @Test
    @Order(4)
    void testGetAllUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/utenti/cerca/tutti")
                        .characterEncoding(StandardCharsets.UTF_8.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                //UTENTE 1
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].userId").exists())
                .andExpect(jsonPath("$[0].userId").value("Cristian"))
                .andExpect(jsonPath("$[0].password").exists())
                .andExpect(jsonPath("$[0].attivo").exists())
                .andExpect(jsonPath("$[0].attivo").value("Si"))
                .andExpect(jsonPath("$[0].ruoli[0]").exists())
                .andExpect(jsonPath("$[0].ruoli[0]").value("USER"))
                //UTENTE 2
                .andExpect(jsonPath("$[1].id").exists())
                .andExpect(jsonPath("$[1].userId").exists())
                .andExpect(jsonPath("$[1].userId").value("Admin"))
                .andExpect(jsonPath("$[1].password").exists())
                .andExpect(jsonPath("$[1].attivo").exists())
                .andExpect(jsonPath("$[1].attivo").value("Si"))
                .andExpect(jsonPath("$[1].ruoli[0]").exists())
                .andExpect(jsonPath("$[1].ruoli[0]").value("USER"))
                .andExpect(jsonPath("$[1].ruoli[1]").exists())
                .andExpect(jsonPath("$[1].ruoli[1]").value("ADMIN"))
                .andReturn();

        assertThat(passwordEncoder.matches("123Stella",
                utentiRepository.findByUserId("Cristian").getPassword()))
                .isTrue();
        assertThat(passwordEncoder.matches("VerySecretPwd",
                utentiRepository.findByUserId("Admin").getPassword()))
                .isTrue();
    }

//    @Test
//    @Order(5)
//    void testDelUtente1() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.delete("/api/utenti/elimina/Cristian")
//                        .characterEncoding(StandardCharsets.UTF_8.toString())
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value("200 OK"))
//                .andExpect(jsonPath("$.message").value("Eliminazione Utente Cristian Eseguita Con Successo"));
//    }
//
//    @Test
//    @Order(6)
//    void testDelUtente2() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.delete("/api/utenti/elimina/Admin")
//                        .characterEncoding(StandardCharsets.UTF_8.toString())
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value("200 OK"))
//                .andExpect(jsonPath("$.message").value("Eliminazione Utente Admin Eseguita Con Successo"));
//    }
}


