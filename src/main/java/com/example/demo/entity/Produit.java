package com.example.demo.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@AllArgsConstructor
@Table (name = "produits")
public class Produit {
    public Produit(){
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProduit;
    private String nom;
    private double prix;
    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn(name = "pointDeVenteId")
    @JsonBackReference
    private PointDeVente pointDeVente;
    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn(name = "idDistributeur")
    @JsonBackReference
    private Distributeur distributeurs;

    @ManyToMany
    @JsonIgnoreProperties("commandes")
    @JoinTable(
            name = "produitCommande",
            joinColumns = @JoinColumn(name = "produitId"),
            inverseJoinColumns = @JoinColumn(name = "commandeId")
    )
    private List<Commande> commandes;

    public Integer getIdProduit() {
        return idProduit;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public PointDeVente getPointDeVente() {
        return pointDeVente;
    }

    public void setPointDeVente(PointDeVente pointDeVente) {
        this.pointDeVente = pointDeVente;
    }

    public Distributeur getDistributeurs() {
        return distributeurs;
    }

    public void setDistributeurs(Distributeur distributeurs) {
        this.distributeurs = distributeurs;
    }



    @Override
    public String toString() {
        return "Produit{" +
                "nom='" + nom + '\'' +
                ", prix=" + prix +
                '}';
    }
}
