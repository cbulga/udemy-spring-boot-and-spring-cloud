package com.xantrix.webapp.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Document(collection = "utenti")
@Data
public class Utenti {

    @Id
    private String id;

    @Indexed(unique = true)
    @Size(min = 5, max = 80, message = "{Size.Utenti.userId.Validation}")
    @NotNull(message = "{NotNull.Articoli.userId.Validation}")
    private String userId;

    @Size(min = 8, max = 80, message = "{Size.Utenti.password.Validation}")
    private String password;

    private String attivo;

    private List<String> ruoli;
}
