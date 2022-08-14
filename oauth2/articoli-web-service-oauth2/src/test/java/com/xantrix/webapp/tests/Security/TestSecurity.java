package com.xantrix.webapp.tests.Security;

import com.xantrix.webapp.Application;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = Application.class)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestSecurity {

    private static final String apiBaseUrl = "/api/articoli";
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity()) //Attiva la sicurezza
                .build();
    }

    @Test
    @WithAnonymousUser
    @Order(1)
    void testErrlistArtByCodArt() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(apiBaseUrl + "/cerca/codice/002000301")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                // redirect to keycloak sso page
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/sso/login"))
                .andReturn();
    }

    @Order(2)
    @Test
    @WithMockUser(username = "cristian", roles = {"User"})
    public void testSecurityErrlistArt2() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(apiBaseUrl + "/cerca/codice/002000301")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }
}
