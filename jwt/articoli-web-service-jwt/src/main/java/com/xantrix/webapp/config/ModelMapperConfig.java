package com.xantrix.webapp.config;

import com.xantrix.webapp.dtos.ArticoliDTO;
import com.xantrix.webapp.entity.Articoli;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        TypeMap<Articoli, ArticoliDTO> typeMapEntityToDto = modelMapper.createTypeMap(Articoli.class, ArticoliDTO.class);
        typeMapEntityToDto.addMappings(mapper -> mapper.map(Articoli::getDataCreaz, ArticoliDTO::setDataCreazione));
        TypeMap<ArticoliDTO, Articoli> typeMapDtoToEntity = modelMapper.createTypeMap(ArticoliDTO.class, Articoli.class);
        typeMapDtoToEntity.addMappings(mapper -> mapper.map(ArticoliDTO::getDataCreazione, Articoli::setDataCreaz));


//        modelMapper.addMappings(articoliEntityToDtoMapping);
//        modelMapper.addMappings(articoliDtoToEntityMapping);

//        modelMapper.addMappings(new PropertyMap<Barcode, BarcodeDto>() {
//            @Override
//            protected void configure() {
//                map().setIdTipoArt(source.getIdTipoArt());
//            }
//        });

        modelMapper.addConverter(articoliConverter);

        return modelMapper;
    }

    // DOES NOT WORK WITH JAVA > 11
//    PropertyMap<Articoli, ArticoliDto> articoliEntityToDtoMapping = new PropertyMap<>() {
//        protected void configure() {
//            map().setDataCreazione(source.getDataCreaz());
//        }
//    };
//    PropertyMap<ArticoliDto, Articoli> articoliDtoToEntityMapping = new PropertyMap<>() {
//        protected void configure() {
//            map().setDataCreaz(source.getDataCreazione());
//        }
//    };

    // i valori null vangono convertiti in ""; se presenti invece vengono trimmati
    Converter<String, String> articoliConverter = context -> context.getSource() == null ? "" : context.getSource().trim();
}
