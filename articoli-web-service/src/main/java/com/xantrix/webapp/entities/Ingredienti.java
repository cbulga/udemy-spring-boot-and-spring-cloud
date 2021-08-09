package com.xantrix.webapp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "INGREDIENTI")
@Data
@ToString(exclude = {"articolo"})
public class Ingredienti implements Serializable {
    private static final long serialVersionUID = -6230810713799336046L;

    @Id
    @Column(name = "CODART")
    private String codArt;

    @Column(name = "INFO")
    private String info;

    @OneToOne
    @PrimaryKeyJoinColumn
    @JsonIgnore //alternativo al @JsonBackReference
    //@JsonBackReference
    private Articoli articolo;

}
