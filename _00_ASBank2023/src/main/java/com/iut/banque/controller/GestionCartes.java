package com.iut.banque.controller;

import com.iut.banque.exceptions.InsufficientFundsException;
import com.opensymphony.xwork2.ActionSupport;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.CarteBancaire;
import java.util.Map;
import org.slf4j.Logger;         // Import SLF4J
import org.slf4j.LoggerFactory;  // Import SLF4J
import com.iut.banque.modele.Compte;

public class GestionCartes extends ActionSupport {

    private static final String TECHNICAL_ERROR = "TECHNICAL";
    private static final Logger logger = LoggerFactory.getLogger(GestionCartes.class);
    private static final long serialVersionUID = 1L;
    private transient BanqueFacade banqueFacade;

    // --- Paramètres d'entrée (Envoyés par les formulaires JSP) ---
    private String numeroCompte;
    private String numeroCarte;
    private String typeDebit; // "IMMEDIAT" ou "DIFFERE"
    private double plafond;
    private boolean definitif; // true = suppression, false = blocage temporaire

    // --- Attributs pour le paiement ---
    private double montantPaiement;
    private String libellePaiement;

    // --- Pour changer le compte lié ---
    private String nouveauNumeroCompte;

    // --- Paramètres de sortie (Pour l'affichage) ---
    private String message;
    private CarteBancaire carte; // Utilisé pour préremplir le formulaire de modification

    // --- La liste des comptes pour le menu déroulant ---
    private Map<String, Compte> comptesClient;

    /**
     * Constructeur : On connecte la façade manuellement (comme dans DetailCompte)
     */
    public GestionCartes() {
        super();
    }

    // Un Setter pour permettre aux tests d'injecter un Mock
    public void setBanqueFacade(BanqueFacade banqueFacade) {
        this.banqueFacade = banqueFacade;
    }
    // -------------------------------------------------------------------
    // ACTION 1 : Création de carte
    // -------------------------------------------------------------------

    /**
     * Affiche le formulaire de création (Appelé par le bouton [+] Ajouter une carte).
     */
    public String formulaireCreation() {
        if (numeroCompte == null || numeroCompte.isEmpty()) {
            message = "MISSING_ACCOUNT";
            return ERROR;
        }
        return SUCCESS;
    }

    /**
     * Exécute la création de la carte (Appelé par le submit du formulaire).
     */
    public String creerCarte() {
        logger.debug("Tentative de création de carte pour le compte {}", numeroCompte);
        try {
            if (plafond <= 0) {
                logger.warn("Tentative de création avec plafond invalide : {}", plafond);
                addFieldError("plafond", "Le plafond doit être strictement positif.");
                return INPUT;
            }

            // Appel sécurisé via la Facade (qui vérifie si c'est un Manager)
            banqueFacade.createCarte(numeroCompte, plafond, typeDebit);

            logger.info("Carte créée avec succès pour le compte {}", numeroCompte);
            message = "Carte créée avec succès.";
            return SUCCESS; // Redirigera vers le détail du compte

        } catch (Exception e) {
            logger.error("Erreur technique lors de la création de la carte", e);
            message = TECHNICAL_ERROR;
            return ERROR;
        }
    }

    // -------------------------------------------------------------------
    // ACTION 2 : Blocage / Déblocage / Suppression
    // -------------------------------------------------------------------

    /**
     * Bloque ou Supprime une carte.
     * Utilisé par Client (blocage simple) et Manager (blocage ou suppression).
     */
    public String bloquerCarte() {
        try {
            // Appel via la Facade
            banqueFacade.bloquerCarte(numeroCarte, definitif);

            if (definitif) {
                message = "Carte supprimée définitivement.";
            } else {
                message = "Carte bloquée temporairement.";
            }
            return SUCCESS;
        } catch (Exception e) {
            message = TECHNICAL_ERROR;
            return ERROR;
        }
    }

    /**
     * Débloque une carte (Manager uniquement).
     */
    public String debloquerCarte() {
        try {
            banqueFacade.debloquerCarte(numeroCarte);
            message = "Carte réactivée avec succès.";
            return SUCCESS;
        } catch (Exception e) {
            message = TECHNICAL_ERROR;
            return ERROR;
        }
    }

    // -------------------------------------------------------------------
    // ACTION 3 : Modification (Plafond uniquement)
    // -------------------------------------------------------------------

    /**
     * Charge les infos de la carte pour afficher le formulaire de modification.
     * (Appelé par le lien "Modifier" dans le tableau).
     */
    public String formulaireModification() {
        try {
            // 1. On charge la carte
            this.carte = banqueFacade.getCarte(numeroCarte);
            if (this.carte == null) {
                message = "CARD_NOT_FOUND";
                return ERROR;
            }

            // 2. On charge la liste des comptes du client propriétaire de la carte
            // Cela servira à remplir le <s:select> dans la JSP
            if (carte.getCompte() != null && carte.getCompte().getOwner() != null) {
                // On récupère tous les comptes de ce client
                this.comptesClient = carte.getCompte().getOwner().getAccounts();
            }

            return SUCCESS;
        } catch (Exception e) {
            message = TECHNICAL_ERROR;
            return ERROR;
        }
    }

    /**
     * Enregistre la modification du plafond.
     */
    public String modifierCarte() {
        try {
            if (plafond <= 0) {
                addFieldError("plafond", "Le plafond doit être positif.");
                return INPUT;
            }

            // 1. Modification du Plafond (Autorisé pour tous)
            banqueFacade.changerPlafondCarte(numeroCarte, plafond);

            // 2. Modification du Compte lié
            if (nouveauNumeroCompte != null && !nouveauNumeroCompte.isEmpty()) {
                // On appelle la façade. Elle contient déjà la sécurité qui interdira
                // le changement si c'est une carte à débit différé.
                // On compare avec l'ancien pour ne pas faire d'appel inutile
                CarteBancaire c = banqueFacade.getCarte(numeroCarte);
                if (!c.getCompte().getNumeroCompte().equals(nouveauNumeroCompte)) {
                    banqueFacade.changerCompteLieCarte(numeroCarte, nouveauNumeroCompte);
                }
            }

            // On recharge la carte pour que Struts puisse lire "carte.compte.numeroCompte"
            // dans le struts.xml lors de la redirection.
            this.carte = banqueFacade.getCarte(numeroCarte);

            message = "Carte modifiée avec succès.";
            return SUCCESS;
        } catch (Exception e) {
            message = TECHNICAL_ERROR;
            return ERROR;
        }
    }

    // -------------------------------------------------------------------
    // ACTION 4 : Simuler un Paiement
    // -------------------------------------------------------------------
    public String payer() {
        try {
            if (montantPaiement <= 0) {
                message = "NEGATIVEAMOUNT";
                return ERROR; // Ou retour à la vue précédente
            }

            // On fixe un libellé par défaut si vide
            if (libellePaiement == null || libellePaiement.isEmpty()) {
                libellePaiement = "Paiement CB Internet";
            }

            banqueFacade.payerParCarte(numeroCarte, montantPaiement, libellePaiement);

            message = "Paiement de " + montantPaiement + "€ accepté !";
            return SUCCESS;

        } catch (InsufficientFundsException e) {
            // L'erreur métier : Plafond dépassé ou solde insuffisant
            message = "OVER_LIMIT";
            return ERROR;
        } catch (Exception e) {
            // Toute autre erreur inattendue
            message = TECHNICAL_ERROR;
            return ERROR;
        }
    }

    // -------------------------------------------------------------------
    // Getters et Setters (Indispensables pour Struts)
    // -------------------------------------------------------------------
    public String getNumeroCompte() { return numeroCompte; }
    public void setNumeroCompte(String numeroCompte) { this.numeroCompte = numeroCompte; }

    public String getNumeroCarte() { return numeroCarte; }
    public void setNumeroCarte(String numeroCarte) { this.numeroCarte = numeroCarte; }

    public String getTypeDebit() { return typeDebit; }
    public void setTypeDebit(String typeDebit) { this.typeDebit = typeDebit; }

    public double getPlafond() { return plafond; }
    public void setPlafond(double plafond) { this.plafond = plafond; }

    public boolean isDefinitif() { return definitif; }
    public void setDefinitif(boolean definitif) { this.definitif = definitif; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public CarteBancaire getCarte() { return carte; }
    public void setCarte(CarteBancaire carte) { this.carte = carte; }

    public String getNouveauNumeroCompte() { return nouveauNumeroCompte; }
    public void setNouveauNumeroCompte(String nouveauNumeroCompte) { this.nouveauNumeroCompte = nouveauNumeroCompte; }

    public Map<String, Compte> getComptesClient() { return comptesClient; }
    public void setComptesClient(Map<String, Compte> comptesClient) { this.comptesClient = comptesClient; }

    public double getMontantPaiement() { return montantPaiement; }
    public void setMontantPaiement(double montantPaiement) { this.montantPaiement = montantPaiement; }

    public String getLibellePaiement() { return libellePaiement; }
    public void setLibellePaiement(String libellePaiement) { this.libellePaiement = libellePaiement; }
}