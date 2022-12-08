package com.xantrix.webapp.config;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.addConverter(nullStringConverter);

        return modelMapper;
    }

    // i valori null vangono convertiti in ""; se presenti invece vengono trimmati
    Converter<String, String> nullStringConverter = context -> context.getSource() == null ? "" : context.getSource().trim();
}
