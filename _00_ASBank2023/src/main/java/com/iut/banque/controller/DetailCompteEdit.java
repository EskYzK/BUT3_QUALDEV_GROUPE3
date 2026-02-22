package com.iut.banque.controller;

import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.modele.CompteAvecDecouvert;
import org.slf4j.Logger;         // Import SLF4J
import org.slf4j.LoggerFactory;  // Import SLF4J

public class DetailCompteEdit extends DetailCompte {

    private static final Logger logger = LoggerFactory.getLogger(DetailCompteEdit.class);
	private static final long serialVersionUID = 1L;
	private String decouvertAutorise;

	/**
	 * Constructeur sans argument de DetailCompteEdit
	 */
	public DetailCompteEdit() {
		super();
        logger.debug("Tentative de création de l'objet DetailCompteEdit {}", this);
	}

	/**
	 * @return the decouvertAutorise
	 */
	public String getDecouvertAutorise() {
		return decouvertAutorise;
	}

	/**
	 * @param decouvertAutorise
	 *            the decouvertAutorise to set
	 */
	public void setDecouvertAutorise(String decouvertAutorise) {
		this.decouvertAutorise = decouvertAutorise;
	}

	/**
	 * Permet le changement de découvert d'un compte avec découvert.
	 * 
	 * @return le status de l'action
	 */
	public String changementDecouvert() {
		if (!(getCompte() instanceof CompteAvecDecouvert)) {
			return "ERROR";
		}
		try {
			Double decouvert = Double.parseDouble(decouvertAutorise);
			banque.changeDecouvert((CompteAvecDecouvert) getCompte(), decouvert);
            logger.info("Changement de découvert effectué avec succès");
			return "SUCCESS";
		} catch (NumberFormatException nfe) {
            logger.error("Erreur dans le format du nombre", nfe);
			return "ERROR";
		} catch (IllegalFormatException e) {
            logger.error("Erreur de format", e);
			return "NEGATIVEOVERDRAFT";
		} catch (IllegalOperationException e) {
            logger.error("Opération illégale", e);
			return "INCOMPATIBLEOVERDRAFT";
		}
	}

}
