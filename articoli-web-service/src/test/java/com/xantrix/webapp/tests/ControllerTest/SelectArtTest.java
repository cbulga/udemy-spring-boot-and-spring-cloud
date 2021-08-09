package com.xantrix.webapp.tests.ControllerTest;

import com.xantrix.webapp.Application;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static com.xantrix.webapp.controller.ArticoliController.BARCODE_NOT_FOUND;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ContextConfiguration(classes = Application.class)
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class SelectArtTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @BeforeEach
    public void setup() throws JSONException, IOException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .build();
    }

    private String apiBaseUrl = "/api/articoli";

    String jsonData =
            "{\n" +
                    "    \"codArt\": \"002000301\",\n" +
                    "    \"descrizione\": \"ACQUA ULIVETO 15 LT\",\n" +
                    "    \"um\": \"PZ\",\n" +
                    "    \"codStat\": \"\",\n" +
                    "    \"pzCart\": 6,\n" +
                    "    \"pesoNetto\": 1.5,\n" +
                    "    \"idStatoArt\": \"1\",\n" +
                    "    \"dataCreaz\": \"2010-06-14\",\n" +
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
    public void testListArtByEan() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(apiBaseUrl + "/cerca/ean/8008490000021")
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
                .andExpect(jsonPath("$.dataCreaz").exists())
                .andExpect(jsonPath("$.dataCreaz").value("2010-06-14"))
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

    private String barCode = "8008490002138";

    @Test
    @Order(2)
    public void testErrlistArtByEan() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(apiBaseUrl + "/cerca/ean/" + barCode)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonData)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codice").value(404))
                .andExpect(jsonPath("$.messaggio").value(String.format(BARCODE_NOT_FOUND, barCode)))
                .andDo(print());
    }

    @Test
    @Order(3)
    public void testListArtByCodArt() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(apiBaseUrl + "/cerca/codice/002000301")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonData))
                .andReturn();
    }

    private String codArt = "002000301b";

    @Test
    @Order(4)
    public void testErrlistArtByCodArt() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(apiBaseUrl + "/cerca/codice/" + codArt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonData)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codice").value(404))
                .andExpect(jsonPath("$.messaggio").value("L'articolo con codice " + codArt + " non è stato trovato!"))
                .andDo(print());
    }

    private String JsonData2 = "[" + jsonData + "]";

    @Test
    @Order(5)
    public void testListArtByDesc() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(apiBaseUrl + "/cerca/descrizione/ACQUA ULIVETO 15 LT")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(JsonData2))
                .andReturn();
    }

    @Test
    @Order(6)
    public void testErrlistArtByDesc() throws Exception {
        String Filter = "123ABC";

        mockMvc.perform(MockMvcRequestBuilders.get(apiBaseUrl + "/cerca/descrizione/" + Filter)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codice").value(404))
                .andExpect(jsonPath("$.messaggio").value("Non è stato trovato alcun articolo avente descrizione " + Filter))
                .andReturn();
    }
}