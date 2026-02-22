package com.iut.banque.controller;

import java.util.Map;

import com.opensymphony.xwork2.ActionSupport;

import com.iut.banque.constants.LoginConstants;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;
import com.iut.banque.modele.Utilisateur;
import org.slf4j.Logger;         // Import SLF4J
import org.slf4j.LoggerFactory;  // Import SLF4J

public class Connect extends ActionSupport {

    private static final String ERROR_ERROR = "ERROR";
    private static final Logger logger = LoggerFactory.getLogger(Connect.class);
	private static final long serialVersionUID = 1L;
	private String userCde;
	private String userPwd;
    private String Retour;
	private transient BanqueFacade banque;

	/**
	 * Constructeur de la classe Connect
	 *
     */
	public Connect() {
        logger.debug("Tentative de création de l'objet de connexion {}", this);
	}

    // Un Setter pour permettre aux tests d'injecter un Mock
    public void setBanqueFacade(BanqueFacade banqueFacade) {
        this.banque = banqueFacade;
    }

	/**
	 * Méthode pour vérifier la connexion de l'utilisateur basé sur les
	 * paramètres userCde et userPwd de cette classe
	 * 
	 * @return String, le resultat du login; "SUCCESS" si réussi, "ERROR" si
	 *         échec
	 */
	public String login() {
        logger.debug("Tentative de connexion login");

		if (userCde == null || userPwd == null) {
			return ERROR_ERROR;
		}
		userCde = userCde.trim();

		int loginResult;
		try {
			loginResult = banque.tryLogin(userCde, userPwd);
		} catch (Exception e) {
            logger.error("Erreur technique lors de la connexion", e);
			loginResult = LoginConstants.ERROR;
		}

		switch (loginResult) {
		case LoginConstants.USER_IS_CONNECTED:
            logger.info("Utilisateur connecté avec succès");
			return "SUCCESS";
		case LoginConstants.MANAGER_IS_CONNECTED:
            logger.info("Gestionnaire connecté avec succès");
			return "SUCCESSMANAGER";
		case LoginConstants.LOGIN_FAILED:
            logger.error("Connexion échouée");
			return ERROR_ERROR;
		default:
            logger.error("Erreur technique lors de la connexion");
			return ERROR_ERROR;
		}
	}

	/**
	 * Getter du champ userCde
	 * 
	 * @return String, le userCde de la classe
	 */
	public String getUserCde() {
		return userCde;
	}

	/**
	 * Setter du champ userCde
	 * 
	 * @param userCde
	 *            : String correspondant au userCode à établir
	 */
	public void setUserCde(String userCde) {
		this.userCde = userCde;
	}

	/**
	 * Getter du champ userPwd
	 * 
	 * @return String, le userPwd de la classe
	 */
	public String getUserPwd() {
		return userPwd;
	}

	/**
	 * Setter du champ userPwd
	 * 
	 * @param userPwd
	 *            : correspondant au pwdCde à établir
	 */
	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	/**
	 * Getter du champ utilisateur (utilisé pour récupérer l'utilisateur
	 * actuellement connecté à l'application)
	 * 
	 * @return Utilisateur, l'utilisateur de la classe
	 */
	public Utilisateur getConnectedUser() {
		return banque.getConnectedUser();
	}

	/**
	 * Méthode qui va récupérer sous forme de map la liste des comptes du client
	 * actuellement connecté à l'application
	 * 
	 * @return Map<String, Compte> correspondant à l'ID du compte et l'objet
	 *         Compte qui y est associé
	 */
	public Map<String, Compte> getAccounts() {
		return ((Client) banque.getConnectedUser()).getAccounts();
	}

	public String logout() {
        logger.info("Utilisateur déconnecté avec succès");
		banque.logout();
		return "SUCCESS";
	}

	/**
	 * Setter pour injecter BanqueFacade (utilisé pour les tests unitaires)
	 * 
	 * @param banque
	 *            : BanqueFacade à injecter
	 */
	public void setBanque(BanqueFacade banque) {
		this.banque = banque;
	}

    public String getRetour() { return Retour; }
    public void setRetour(String retour) { this.Retour = retour; }
}
