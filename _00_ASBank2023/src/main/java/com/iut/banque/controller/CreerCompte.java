package com.iut.banque.controller;

import org.slf4j.Logger;         // Import SLF4J
import org.slf4j.LoggerFactory;  // Import SLF4J

import com.opensymphony.xwork2.ActionSupport;

import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.exceptions.TechnicalException;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;

public class CreerCompte extends ActionSupport {

    private static final Logger logger = LoggerFactory.getLogger(CreerCompte.class);
	private static final long serialVersionUID = 1L;
	private String numeroCompte;
	private boolean avecDecouvert;
	private double decouvertAutorise;
	private Client client;
	private String message;
	private boolean error;
	private boolean result;
	private transient BanqueFacade banqueFacade;
	private Compte compte;

	/**
	 * @param compte
	 *            the compte to set
	 */
	public void setCompte(Compte compte) {
		this.compte = compte;
	}

	/**
	 * @return the compte
	 */
	public Compte getCompte() {
		return compte;
	}

	/**
	 * @return the client
	 */
	public Client getClient() {
		return client;
	}

	/**
	 * Indique si le résultat de l'action précédente avait réussi
	 * 
	 * @return le status de l'action précédente
	 */
	public boolean isError() {
		return error;
	}

	/**
	 * Setter de l'action précédente
	 *
     */
	public void setError(boolean error) {
		this.error = error;
	}

	/**
	 * @param client
	 *            the client to set
	 */
	public void setClient(Client client) {
		this.client = client;
	}

	/**
	 * Constructeur sans paramètre de CreerCompte
	 */
	public CreerCompte() {
        logger.debug("Instanciation du contrôleur CreerCompte");
	}

    // Le setter indispensable pour l'injection
    public void setBanqueFacade(BanqueFacade banqueFacade) {
        this.banqueFacade = banqueFacade;
    }

	/**
	 * @return the numeroCompte
	 */
	public String getNumeroCompte() {
		return numeroCompte;
	}

	/**
	 * @param numeroCompte
	 *            the numeroCompte to set
	 */
	public void setNumeroCompte(String numeroCompte) {
		this.numeroCompte = numeroCompte;
	}

	/**
	 * @return the avecDecouvert
	 */
	public boolean isAvecDecouvert() {
		return avecDecouvert;
	}

	/**
	 * @param avecDecouvert
	 *            the avecDecouvert to set
	 */
	public void setAvecDecouvert(boolean avecDecouvert) {
		this.avecDecouvert = avecDecouvert;
	}

	/**
	 * @return the decouvertAutorise
	 */
	public double getDecouvertAutorise() {
		return decouvertAutorise;
	}

	/**
	 * @param decouvertAutorise
	 *            the decouvertAutorise to set
	 */
	public void setDecouvertAutorise(double decouvertAutorise) {
		this.decouvertAutorise = decouvertAutorise;
	}

	/**
	 * Getter du message résultant de l'action précédente.
	 * 
	 * @return le message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Choisi le message à enregistrer en fonction du message reçu en paramètre.
	 * 
	 * @param message
	 *            : le message indiquant le status de l'action précédente.
	 */
	public void setMessage(String message) {
		switch (message) {
		case "NONUNIQUEID":
			this.message = "Ce numéro de compte existe déjà !";
			break;
		case "INVALIDFORMAT":
			this.message = "Ce numéro de compte n'est pas dans un format valide !";
			break;
		case "SUCCESS":
            if (compte != null) {
                this.message = "Le compte " + compte.getNumeroCompte() + " a bien été créé.";
            } else {
                this.message = "Compte créé.";
            }
			break;
        default:
            this.message = message;
            break;
		}
	}

	/**
	 * Getter du status de l'action précédente. Si vrai, indique qu'une création
	 * de compte a déjà été essayée (elle peut avoir réussi ou non). Sinon, le
	 * client vient d'arriver sur la page.
	 * 
	 * @return le status de l'action précédente
	 */
	public boolean isResult() {
		return result;
	}

	/**
	 * Setter du status de l'action précédente.
	 * 
	 * @param result
	 *            : le status
	 */
	public void setResult(boolean result) {
		this.result = result;
	}

	/**
	 * Action créant un compte client ou gestionnaire.
	 * 
	 * @return une chaine déterminant le résultat de l'action
	 */
	public String creationCompte() {
        logger.info("Tentative de création du compte {} (Découvert: {})", numeroCompte, avecDecouvert);
		try {
			if (avecDecouvert) {
                banqueFacade.createAccount(numeroCompte, client, decouvertAutorise);
			} else {
				banqueFacade.createAccount(numeroCompte, client);
			}
            logger.info("Compte {} créé avec succès.", numeroCompte);
			this.compte = banqueFacade.getCompte(numeroCompte);
			return "SUCCESS";
		} catch (TechnicalException e) {
            logger.warn("Échec création : Le numéro de compte {} existe déjà.", numeroCompte);
			return "NONUNIQUEID";
		} catch (IllegalFormatException e) {
            logger.warn("Échec création : Format invalide pour le compte {}.", numeroCompte);
			return "INVALIDFORMAT";
		}catch (IllegalOperationException e) {
            // 4. Gestion de l'exception qui était "avalée" auparavant
            logger.error("Erreur métier lors de la création du compte avec découvert.", e);
            this.message = "Erreur opération : " + e.getMessage();
            return ERROR; // Retourne vers la page d'erreur standard de Struts

        } catch (Exception e) {
            // Sécurité globale pour tout autre crash
            logger.error("Erreur système inattendue.", e);
            return ERROR;
        }

	}
}
