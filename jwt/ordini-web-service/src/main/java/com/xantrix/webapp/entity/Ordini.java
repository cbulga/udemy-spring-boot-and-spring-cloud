package com.xantrix.webapp.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "ordini")
@Getter
@Setter
public class Ordini implements Serializable {

    @Serial
    private static final long serialVersionUID = -4993364475755374640L;
    @Id
    @NotNull(message = "{NotNull.Ordini.id.Validation}")
    private String id;
    //@Temporal(TemporalType.DATE)
    private LocalDate data;
    private int idpdv;
    private String codfid;
    private int stato;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "ordine", orphanRemoval = true)
    @OrderBy("id desc")
    @JsonManagedReference
    @Valid
    private List<DettOrdini> dettOrdine;
}
