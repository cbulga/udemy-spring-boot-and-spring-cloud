package com.xantrix.webapp.feign;

import com.xantrix.webapp.dtos.PrezzoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "PriceArtWebService", configuration = FeignConfiguration.class)
public interface PriceClient {

    @GetMapping(value = "/api/prezzi/{codart}")
    ResponseEntity<Double> getDefPriceArt(@RequestHeader("Authorization") String authHeader, @PathVariable("codart") String codArt);

    @GetMapping(value = "/api/prezzi/{codart}/{idlist}")
    ResponseEntity<Double> getPriceArt(@RequestHeader("Authorization") String authHeader, @PathVariable("codart") String codArt,
                          @PathVariable("idlist") String idList);

    @GetMapping(value = "/api/prezzi/info/{codart}")
    ResponseEntity<PrezzoDto> getDefPriceArt2(@RequestHeader("Authorization") String authHeader, @PathVariable("codart") String codArt);

    @GetMapping(value = "/api/prezzi/info/{codart}/{idlist}")
    ResponseEntity<PrezzoDto> getPriceArt2(@RequestHeader("Authorization") String authHeader, @PathVariable("codart") String codArt,
                                           @PathVariable("idlist") String idList);
}
