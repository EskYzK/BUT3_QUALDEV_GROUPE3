package com.iut.banque.modele;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Classe abstraite représentant une carte bancaire.
 * * Stratégie d'héritage : SINGLE_TABLE (comme pour la classe Compte).
 * La distinction entre débit immédiat et différé se fait via la colonne "typeDebit".
 */
@Entity
@Table(name = "CarteBancaire")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "typeDebit", discriminatorType = DiscriminatorType.STRING)
public abstract class CarteBancaire implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "numeroCarte")
    private String numeroCarte;

    @Column(name = "plafond")
    private double plafond;

    /**
     * 0 = Active, 1 = Bloquée temporairement.
     */
    @Column(name = "bloquee")
    private boolean bloquee;

    /**
     * 0 = Existante, 1 = Supprimée (Blocage définitif).
     */
    @Column(name = "supprimee")
    private boolean supprimee;

    /**
     * Le compte auquel la carte est rattachée.
     */
    @ManyToOne
    @JoinColumn(name = "numeroCompte")
    private Compte compte;

    /**
     * Constructeur vide requis par Hibernate.
     */
    public CarteBancaire() {
        super();
    }

    /**
     * Constructeur complet.
     * * @param numeroCarte : Le numéro à 16 chiffres
     * @param plafond : Le plafond de dépenses (30 jours glissants)
     * @param compte : Le compte associé
     */
    protected CarteBancaire(String numeroCarte, double plafond, Compte compte) {
        this.numeroCarte = numeroCarte;
        this.plafond = plafond;
        this.compte = compte;
        this.bloquee = false;
        this.supprimee = false;
    }

    // -------------------------
    // Getters et Setters
    // -------------------------

    public String getNumeroCarte() {
        return numeroCarte;
    }

    public void setNumeroCarte(String numeroCarte) {
        this.numeroCarte = numeroCarte;
    }

    public double getPlafond() {
        return plafond;
    }

    public void setPlafond(double plafond) {
        this.plafond = plafond;
    }

    public boolean isBloquee() {
        return bloquee;
    }

    public void setBloquee(boolean bloquee) {
        this.bloquee = bloquee;
    }

    public boolean isSupprimee() {
        return supprimee;
    }

    public void setSupprimee(boolean supprimee) {
        this.supprimee = supprimee;
    }

    public Compte getCompte() {
        return compte;
    }

    public void setCompte(Compte compte) {
        this.compte = compte;
    }

    // Méthode abstraite pour l'affichage dans la JSP
    public abstract String getTypeDeCarte();

    @Override
    public String toString() {
        return "CarteBancaire [numero=" + numeroCarte + ", plafond=" + plafond + ", bloquee=" + bloquee + "]";
    }
}