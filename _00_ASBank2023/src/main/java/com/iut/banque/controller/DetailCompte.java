package com.iut.banque.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.iut.banque.modele.*;

import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.InsufficientFundsException;
import com.iut.banque.facade.BanqueFacade;
import com.opensymphony.xwork2.ActionSupport;
import org.slf4j.Logger;         // Import SLF4J
import org.slf4j.LoggerFactory;  // Import SLF4J

public class DetailCompte extends ActionSupport {

    private static final String NEGATIVEAMOUNT_ERROR = "NEGATIVEAMOUNT";
    private static final Logger logger = LoggerFactory.getLogger(DetailCompte.class);
	private static final long serialVersionUID = 1L;
	protected transient BanqueFacade banque;
	private String montant;
	private String error;
    private String message;
	protected Compte compte;

	/**
	 * Constructeur du controller DetailCompte
	 * Récupère l'ApplicationContext
	 *
     */
	public DetailCompte() {
        logger.debug("Tentative de création de l'objet DetailCompte {}", this);
	}

    // Un Setter pour permettre aux tests d'injecter un Mock
    public void setBanqueFacade(BanqueFacade banqueFacade) {
        this.banque = banqueFacade;
    }

	/**
	 * Retourne sous forme de string le message d'erreur basé sur le champ
	 * "error" actuellement défini dans la classe
	 * 
	 * @return String, le string avec le détail du message d'erreur
	 */
	public String getError() {
        // On évite le NullPointerException
        if (error == null || error.equals("EMPTY") || error.isEmpty()) {
            return "";
        }

		switch (error) {
		case "TECHNICAL":
			return "Erreur interne. Vérifiez votre saisie puis réessayez. Contactez votre conseiller si le problème persiste.";
		case "BUSINESS":
			return "Fonds insuffisants.";
        case "OVER_LIMIT":
            return "Paiement refusé : Le plafond de la carte (sur 30 jours) ou le solde du compte est dépassé.";
        case "MISSING_ACCOUNT":
            return "Numéro de compte manquant.";
        case "CARD_NOT_FOUND":
            return "Carte introuvable.";
		case NEGATIVEAMOUNT_ERROR:
			return "Veuillez rentrer un montant positif.";
		case "NEGATIVEOVERDRAFT":
			return "Veuillez rentrer un découvert positif.";
		case "INCOMPATIBLEOVERDRAFT":
			return "Le nouveau découvert est incompatible avec le solde actuel.";
		default:
			return "Erreur inconnue";
		}
	}

	/**
	 * Permet de définir le champ error de la classe avec le string passé en
	 * paramètre. Si jamais on passe un objet null, on adapte le string
	 * automatiquement en "EMPTY".
	 * 
	 * @param error
	 *            : Un String correspondant à celui qu'on veut définir dans le
	 *            champ error
	 */
	public void setError(String error) {
		if (error == null) {
			this.error = "EMPTY";
		} else {
			this.error = error;
		}
	}

	/**
	 * Getter du champ montant
	 * 
	 * @return String : valeur du champ montant
	 */
	public String getMontant() {
		return montant;
	}

	/**
	 * Setter du champ montant
	 * 
	 * @param montant
	 *            un String correspondant au montant à définir
	 */
	public void setMontant(String montant) {
		this.montant = montant;
	}


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

	/**
	 * Getter du compte actuellement sélectionné. Récupère la liste des comptes
	 * de l'utilisateur connecté dans un premier temps. Récupère ensuite dans la
	 * HashMap la clé qui comporte le string provenant d'idCompte. Renvoie donc
	 * null si le compte n'appartient pas à l'utilisateur
	 * 
	 * @return Compte : l'objet compte après s'être assuré qu'il appartient à
	 *         l'utilisateur
	 */
	public Compte getCompte() {
        Utilisateur user = banque.getConnectedUser();

        if (user instanceof Gestionnaire ||
                (user instanceof Client && ((Client) user).getAccounts().containsKey(compte.getNumeroCompte()))) {
            return compte;
        }
		return null;
	}

	public void setCompte(Compte compte) {
		this.compte = compte;
	}

	/**
	 * Setter pour injection de la BanqueFacade (utilisé en test)
	 * 
	 * @param banque la BanqueFacade à injecter
	 */
	public void setBanque(BanqueFacade banque) {
		this.banque = banque;
	}

	/**
	 * Méthode débit pour débiter le compte considéré en cours
	 * 
	 * @return String : Message correspondant à l'état du débit (s'il a réussi
	 *         ou pas)
	 */
	public String debit() {
		Compte compte = getCompte();
		try {
			banque.debiter(compte, Double.parseDouble(montant.trim()));
            logger.info("Débit effectué avec succès");
			return "SUCCESS";
		} catch (NumberFormatException e) {
            logger.error("Erreur dans le format du nombre", e);
			return "ERROR";
		} catch (InsufficientFundsException ife) {
            logger.error("Erreur pour cause de fonds insuffisants", ife);
			return "NOTENOUGHFUNDS";
		} catch (IllegalFormatException e) {
            logger.error("Erreur de format", e);
			return NEGATIVEAMOUNT_ERROR;
		}
	}

	/**
	 * Méthode crédit pour créditer le compte considéré en cours
	 * 
	 * @return String : Message correspondant à l'état du crédit (s'il a réussi
	 *         ou pas)
	 */
	public String credit() {
		Compte compte = getCompte();
		try {
			banque.crediter(compte, Double.parseDouble(montant.trim()));
            logger.info("Crédit effectué avec succès");
			return "SUCCESS";
		} catch (NumberFormatException nfe) {
            logger.error("Erreur dans le format du nombre", nfe);
			return "ERROR";
		} catch (IllegalFormatException e) {
			return NEGATIVEAMOUNT_ERROR;
		}
	}

    /**
     * Récupère les cartes du compte et les trie par numéro croissant.
     * Utilise les imports pour un code lisible.
     */
    public List<CarteBancaire> getCartesTriees() {
        Compte c = getCompte();

        // Sécurité si le compte ou la liste est null
        if (c == null || c.getCartes() == null) {
            return new ArrayList<>();
        }

        List<CarteBancaire> listeCartes;
        Object rawCartes = c.getCartes();

        // Conversion propre selon qu'Hibernate renvoie une Map ou une Collection
        if (rawCartes instanceof Map) {
            listeCartes = new ArrayList<>(((Map<?, CarteBancaire>) rawCartes).values());
        } else {
            listeCartes = new ArrayList<>((Collection<CarteBancaire>) rawCartes);
        }

        // Tri propre avec Comparator
        Collections.sort(listeCartes, new Comparator<CarteBancaire>() {
            @Override
            public int compare(CarteBancaire c1, CarteBancaire c2) {
                return c1.getNumeroCarte().compareTo(c2.getNumeroCarte());
            }
        });

        return listeCartes;
    }
}
