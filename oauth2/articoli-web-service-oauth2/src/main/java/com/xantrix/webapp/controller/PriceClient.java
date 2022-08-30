package com.xantrix.webapp.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "PriceArtWebService", url = "localhost:5071")
public interface PriceClient {

    @GetMapping(value = "/api/prezzi/{codart}")
    Double getDefPriceArt(@RequestHeader("Authorization") String authHeader, @PathVariable("codart") String codArt);

    @GetMapping(value = "/api/prezzi/{codart}/{idlist}")
    Double getPriceArt(@RequestHeader("Authorization") String authHeader, @PathVariable("codart") String codArt,
                          @PathVariable("idlist") String idList);
}
