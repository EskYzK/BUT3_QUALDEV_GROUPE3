package com.iut.banque.controller;

import java.util.Map;

import com.opensymphony.xwork2.ActionSupport;

import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.exceptions.TechnicalException;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;
import org.slf4j.Logger;         // Import SLF4J
import org.slf4j.LoggerFactory;  // Import SLF4J

public class ListeCompteManager extends ActionSupport {

    private static final Logger logger = LoggerFactory.getLogger(ListeCompteManager.class);
	private static final long serialVersionUID = 1L;
	private transient BanqueFacade banque;
	private boolean aDecouvert;
	private Compte compte;
	private Client client;
	private String userInfo;
	private String compteInfo;

	/**
	 * Constructeur de la classe Connect
	 *
     */
	public ListeCompteManager() {
        logger.debug("Tentative de création de l'objet ListeCompteManager {}", this);
	}

    // Le setter indispensable pour l'injection
    public void setBanqueFacade(BanqueFacade banqueFacade) {
        this.banque = banqueFacade;
    }

	/**
	 * Méthode qui va renvoer la liste de tous les clients sous forme de hashmap
	 * 
	 * @return Map<String,Client> : la hashmap correspondant au résultat
	 */
	public Map<String, Client> getAllClients() {
		banque.loadClients();
		return banque.getAllClients();
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
	 * Getter pour le champ aDecouvert.
	 * 
	 * @return boolean : la valeur du champ aDecouvert
	 */
	public boolean isaDecouvert() {
		return aDecouvert;
	}

	/**
	 * Setter pour le champ aDecouvert.
	 * 
	 * @param aDecouvert
	 *            : la valeur de ce qu'on veut définir
	 */
	public void setaDecouvert(boolean aDecouvert) {
		this.aDecouvert = aDecouvert;
	}

	/**
	 * @return the compte
	 */
	public Compte getCompte() {
		return compte;
	}

	/**
	 * @param compte
	 *            the compte to set
	 */
	public void setCompte(Compte compte) {
		this.compte = compte;
	}

	/**
	 * @return the client
	 */
	public Client getClient() {
		return client;
	}

	/**
	 * @param client
	 *            the client to set
	 */
	public void setClient(Client client) {
		this.client = client;
	}

	/**
	 * @return the userInfo
	 */
	public String getUserInfo() {
		return userInfo;
	}

	/**
	 * @param userInfo
	 *            the userInfo to set
	 */
	private void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}

	/**
	 * @return the userInfo
	 */
	public String getCompteInfo() {
		return compteInfo;
	}

	/**
	 * @param compteInfo
	 *            the compteInfo to set
	 */
	private void setCompteInfo(String compteInfo) {
		this.compteInfo = compteInfo;
	}

	/**
	 * Action appelée pour supprimer un utilisateur
	 * 
	 * @return String, le status de l'opération
	 */
	public String deleteUser() {
		try {
			setUserInfo(client.getIdentity());
			banque.deleteUser(client);
            logger.info("Utilisateur supprimé avec succès");
			return "SUCCESS";
		} catch (TechnicalException e) {
            logger.error("Erreur technique lors de la suppression d'utilisateur", e);
			return "ERROR";
		} catch (IllegalOperationException ioe) {
            logger.error("Opération illégale", ioe);
			return "NONEMPTYACCOUNT";
		}
	}

	/**
	 * Action appelée pour supprimer un compte
	 * 
	 * @return String, le status de l'opération
	 */
	public String deleteAccount() {
		try {
			setCompteInfo(compte.getNumeroCompte());
			banque.deleteAccount(compte);
            logger.info("Compte supprimé avec succès");
			return "SUCCESS";
		} catch (IllegalOperationException e) {
            logger.error("Opération illégale", e);
			return "NONEMPTYACCOUNT";
		} catch (TechnicalException e) {
            logger.error("Erreur technique lors de la suppression d'utilisateur", e);
			return "ERROR";
		}
	}
}
