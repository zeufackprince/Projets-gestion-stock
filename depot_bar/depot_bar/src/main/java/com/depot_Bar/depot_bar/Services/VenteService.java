package com.depot_Bar.depot_bar.Services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.depot_Bar.depot_bar.Dto.VenteDto;
import com.depot_Bar.depot_bar.Models.Produits;
import com.depot_Bar.depot_bar.Models.Vente;
import com.depot_Bar.depot_bar.Models.VenteItem;
import com.depot_Bar.depot_bar.Repository.ProduitRepository;
import com.depot_Bar.depot_bar.Repository.VenteRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VenteService {

    private final ProduitService produitService;

    private final ProduitRepository produitRepository;

    private final VenteRepository venteRepo;

    public Vente enregistrerVente(Vente vente) {
        vente.setDate(LocalDate.now());
        
        for (VenteItem item : vente.getItems()) {
            produitService.retirerQuantite(item.getProduit().getId(), item.getQuantite());
            item.setVente(vente);
        }
        return venteRepo.save(vente);
    }

    public VenteDto newVente(Vente vente) {

        VenteDto res = new VenteDto();

        try {
            
            vente.setDate(LocalDate.now());

            for (VenteItem item : vente.getItems()) {
                produitService.retirerQuantite(item.getProduit().getId(), item.getQuantite());
                item.setVente(vente);
            }

            

            List<String> nomEtCoutTotal = new ArrayList<>();

            double Tsum = 0.0;  Integer num = 0; double gain = 0.0;

            for(VenteItem item: vente.getItems()) {

                Optional<Produits> dbProd = this.produitRepository.findById(item.getProduit().getId());

                if(!dbProd.isPresent()) {

                    res.setMessage("Le Produit nes pas en BD, Veuillez reverifier votre requet!!!");
                    return res;
                }

                nomEtCoutTotal.add(dbProd.get().getNom() + " "+ dbProd.get().getUPrice()+ " " + item.getPrixVendu() + item.getQuantite() * item.getPrixVendu());

                num += item.getQuantite();

                Tsum += item.getQuantite() * item.getPrixVendu();

                gain = item.getPrixVendu() - dbProd.get().getUPrice();
                
                item.setPrix(Tsum);
                item.setGain(gain);
            }

           
            res.setGain(gain);
            res.setDate(vente.getDate());
            res.setCoutTotal(Tsum);
            res.setTotalItem(num);
            res.setNomProdEtPrixT(nomEtCoutTotal);
            res.setMessage("Vente Enregistrer avec succes");

            Vente dbData = venteRepo.save(vente);

             res.setId(dbData.getId());

        } catch (Exception e) {
            
            res.setMessage("Erreur lors du traitement de la requete " + e);
            
        }
        
        return res;

    }

    public VenteDto registerVente(Vente vente) {

        vente.setDate(LocalDate.now());

        VenteDto res = new VenteDto();

        List<String> nomEtCoutTotal = new ArrayList<>();

        double Tsum = 0.0;  Integer num = 0;

        for (VenteItem item: vente.getItems()) {

            Optional<Produits> dbProd = this.produitRepository.findById(item.getProduit().getId());

            if(!dbProd.isPresent()){

                throw new EntityNotFoundException("No Entity found");
                
            }else{

                nomEtCoutTotal.add(dbProd.get().getNom() + " " + item.getQuantite() * dbProd.get().getUPrice());

                num += item.getQuantite();

                Tsum += item.getQuantite() * dbProd.get().getUPrice();
            }

            produitService.retirerQuantite(item.getProduit().getId(), item.getQuantite());
            item.setPrix(Tsum);
            item.setVente(vente);
        }

        res.setDate(vente.getDate());
        res.setCoutTotal(Tsum);
        res.setTotalItem(num);
        res.setNomProdEtPrixT(nomEtCoutTotal);

        Vente db = venteRepo.save(vente);

        res.setId(db.getId());



        return res;
        
    }

    public List<VenteDto> getAllVente(){

        List<Vente> allVente = this.venteRepo.findAll();

        List<VenteDto> resList = new ArrayList<>();

        try {
            
            if(allVente.size() <= 0){
                throw new EntityNotFoundException("No data found in DB!!");
            }

            for(Vente vente: allVente){

                VenteDto data = new VenteDto();
                List<String> nomEtCoutTotal = new ArrayList<>();
                double coutTotal = 0.0;  
                Integer totalItem = 0;
                double prixVenduTotal = 0.0;
                double totalGain = 0.0;

                for(VenteItem item: vente.getItems()){

                    Optional<Produits> dbProd = this.produitRepository.findById(item.getProduit().getId());

                    if(!dbProd.isPresent()){

                        throw new EntityNotFoundException("No Entity found");
                
                    }else{

                        double itemTotal = item.getQuantite() * item.getPrixVendu();
                        double itemGain = (item.getPrixVendu() - dbProd.get().getUPrice()) * item.getQuantite();

                        nomEtCoutTotal.add(
                            "CodeProduit: " + dbProd.get().getId() + 
                            ", Nom produit: " + dbProd.get().getNom() + 
                            ", Qte produit: " + item.getQuantite() +
                            ", Prix unitaire: " + dbProd.get().getUPrice() +
                            ", Prix vendu: " + item.getPrixVendu() +
                            ", Total: " + item.getQuantite() * item.getPrixVendu()
                     );

                        coutTotal += itemTotal;
                        prixVenduTotal += itemTotal;
                        totalGain += itemGain;
                        totalItem += item.getQuantite();
                    }

                }

                data.setId(vente.getId());
                data.setPrixVendu(prixVenduTotal);
                data.setDate(vente.getDate());
                data.setCoutTotal(coutTotal);
                data.setTotalItem(totalItem);
                data.setGain(totalGain);
                data.setNomProdEtPrixT(nomEtCoutTotal);
                data.setMessage("Data fetched with success");

                resList.add(data);
            }

        } catch (Exception e) {
            VenteDto data = new VenteDto();

            data.setMessage("Error while fetching data in DB " + e);
            resList.add(data);
        }

        return resList;
    }

    public VenteDto getVenteById(Long id) {
        Vente vente = venteRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Vente with ID " + id + " not found"));
    
        return mapVenteToDto(vente);
    }
    

    public List<VenteDto> getVentesByDate(LocalDate date) {
        List<Vente> ventes = venteRepo.findByDate(date); // You need to implement this in the repository

        if (ventes.isEmpty()) {
            throw new EntityNotFoundException("No ventes found for date " + date);
        }

        return ventes.stream()
                .map(this::mapVenteToDto)
                .collect(Collectors.toList());
    }

    private VenteDto mapVenteToDto(Vente vente) {
        VenteDto dto = new VenteDto();
        dto.setId(vente.getId());
        dto.setDate(vente.getDate());

        List<String> nomEtCoutTotal = new ArrayList<>();
        double Tsum = 0.0;
        int totalItems = 0;

        for (VenteItem item : vente.getItems()) {
            Produits prod = produitRepository.findById(item.getProduit().getId())
            .orElseThrow(() -> new EntityNotFoundException("Produit not found with ID: " + item.getProduit().getId()));

            nomEtCoutTotal.add(
                "CodeProduit: " + prod.getId() +
                ", Nom produit: " + prod.getNom() +
                ", Qte produit: " + item.getQuantite() +
                ", Prix unitaire: " + prod.getUPrice() +
                ", Total: " + (item.getQuantite() * prod.getUPrice())
            );

            Tsum += item.getQuantite() * prod.getUPrice();
            totalItems += item.getQuantite();
        }

        dto.setCoutTotal(Tsum);
        dto.setTotalItem(totalItems);
        dto.setNomProdEtPrixT(nomEtCoutTotal);

        return dto;
    }



    
}
