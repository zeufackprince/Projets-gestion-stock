package com.depot_Bar.depot_bar.Models;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table
public class Produits {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name = "nom", nullable = false, unique = true)
    private String nom;

    @Column(name = "Quantite", nullable = false)
    private int Qte;

    @Column(name = "Prix-unitaire", nullable = true)
    private double UPrice;

    @ManyToOne
    private Vente vente;
    
}
