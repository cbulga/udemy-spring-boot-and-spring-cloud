package com.xantrix.webapp.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "PromoPrezziWebService", configuration = FeignConfiguration.class)
public interface PromoClient {

    @GetMapping(value = "/api/promo/prezzo/{codArt}")
    double getPromoPrice(@RequestHeader("Authorization") String authHeader, @PathVariable("codArt") String codArt);
}
