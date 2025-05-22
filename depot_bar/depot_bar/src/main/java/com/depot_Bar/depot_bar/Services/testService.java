package com.depot_Bar.depot_bar.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.depot_Bar.depot_bar.Dto.VenteDto;
import com.depot_Bar.depot_bar.Models.Produits;
import com.depot_Bar.depot_bar.Models.Vente;
import com.depot_Bar.depot_bar.Models.VenteItem;
import com.depot_Bar.depot_bar.Repository.ProduitRepository;
import com.depot_Bar.depot_bar.Repository.VenteRepository;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDate;
import java.util.*;

@Service
public class testService {

    @Autowired
    private VenteRepository venteRepo;

    @Autowired
    private ProduitRepository produitRepo;

    @Autowired
    private ProduitService produitService;

    public VenteDto registerVente(Vente vente) {
        vente.setDate(LocalDate.now());

        VenteDto dto = new VenteDto();
        List<String> nomEtCoutTotal = new ArrayList<>();
        double coutTotal = 0.0;
        double prixVenduTotal = 0.0;
        double totalGain = 0.0;
        int totalItem = 0;

        for (VenteItem item : vente.getItems()) {
            Produits prod = produitRepo.findById(item.getProduit().getId())
                .orElseThrow(() -> new EntityNotFoundException("Produit non trouvé"));

            double itemTotal = item.getQuantite() * item.getPrixVendu();
            double itemGain = (item.getPrixVendu() - prod.getUPrice()) * item.getQuantite();

            nomEtCoutTotal.add("Code Prod: " + prod.getId() + 
            ", Nom Prod: " + prod.getNom() + 
            ", Quantite Produit: " + item.getQuantite() +
            ", Prix unitaire: "+ prod.getUPrice() +
            ", Prix vendu(Pv): " + item.getPrixVendu() + 
            ", Total(Qte * Pv): " + itemTotal);

            coutTotal += itemTotal;
            prixVenduTotal += itemTotal;
            totalGain += itemGain;
            totalItem += item.getQuantite();

            produitService.retirerQuantite(prod.getId(), item.getQuantite());

            item.setPrix(itemTotal);
            item.setGain(itemGain);
            item.setVente(vente);
        }

        Vente saved = venteRepo.save(vente);

        dto.setId(saved.getId());
        dto.setDate(saved.getDate());
        dto.setCoutTotal(coutTotal);
        dto.setPrixVendu(prixVenduTotal);
        dto.setGain(totalGain);
        dto.setTotalItem(totalItem);
        dto.setNomProdEtPrixT(nomEtCoutTotal);
        dto.setMessage("Vente enregistrée avec succès");

        return dto;
    }
}

