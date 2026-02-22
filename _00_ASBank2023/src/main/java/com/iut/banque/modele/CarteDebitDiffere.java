package com.iut.banque.modele;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Classe représentant une carte à débit différé.
 * Correspond aux lignes de la table 'CarteBancaire' ayant typeDebit='DIFFERE'.
 */
@Entity
@DiscriminatorValue("DIFFERE")
public class CarteDebitDiffere extends CarteBancaire {

    private static final long serialVersionUID = 1L;

    /**
     * Constructeur vide requis par Hibernate.
     */
    public CarteDebitDiffere() {
        super();
    }

    /**
     * Constructeur complet.
     * * @param numeroCarte : Le numéro à 16 chiffres
     * @param plafond     : Le plafond de dépenses (30 jours glissants)
     * @param compte      : Le compte associé
     */
    public CarteDebitDiffere(String numeroCarte, double plafond, Compte compte) {
        super(numeroCarte, plafond, compte);
    }

    @Override
    public String getTypeDeCarte() {
        return "Débit Différé";
    }

    @Override
    public String toString() {
        return "CarteDebitDiffere [" + super.toString() + "]";
    }
}