package com.xantrix.webapp.config;

import com.xantrix.webapp.dto.ClientiDTO;
import com.xantrix.webapp.entity.Cards;
import com.xantrix.webapp.entity.Clienti;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.Condition;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class ModelMapperConfig {

    private static final Condition<String, String> STRING_NOT_BLANK = ctx -> StringUtils.isNotBlank(ctx.getSource());
    private static final Condition<Long, Long> LONG_VALUE_PERSISTED = ctx -> ctx.getSource() != null && ctx.getSource() > 0;
    private static final Converter<Clienti, ClientiDTO> CLIENTI_TO_CLIENTI_DTO_CONVERTER = context -> {
        Clienti clienti = context.getSource();
        // FIXME: ModelMapper non funziona correttamente al momento! non supporta le JDK > 1.8 ed in particolare se si usa la PropertyMap viene sollevata un'eccezione del tipo "org.modelmapper.configurationexception: modelmapper configuration errors: 1) failed to configure mappings java.lang.illegalargumentexception: unsupported class file major version 63". Quindi non ho usato il ModelMapper per questo progetto...
        ClientiDTO clientiDTO = ClientiDTO.builder()
                .nominativo(String.format("%s %s", StringUtils.defaultString(clienti.getNome()), StringUtils.defaultString(clienti.getCognome())))
                .indirizzo(clienti.getIndirizzo())
                .comune(clienti.getComune())
                .cap(clienti.getCap())
                .telefono(clienti.getTelefono())
                .mail(clienti.getMail())
                .stato(clienti.getStato())
                .bollini(Optional.ofNullable(clienti.getCard()).map(Cards::getBollini).orElse(null))
                .ultimaSpesa(Optional.ofNullable(clienti.getCard()).map(Cards::getUltimaSpesa).orElse(null))
                .build();
        return clientiDTO;
    };

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);
//        TypeMap<ClientiDTO, Clienti> clientiDTOToClientiTypeMap = modelMapper.createTypeMap(ClientiDTO.class, Clienti.class);
//        clientiDTOToClientiTypeMap.addMappings(mapper -> mapper.when(STRING_NOT_BLANK).map(ClientiDTO::getCodice, Clienti::setCodice));
        TypeMap<Clienti, ClientiDTO> clientiToClientiDTOTypeMap = modelMapper.createTypeMap(Clienti.class, ClientiDTO.class);
        clientiToClientiDTOTypeMap.addMappings(mapper -> mapper.when(STRING_NOT_BLANK).map(Clienti::getCodice, ClientiDTO::setCodice));
//        clientiToClientiDTOTypeMap.addMappings(mapper -> mapper.using(CLIENTI_TO_CLIENTI_DTO_CONVERTER).map()when(STRING_NOT_BLANK).map(Clienti::getCodice, ClientiDTO::setCodice));
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
