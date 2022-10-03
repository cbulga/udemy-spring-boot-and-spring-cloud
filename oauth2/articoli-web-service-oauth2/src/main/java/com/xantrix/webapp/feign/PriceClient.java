package com.xantrix.webapp.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "PriceArtWebService", url = "localhost:5071", configuration = FeignConfiguration.class)
public interface PriceClient {

    @GetMapping(value = "/api/prezzi/{codart}")
    ResponseEntity<Double> getDefPriceArt(@RequestHeader("Authorization") String authHeader, @PathVariable("codart") String codArt);

    @GetMapping(value = "/api/prezzi/{codart}/{idlist}")
    ResponseEntity<Double> getPriceArt(@RequestHeader("Authorization") String authHeader, @PathVariable("codart") String codArt,
                          @PathVariable("idlist") String idList);
}
