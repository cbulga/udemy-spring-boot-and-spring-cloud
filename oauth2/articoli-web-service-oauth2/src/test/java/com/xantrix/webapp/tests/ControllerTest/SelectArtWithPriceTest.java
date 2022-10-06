package com.xantrix.webapp.tests.ControllerTest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xantrix.webapp.Application;
import lombok.SneakyThrows;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * INTEGRATION TEST! PriceArtWebService must be up & running before!
 */
@ContextConfiguration(classes = Application.class)
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class SelectArtWithPriceTest {
    private MockMvc mockMvc;

    private String tokenJwt = "";

    private static final String authServerUrl = "http://localhost:8080/realms/Alphashop/protocol/openid-connect/token";
    private static final String username = "Cristian";
    private static final String password = "AmalaPazzaInterAmala";
    private static final String grant_type = "password";
    private static final String client_id = "Articoli-Web-Service";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WebApplicationContext wac;

    @BeforeEach
    public void setup() throws JSONException, IOException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .build();

        if (tokenJwt.length() == 0)
            getTokenFromAuthSrv();
    }

    @SneakyThrows
    private void getTokenFromAuthSrv() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //headers.setContentType(MediaType.APPLICATION_JSON);

        String parameters = String.format("client_id=%s&username=%s&password=%s&grant_type=%s", client_id, username, password, grant_type);
		
		/*
		JSONObject parameters = new JSONObject();
		userJson.put("username", userId);
		userJson.put("password", password);
		*/

        HttpEntity<String> request = new HttpEntity<String>(parameters, headers);

        String jsonBody = restTemplate.postForObject(authServerUrl, request, String.class);
        JsonNode root = objectMapper.readTree(jsonBody);

        tokenJwt = "Bearer " + root.path("access_token").asText();

    }

    String JsonData =
            "{\n" +
                    "    \"codArt\": \"002000301\",\n" +
                    "    \"descrizione\": \"ACQUA ULIVETO 15 LT\",\n" +
                    "    \"um\": \"PZ\",\n" +
                    "    \"codStat\": \"\",\n" +
                    "    \"pzCart\": 6,\n" +
                    "    \"pesoNetto\": 1.5,\n" +
                    "    \"idStatoArt\": \"1\",\n" +
                    "    \"dataCreazione\": \"2010-06-14\",\n" +
                    "    \"prezzo\": 1.02,\r\n" + //<-- Aggiunto Prezzo Listino 1 pari a 1.07, ma scontato del 5%
                    "    \"barcode\": [\n" +
                    "        {\n" +
                    "            \"barcode\": \"8008490000021\",\n" +
                    "            \"idTipoArt\": \"CP\"\n" +
                    "        }\n" +
                    "    ],\n" +
                    "    \"famAssort\": {\n" +
                    "        \"id\": 1,\n" +
                    "        \"descrizione\": \"DROGHERIA ALIMENTARE\"\n" +
                    "    },\n" +
                    "    \"ingredienti\": null,\n" +
                    "    \"iva\": {\n" +
                    "        \"idIva\": 22,\n" +
                    "        \"descrizione\": \"IVA RIVENDITA 22%\",\n" +
                    "        \"aliquota\": 22\n" +
                    "    }\n" +
                    "}";

    @Test
    @Order(1)
    //@Disabled("Test disabilitato")
    public void listArtByEan() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/articoli/cerca/ean/8008490000021")
                        .header("Authorization", this.tokenJwt)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                //articoli
                .andExpect(jsonPath("$.codArt").exists())
                .andExpect(jsonPath("$.codArt").value("002000301"))
                .andExpect(jsonPath("$.descrizione").exists())
                .andExpect(jsonPath("$.descrizione").value("ACQUA ULIVETO 15 LT"))
                .andExpect(jsonPath("$.um").exists())
                .andExpect(jsonPath("$.um").value("PZ"))
                .andExpect(jsonPath("$.codStat").exists())
                .andExpect(jsonPath("$.codStat").value(""))
                .andExpect(jsonPath("$.pzCart").exists())
                .andExpect(jsonPath("$.pzCart").value("6"))
                .andExpect(jsonPath("$.pesoNetto").exists())
                .andExpect(jsonPath("$.pesoNetto").value("1.5"))
                .andExpect(jsonPath("$.idStatoArt").exists())
                .andExpect(jsonPath("$.idStatoArt").value("1"))
                .andExpect(jsonPath("$.dataCreazione").exists())
                .andExpect(jsonPath("$.dataCreazione").value("2010-06-14"))
                .andExpect(jsonPath("$.prezzo").exists())
                .andExpect(jsonPath("$.prezzo").value("1.02")) //<-- Aggiunto Prezzo Listino 1 (prezzo 1.07 scontato al 5%)
                //barcode
                .andExpect(jsonPath("$.barcode[0].barcode").exists())
                .andExpect(jsonPath("$.barcode[0].barcode").value("8008490000021"))
                .andExpect(jsonPath("$.barcode[0].idTipoArt").exists())
                .andExpect(jsonPath("$.barcode[0].idTipoArt").value("CP"))
                //famAssort
                .andExpect(jsonPath("$.famAssort.id").exists())
                .andExpect(jsonPath("$.famAssort.id").value("1"))
                .andExpect(jsonPath("$.famAssort.descrizione").exists())
                .andExpect(jsonPath("$.famAssort.descrizione").value("DROGHERIA ALIMENTARE"))
                //ingredienti
                .andExpect(jsonPath("$.ingredienti").isEmpty())
                //Iva
                .andExpect(jsonPath("$.iva.idIva").exists())
                .andExpect(jsonPath("$.iva.idIva").value("22"))
                .andExpect(jsonPath("$.iva.descrizione").exists())
                .andExpect(jsonPath("$.iva.descrizione").value("IVA RIVENDITA 22%"))
                .andExpect(jsonPath("$.iva.aliquota").exists())
                .andExpect(jsonPath("$.iva.aliquota").value("22"))

                .andDo(print());
    }

    @Test
    @Order(2)
    //@Disabled("Test disabilitato")
    public void listArtByEanWithIdList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/articoli/cerca/ean/8008490000021/2")
                        .header("Authorization", this.tokenJwt)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.prezzo").exists())
                .andExpect(jsonPath("$.prezzo").value("0.83")) //<-- Prezzo Listino 2 (prezzo 0.87 scontato del 5%)
                .andDo(print());
    }

    private String barcode = "8008490002138";

    @Test
    @Order(3)
    //@Disabled("Test disabilitato")
    public void errlistArtByEan() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/articoli/cerca/ean/" + barcode)
                        .header("Authorization", this.tokenJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonData)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codice").value(404))
                .andExpect(jsonPath("$.messaggio").value("Il barcode " + barcode + " non è stato trovato!"));
    }

    @Test
    @Order(4)
    //@Disabled("Test disabilitato")
    public void listArtByCodArt() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/articoli/cerca/codice/002000301")
                        .header("Authorization", this.tokenJwt)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(JsonData))
                .andReturn();
    }

    @Test
    @Order(5)
    //@Disabled("Test disabilitato")
    public void listArtByCodArtWithIdList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/articoli/cerca/codice/002000301/2")
                        .header("Authorization", this.tokenJwt)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.prezzo").exists())
                .andExpect(jsonPath("$.prezzo").value("0.83")) //<-- Prezzo Listino 2 (prezzo 0.87 scontato al 5%)
                .andDo(print());
    }

    private String CodArt = "002000301b";

    @Test
    @Order(6)
    //@Disabled("Test disabilitato")
    public void errlistArtByCodArt() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/articoli/cerca/codice/" + CodArt)
                        .header("Authorization", this.tokenJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonData)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codice").value(404))
                .andExpect(jsonPath("$.messaggio").value("L'articolo con codice " + CodArt + " non è stato trovato!"));
    }

    private String JsonData2 = "[" + JsonData + "]";

    @Test
    @Order(7)
    //@Disabled("Test disabilitato")
    public void listArtByDesc() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/articoli/cerca/descrizione/ACQUA ULIVETO 15 LT")
                        .header("Authorization", this.tokenJwt)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(JsonData2))
                .andReturn();
    }

    @Test
    @Order(8)
    //@Disabled("Test disabilitato")
    public void listArtByDescWithIdList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/articoli/cerca/descrizione/ACQUA ULIVETO 15 LT/2")
                        .header("Authorization", this.tokenJwt)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].prezzo").exists())
                .andExpect(jsonPath("$[0].prezzo").value("0.83")) //<-- Prezzo Listino 2 (prezzo 0.87 scontato al 5%)
                .andDo(print());
    }

    @Test
    @Order(9)
    //@Disabled("Test disabilitato")
    public void errlistArtByDesc() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/articoli/cerca/descrizione/ABC1234")
                        .header("Authorization", this.tokenJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonData)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codice").value(404))
                .andExpect(jsonPath("$.messaggio").value("Non è stato trovato alcun articolo avente descrizione ABC1234"));
    }
}
