package com.xantrix.webapp.controller;

import com.xantrix.webapp.service.ArticoliService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.config.CacheManagementConfigUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class CachingController {

    @Autowired
    ArticoliService articoliService;

    @GetMapping("clearAllCaches")
    public void clearAllCaches() {
        log.info("Clearing all caches");
        articoliService.cleanCaches();
    }
}
