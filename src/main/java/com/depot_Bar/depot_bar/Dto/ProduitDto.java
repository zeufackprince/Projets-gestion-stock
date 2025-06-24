package com.depot_Bar.depot_bar.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProduitDto {

    private Long Id;

    private String nom;

    private int Qte;

    private String message;

    private Integer code;

    private double UPrix;

    private Long venteId;
    
}
