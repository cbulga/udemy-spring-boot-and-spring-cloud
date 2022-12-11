package com.xantrix.webapp.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "dettordini")
@Getter
@Setter
public class DettOrdini implements Serializable {

    @Serial
    private static final long serialVersionUID = -8158271569844774997L;
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "codart")
    @NotBlank(message = "{NotBlank.DettOrdini.codart.Validation}")
    private String codArt;

    @Min(value = (long) 0.01, message = "{Min.DettOrdini.qta.Validation}")
    private double qta;

    private double prezzo;

    @ManyToOne
    @JoinColumn(name = "IDORDINE", referencedColumnName = "id")
    @JsonBackReference
    private Ordini ordine;
}
