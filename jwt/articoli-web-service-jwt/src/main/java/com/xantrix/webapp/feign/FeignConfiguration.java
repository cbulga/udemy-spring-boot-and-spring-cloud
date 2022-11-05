package com.xantrix.webapp.feign;

import feign.Logger;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients
public class FeignConfiguration {

    @Bean
    public ErrorDecoder getFeignErrorDecoder() {
//        return new FeignErrorDecoder();
        return new ErrorAndRetryDecoder();
    }

    @Bean
    public Retryer getCustomRetryer() {
        return new CustomRetryer();
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }
}
