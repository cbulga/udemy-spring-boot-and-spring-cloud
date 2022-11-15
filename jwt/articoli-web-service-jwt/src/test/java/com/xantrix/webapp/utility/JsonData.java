package com.xantrix.webapp.utility;

@SuppressWarnings("unused")
public class JsonData {

    public static String getArtData() {

        return "{\n" +
                "    \"codArt\": \"002000301\",\n" +
                "    \"descrizione\": \"ACQUA ULIVETO 15 LT\",\n" +
                "    \"um\": \"PZ\",\n" +
                "    \"codStat\": \"\",\n" +
                "    \"pzCart\": 6,\n" +
                "    \"pesoNetto\": 1.5,\n" +
                "    \"idStatoArt\": \"1\",\n" +
                "    \"dataCreazione\": \"2010-06-14\",\n" +
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
    }

    public static String getTestArtData() {

        return "{\r\n" +
                "    \"codArt\": \"5001234949\",\r\n" +
                "    \"descrizione\": \"Articoli Unit Test Inserimento\",\r\n" +
                "    \"um\": \"PZ\",\r\n" +
                "    \"codStat\": \"TESTART\",\r\n" +
                "    \"pzCart\": 6,\r\n" +
                "    \"pesoNetto\": 1.75,\r\n" +
                "    \"idStatoArt\": \"1\",\r\n" +
                "    \"dataCreazione\": \"2019-05-14\",\r\n" +
                "    \"barcode\": [\r\n" +
                "        {\r\n" +
                "            \"barcode\": \"12345678\",\r\n" +
                "            \"idTipoArt\": \"CP\"\r\n" +
                "        }\r\n" +
                "    ],\r\n" +
                "    \"ingredienti\": null,\r\n" +
                "    \"iva\": {\r\n" +
                "        \"idIva\": 22,\r\n" +
                "        \"descrizione\": \"IVA RIVENDITA 22%\",\r\n" +
                "        \"aliquota\": 22\r\n" +
                "    },\r\n" +
                "    \"famAssort\": {\r\n" +
                "        \"id\": 1,\r\n" +
                "        \"descrizione\": \"DROGHERIA ALIMENTARE\"\r\n" +
                "    }\r\n" +
                "}";
    }
}
