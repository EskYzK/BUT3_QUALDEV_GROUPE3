package com.iut.banque.modele;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Classe représentant une carte à débit immédiat.
 * Correspond aux lignes de la table 'CarteBancaire' ayant typeDebit='IMMEDIAT'.
 */
@Entity
@DiscriminatorValue("IMMEDIAT")
public class CarteDebitImmediat extends CarteBancaire {

    private static final long serialVersionUID = 1L;

    /**
     * Constructeur vide requis par Hibernate.
     */
    public CarteDebitImmediat() {
        super();
    }

    /**
     * Constructeur complet.
     * * @param numeroCarte : Le numéro à 16 chiffres
     * @param plafond     : Le plafond de dépenses (30 jours glissants)
     * @param compte      : Le compte associé
     */
    public CarteDebitImmediat(String numeroCarte, double plafond, Compte compte) {
        super(numeroCarte, plafond, compte);
    }

    @Override
    public String getTypeDeCarte() {
        return "Débit Immédiat";
    }

    @Override
    public String toString() {
        return "CarteDebitImmediat [" + super.toString() + "]";
    }
}