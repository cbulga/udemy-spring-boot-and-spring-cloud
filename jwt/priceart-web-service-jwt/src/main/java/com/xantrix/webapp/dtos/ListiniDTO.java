package com.xantrix.webapp.dtos;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "dettListini")
@Builder
public class ListiniDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -1279216898082445541L;
    private String id;
    @Size(min = 10, max = 30, message = "{Size.Listini.descrizione.Validation}")
    private String descrizione;
    private String obsoleto;
//    @JsonManagedReference
    @Builder.Default
    private Set<DettListiniDTO> dettListini = new HashSet<>();
}
