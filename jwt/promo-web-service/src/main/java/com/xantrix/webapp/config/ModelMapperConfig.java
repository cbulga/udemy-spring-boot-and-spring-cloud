package com.xantrix.webapp.config;

import com.xantrix.webapp.dto.*;
import com.xantrix.webapp.entity.DepRifPromo;
import com.xantrix.webapp.entity.DettPromo;
import com.xantrix.webapp.entity.Promo;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.Condition;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    private static final Condition<String, String> STRING_NOT_BLANK = ctx -> StringUtils.isNotBlank(ctx.getSource());
    private static final Condition<Long, Long> LONG_VALUE_PERSISTED = ctx -> ctx.getSource() != null && ctx.getSource() > 0;

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        TypeMap<PromoDTO, Promo> promoDTOToPromoTypeMap = modelMapper.createTypeMap(PromoDTO.class, Promo.class);
        promoDTOToPromoTypeMap.addMappings(mapper -> mapper.when(STRING_NOT_BLANK).map(PromoDTO::getIdPromo, Promo::setIdPromo));
        TypeMap<CreatePromoDTO, Promo> createPromoDTOToPromoTypeMap = modelMapper.createTypeMap(CreatePromoDTO.class, Promo.class);
        createPromoDTOToPromoTypeMap.addMappings(mapper -> mapper.when(STRING_NOT_BLANK).map(CreatePromoDTO::getIdPromo, Promo::setIdPromo));
        TypeMap<DettPromoDTO, DettPromo> dettPromoDTOToDettPromoTypeMap = modelMapper.createTypeMap(DettPromoDTO.class, DettPromo.class);
        dettPromoDTOToDettPromoTypeMap.addMappings(mapper -> mapper.when(LONG_VALUE_PERSISTED).map(DettPromoDTO::getId, DettPromo::setId));
        TypeMap<DepRifPromoDTO, DepRifPromo> depRifPromoDTOToDepRifPromoTypeMap = modelMapper.createTypeMap(DepRifPromoDTO.class, DepRifPromo.class);
        depRifPromoDTOToDepRifPromoTypeMap.addMappings(mapper -> mapper.when(LONG_VALUE_PERSISTED).map(DepRifPromoDTO::getId, DepRifPromo::setId));
        return modelMapper;
    }
//    private final TipoPromoService tipoPromoService;
////    private final ResourceBundleMessageSource errMessage;
//
//    public ModelMapperConfig(TipoPromoService tipoPromoService) {
//        this.tipoPromoService = tipoPromoService;
////        this.errMessage = errMessage;
//    }
//
//    @Bean
//    public ModelMapper modelMapper() {
//        ModelMapper modelMapper = new ModelMapper();
//        modelMapper.getConfiguration().setSkipNullEnabled(true);
////        PropertyMap<DettPromoDTO, DettPromo> promoMapping = new PropertyMap<>() {
////            protected void configure() {
////                map().setTipoPromo(tipoPromoService.findById(source.getTipoPromo().getIdTipoPromo())
////                        .orElse(null));
////            }
////        };
////        modelMapper.addMappings(promoMapping);
//
////        modelMapper.addMappings(new PropertyMap<Promo, PromoDto>() {
////            @Override
////            protected void configure() {
////                map().setIdTipoArt(source.getIdTipoArt());
////            }
////        });
//
//        modelMapper.addConverter(stringTrimConverter);
//
//        return modelMapper;
//    }
//
////    PropertyMap<DettPromoDTO, DettPromo> promoMapping = new PropertyMap<>() {
////        protected void configure() {
////            map().setTipoPromo(tipoPromoService.findById(source.getTipoPromo().getIdTipoPromo())
////                    .orElse(null));
////        }
////    };
////    PropertyMap<DettPromo, DettPromoDto> dettPromoMapping = new PropertyMap<>() {
////        protected void configure() {
////            map().setDataCreazione(source.getDataCreaz());
////        }
////    };
////    Provider<TipoPromo> tipoPromoProvider = request -> tipoPromoService.findById(request.getSource());
//
//    // i valori null vengono convertiti in ""; se presenti invece vengono trimmati
//    Converter<String, String> stringTrimConverter = context -> context.getSource() == null ? "" : context.getSource().trim();
}
