package com.xantrix.webapp.feign;

import com.xantrix.webapp.dto.ArticoliDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "ProductsWebService", configuration = FeignConfiguration.class)
public interface ArticoliClient {

    @GetMapping(value = "/api/articoli/cerca/codice/{codart}")
    ResponseEntity<ArticoliDTO> getArticolo(@RequestHeader("Authorization") String authHeader, @PathVariable("codart") String codArt);

    @GetMapping(value = "/api/articoli/cerca/codice/{codart}")
    ResponseEntity<ArticoliDTO> getDefArticolo(@RequestHeader("Authorization") String authHeader, @PathVariable("codart") String codArt);
}
