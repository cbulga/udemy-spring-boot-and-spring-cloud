package com.xantrix.webapp.feign;

import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
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
}
