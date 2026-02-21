package com.iut.banque.modele;

import java.util.Date;

import javax.persistence.*;

/**
 * Classe représentant une opération bancaire (débit, crédit, paiement CB, etc.).
 * * Cette classe permet de tracer l'historique des mouvements sur un compte.
 * Elle est indispensable pour le calcul du plafond glissant des cartes bancaires.
 */
@Entity
@Table(name = "Operation")
public class Operation {

    /**
     * Identifiant unique de l'opération (Auto-incrémenté par la BDD).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idOperation")
    private int idOperation;

    /**
     * Libellé de l'opération (ex: "Achat Supermarché", "Virement reçu").
     */
    @Column(name = "libelle")
    private String libelle;

    /**
     * Carte utilisée pour effectuer l'opération.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "numeroCarte", nullable = true)
    private CarteBancaire carte;

    /**
     * Montant de l'opération.
     * Négatif pour une dépense, Positif pour un dépôt.
     */
    @Column(name = "montant")
    private double montant;

    /**
     * Date et heure de l'opération.
     */
    @Column(name = "dateOperation")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOperation;

    /**
     * Type de l'opération (ex: "CB", "VIREMENT", "DEPOT").
     * Utile pour filtrer les dépenses comptant pour le plafond CB.
     */
    @Column(name = "typeOperation")
    private String typeOperation;

    /**
     * Le compte associé à cette opération.
     */
    @ManyToOne
    @JoinColumn(name = "numeroCompte")
    private Compte compte;

    /**
     * Constructeur vide requis par Hibernate.
     */
    public Operation() {
        super();
    }

    /**
     * Constructeur complet.
     * * @param libelle : Description de l'opération
     * @param montant : Montant de la transaction
     * @param dateOperation : Date de la transaction
     * @param typeOperation : Type (CB, VIREMENT...)
     * @param compte : Le compte impacté
     */
    public Operation(String libelle, double montant, Date dateOperation, String typeOperation, Compte compte,  CarteBancaire carte) {
        super();
        this.libelle = libelle;
        this.montant = montant;
        this.dateOperation = dateOperation;
        this.typeOperation = typeOperation;
        this.compte = compte;
        this.carte = carte;
    }

    // -------------------------
    // Getters et Setters
    // -------------------------

    public int getIdOperation() {
        return idOperation;
    }

    public void setIdOperation(int idOperation) {
        this.idOperation = idOperation;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public Date getDateOperation() {
        return dateOperation;
    }

    public void setDateOperation(Date dateOperation) {
        this.dateOperation = dateOperation;
    }

    public String getTypeOperation() {
        return typeOperation;
    }

    public void setTypeOperation(String typeOperation) {
        this.typeOperation = typeOperation;
    }

    public Compte getCompte() {
        return compte;
    }

    public void setCompte(Compte compte) {
        this.compte = compte;
    }

    public CarteBancaire getCarte() {
        return carte;
    }

    public void setCarte(CarteBancaire carte) {
        this.carte = carte;
    }

    @Override
    public String toString() {
        return "Operation [id=" + idOperation + ", montant=" + montant + ", type=" + typeOperation + ", date=" + dateOperation + "]";
    }
}