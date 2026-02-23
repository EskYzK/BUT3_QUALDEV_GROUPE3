package com.iut.banque.facade;

import java.util.Map;
import java.security.SecureRandom;

import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.exceptions.InsufficientFundsException;
import com.iut.banque.exceptions.TechnicalException;
import com.iut.banque.interfaces.IDao;
import com.iut.banque.modele.Banque;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;
import com.iut.banque.modele.CompteAvecDecouvert;
import com.iut.banque.modele.Gestionnaire;
import com.iut.banque.modele.Utilisateur;
import java.util.Calendar;
import java.util.Date;
import com.iut.banque.modele.CarteBancaire;
import com.iut.banque.modele.CarteDebitDiffere;
import com.iut.banque.modele.CarteDebitImmediat;
import com.iut.banque.modele.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BanqueManager {

    private static final Logger logger = LoggerFactory.getLogger(BanqueManager.class);
	private Banque bank;
	private IDao dao;

	/**
	 * Constructeur du BanqueManager
	 * 
	 * @return BanqueManager : un nouvel objet BanqueManager
	 */
	public BanqueManager() {
		super();
		bank = new Banque();
	}

	/**
	 * Méthode utilisé pour les tests unitaires et ne devrait pas être utilisée
	 * ailleurs.
	 */
	public Compte getAccountById(String id) {
		return dao.getAccountById(id);
	}

	/**
	 * Méthode utilisée pour les tests unitaires et ne devrait pas être utilisée
	 * ailleurs.
	 */
	public Utilisateur getUserById(String id) {
		return dao.getUserById(id);
	}

	/**
	 * Setter pour la DAO.
	 * 
	 * Utilisé par Spring par Injection de Dependence
	 * 
	 * @param dao
	 *            : la dao nécessaire pour le BanqueManager
	 */
	public void setDao(IDao dao) {
		this.dao = dao;
	}

	/**
	 * Méthode pour créditer un compte en faisant appel à la méthode créditer de
	 * l'objet bank pour mettre à jour localement, et ensuite appeler la méthode
	 * updateAccount de la DAO pour mettre à jour les données dans la base de
	 * données
	 * 
	 * @param compte
	 *            : un objet de type compte représentant le compte à créditer
	 * @param montant
	 *            : un double correspondant au montant à créditer
	 * @throws IllegalFormatException
	 *             : si le param montant est négatif
	 */
	public void crediter(Compte compte, double montant) throws IllegalFormatException {
		bank.crediter(compte, montant);
		dao.updateAccount(compte);
        Operation op = new Operation(
                "Crédit réalisé en agence",  // Libellé
                montant,                     // Montant positif
                new Date(),                  // Date du jour
                "DEPOT",                     // Type d'opération
                compte,                       // Compte associé
                null
        );
        try {
            dao.createOperation(op);
        } catch (Exception e) {
            logger.error("Erreur lors de la création de l'historique crédit", e);
        }
	}

	/**
	 * Méthode pour créditer un compte en faisant appel à la méthode créditer de
	 * l'objet bank pour mettre à jour localement, et ensuite appeler la méthode
	 * updateAccount de la DAO pour mettre à jour les données dans la base de
	 * données
	 * 
	 * @param compte
	 *            : un objet de type compte repr�sentant le compte à créditer
	 * @param montant
	 *            : un double correspondant au montant à créditer
	 * @throws IllegalFormatException
	 *             : si le param montant est négatif
     * @throws InsufficientFundsException
	 *             : si les fonds sont insuffisants
	 */
	public void debiter(Compte compte, double montant) throws InsufficientFundsException, IllegalFormatException {
		bank.debiter(compte, montant);
		dao.updateAccount(compte);
        Operation op = new Operation(
                "Débit réalisé en agence",   // Libellé
                -montant,                    // Montant NÉGATIF !
                new Date(),                  // Date du jour
                "RETRAIT",                   // Type d'opération
                compte,                       // Compte associé
                null
        );
        try {
            dao.createOperation(op);
        } catch (Exception e) {
            logger.error("Erreur lors de la création de l'historique débit", e);
        }
	}

	/**
	 * Méthode pour mettre à jour la liste des comptes de la banque. Elle
	 * contiendra la liste de tous les comptes de la banque
	 */
	public void loadAllClients() {
		bank.setClients(dao.getAllClients());
	}

	/**
	 * Méthode pour mettre à jour la liste des gestionnaires de la banque. Elle
	 * contiendra la liste de tous les gestionnaires de la banque
	 */
	public void loadAllGestionnaires() {
		bank.setGestionnaires(dao.getAllGestionnaires());
	}

	/**
	 * Cette méthode renvoie tous les clients de la banque
	 * 
	 * @return la liste de tous les clients
	 */
	public Map<String, Client> getAllClients() {
		return bank.getClients();
	}

	/**
	 * Cette méthode renvoie tous les gestionnaires de la banque
	 * 
	 * @return la liste de tous les clients
	 */
	public Map<String, Client> getAllManagers() {
		return bank.getClients();
	}

	/**
	 * Cette méthode appelle la DAO pour cr�er un compte sans découvert dans la
	 * BdD
	 * 
	 * @param numeroCompte
	 *            String correspondant au numéro de compte à créer
	 * @param client
	 *            Client, objet correspondant à l'objet client auquel on veut
	 *            rajouter le compte
	 * @throws TechnicalException
	 * @throws IllegalFormatException
	 */
	public void createAccount(String numeroCompte, Client client) throws TechnicalException, IllegalFormatException {
		dao.createCompteSansDecouvert(0, numeroCompte, client);
	}

	/**
	 * Cette méthode appelle la DAO pour créer un compte avec découvert dans la
	 * BdD
	 * 
	 * @param numeroCompte
	 *            String correspondant au numéro de compte à créer
	 * @param client
	 *            Client, objet correspondant à l'objet client auquel on veut
	 *            rajouter un compte
	 * @param decouvertAutorise
	 *            double correspondant au montant de découvert autorisé pour le
	 *            nouveau compte
	 * @throws TechnicalException
	 * @throws IllegalFormatException
	 * @throws IllegalOperationException
	 */
	public void createAccount(String numeroCompte, Client client, double decouvertAutorise)
			throws TechnicalException, IllegalFormatException, IllegalOperationException {
		dao.createCompteAvecDecouvert(0, numeroCompte, decouvertAutorise, client);
	}

	/**
	 * Méthode qui va appeler la DAO pour supprimer le compte passé en paramètre
	 * dans la mesure du possible
	 * 
	 * @param c
	 *            Compte correspondant à l'objet à supprimer
	 * @throws IllegalOperationException
	 *             quand on essaie la suppression d'un compte avec un solde
	 *             différent de 0.
	 * @throws TechnicalException
	 *             si le compte est null ou si le compte n'est pas un compte
	 *             persistant.
	 */
	public void deleteAccount(Compte c) throws IllegalOperationException, TechnicalException {
		if (c.getSolde() != 0) {
			throw new IllegalOperationException("Impossible de supprimer un compte avec une solde différent de 0");
		}
		dao.deleteAccount(c);
	}

	/**
	 * Méthode qui va appeler la DAO pour créer un nouveau manager dans la BdD
	 * 
	 * @param userId
	 *            String pour le userId à utiliser
	 * @param userPwd
	 *            String pour le password à utiliser
	 * @param nom
	 *            String pour le nom
	 * @param prenom
	 *            String pour le prenom
	 * @param adresse
	 *            String pour l'adresse
	 * @param male
	 *            boolean pour savoir si c'est un homme ou une femme
	 * @throws TechnicalException
	 *             : Si l'id fourni en paramètre est déjà assigné à un autre
	 *             utilisateur de la base
	 * @throws IllegalFormatException
	 * @throws IllegalArgumentException
	 */
	public void createManager(String userId, String userPwd, String nom, String prenom, String adresse, boolean male, String email)
			throws TechnicalException, IllegalArgumentException, IllegalFormatException {
		dao.createUser(nom, prenom, adresse, male, userId, userPwd, email, true, null);
	}

	/**
	 * Méthode qui va appeler la DAO pour créer un nouveau client dans la BdD
	 * 
	 * @param userId
	 *            String pour le userId à utiliser
	 * @param userPwd
	 *            String pour le password à utiliser
	 * @param nom
	 *            String pour le nom
	 * @param prenom
	 *            String pour le prenom
	 * @param adresse
	 *            String pour l'adresse
	 * @param male
	 *            boolean pour savoir si c'est un homme ou une femme
	 * @param numeroClient
	 *            String pour le numero de client
	 * @throws IllegalOperationException
	 * @throws TechnicalException
	 *             : Si l'id fourni en param�tre est déjà assigné à un autre
	 *             utilisateur de la base
	 * @throws IllegalFormatException
	 * @throws IllegalArgumentException
	 */
	public void createClient(String userId, String userPwd, String nom, String prenom, String adresse, boolean male, String email, String numeroClient)
			throws IllegalOperationException, TechnicalException, IllegalArgumentException, IllegalFormatException {
		Map<String, Client> liste = this.getAllClients();
		for (Map.Entry<String, Client> entry : liste.entrySet()) {
			if (entry.getValue().getNumeroClient().equals(numeroClient)) {
				throw new IllegalOperationException(
						"Un client avec le numero de client " + numeroClient + " existe déjà");
			}
		}
		dao.createUser(nom, prenom, adresse, male, userId, userPwd, email, false, numeroClient);

	}

	/**
	 * Méthode qui va appeler la DAO pour supprimer un utilisateur
	 * 
	 * @param u
	 *            Utilisateur correspondant à l'objet Utilisateur à supprimer
	 * @throws TechnicalException
	 *             si l'user est null ou si l'utilisateur n'est pas un
	 *             utilisateur persistant.
	 * @throws IllegalOperationException
	 *             si le manager à supprimer est le dernier dans la base
	 */
	public void deleteUser(Utilisateur u) throws IllegalOperationException, TechnicalException {
		if (u instanceof Client) {
			Map<String, Compte> liste = ((Client) u).getAccounts();
			for (Map.Entry<String, Compte> entry : liste.entrySet()) {
				this.deleteAccount(entry.getValue());
			}
		} else if (u instanceof Gestionnaire && bank.getGestionnaires().size() == 1) {
            throw new IllegalOperationException("Impossible de supprimer le dernier gestionnaire de la banque");
        }
		this.bank.deleteUser(u.getUserId());
		dao.deleteUser(u);
	}

	/**
	 * Change le découvert d'un compte
	 * 
	 * @param compte
	 * @param nouveauDecouvert
	 * @throws IllegalFormatException
	 * @throws IllegalOperationException
	 */
	public void changeDecouvert(CompteAvecDecouvert compte, double nouveauDecouvert) throws IllegalFormatException, IllegalOperationException {
		bank.changeDecouvert(compte, nouveauDecouvert);
		dao.updateAccount(compte);
	}

    // ------------------------------------------------------------------------
    // GESTION DES CARTES BANCAIRES
    // ------------------------------------------------------------------------

    /**
     * Création d'une nouvelle carte bancaire.
     * Génère un numéro de carte aléatoire (simulation) et l'associe au compte.
     */
    public CarteBancaire creerCarte(String numeroCompte, double plafond, String typeDebit)
            throws TechnicalException, IllegalFormatException {

        Compte compte = dao.getAccountById(numeroCompte);
        if (compte == null) {
            throw new TechnicalException("Le compte associé n'existe pas.");
        }

        // Génération d'un faux numéro de carte à 16 chiffres pour l'exercice
        String numeroCarte = generateRandomCardNumber();

        CarteBancaire carte;
        if ("IMMEDIAT".equals(typeDebit)) {
            carte = new CarteDebitImmediat(numeroCarte, plafond, compte);
        } else if ("DIFFERE".equals(typeDebit)) {
            carte = new CarteDebitDiffere(numeroCarte, plafond, compte);
        } else {
            throw new IllegalFormatException("Type de débit invalide (doit être IMMEDIAT ou DIFFERE)");
        }

        return dao.createCarteBancaire(carte);
    }

    /**
     * Récupère une carte bancaire par son numéro.
     */
    public CarteBancaire getCarte(String numeroCarte) {
        return dao.getCarteBancaire(numeroCarte);
    }

    /**
     * Change le plafond d'une carte.
     * Autorisé pour tous les types de cartes.
     */
    public void changerPlafondCarte(String numeroCarte, double nouveauPlafond) throws TechnicalException {
        CarteBancaire carte = dao.getCarteBancaire(numeroCarte);
        if (carte == null) {
            throw new TechnicalException("Carte introuvable.");
        }
        if (carte.isSupprimee()) {
            throw new TechnicalException("Impossible de modifier une carte supprimée.");
        }

        carte.setPlafond(nouveauPlafond);
        dao.updateCarteBancaire(carte);
    }

    /**
     * Change le compte associé à une carte.
     * RÈGLE MÉTIER : Interdit pour les cartes à débit différé.
     */
    public void changerCompteLieCarte(String numeroCarte, String nouveauNumeroCompte)
            throws TechnicalException, IllegalOperationException {

        CarteBancaire carte = dao.getCarteBancaire(numeroCarte);
        if (carte == null) {
            throw new TechnicalException("Carte introuvable.");
        }

        // Vérification de la règle métier spécifique
        if (carte instanceof CarteDebitDiffere) {
            throw new IllegalOperationException("Impossible de changer le compte d'une carte à débit différé (Cohérence comptable).");
        }

        Compte nouveauCompte = dao.getAccountById(nouveauNumeroCompte);
        if (nouveauCompte == null) {
            throw new TechnicalException("Le nouveau compte n'existe pas.");
        }

        carte.setCompte(nouveauCompte);
        dao.updateCarteBancaire(carte);
    }

    /**
     * Bloque une carte (Temporaire ou Définitif).
     * managerRequest = true si c'est le banquier qui demande, false si c'est le client.
     */
    public void bloquerCarte(String numeroCarte, boolean definitif) throws TechnicalException {
        CarteBancaire carte = dao.getCarteBancaire(numeroCarte);
        if (carte == null) {
            throw new TechnicalException("Carte introuvable.");
        }

        if (definitif) {
            carte.setSupprimee(true); // Blocage irréversible
        } else {
            carte.setBloquee(true); // Blocage temporaire
        }
        dao.updateCarteBancaire(carte);
    }

    /**
     * Débloque une carte.
     * RÈGLE MÉTIER : Impossible si la carte est supprimée (blocage définitif).
     */
    public void debloquerCarte(String numeroCarte) throws TechnicalException, IllegalOperationException {
        CarteBancaire carte = dao.getCarteBancaire(numeroCarte);
        if (carte == null) {
            throw new TechnicalException("Carte introuvable.");
        }

        if (carte.isSupprimee()) {
            throw new IllegalOperationException("Impossible de débloquer une carte supprimée définitivement.");
        }

        carte.setBloquee(false);
        dao.updateCarteBancaire(carte);
    }

    /**
     * Tente d'effectuer un paiement par carte.
     * VÉRIFIE LE PLAFOND GLISSANT SUR 30 JOURS.
     */
    public void effectuerPaiementCarte(String numeroCarte, double montant, String libelle)
            throws TechnicalException, IllegalOperationException, InsufficientFundsException, IllegalFormatException {

        CarteBancaire carte = dao.getCarteBancaire(numeroCarte);
        if (carte == null) throw new TechnicalException("Carte introuvable.");
        if (carte.isBloquee() || carte.isSupprimee()) {
            throw new IllegalOperationException("Carte bloquée ou supprimée. Paiement refusé.");
        }

        // 1. Calcul des dépenses sur les 30 derniers jours glissants
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -30);
        Date dateDebut = cal.getTime();

        // On récupère la somme des dépenses
        double depenses30Jours = dao.getMontantTotalDepensesCarte(
                carte.getNumeroCarte(),
                dateDebut
        );

        // 2. Vérification du plafond
        if ((depenses30Jours + montant) > carte.getPlafond()) {
            throw new InsufficientFundsException("Plafond de carte dépassé (" + carte.getPlafond() + "€ sur 30 jours).");
        }

        // 3. Exécution du débit sur le compte (via la méthode existante debiter() qui gère le découvert)
        Compte compte = carte.getCompte();
        String typeOpEnregistre;

        if (carte instanceof CarteDebitImmediat) {
            // Cas 1 : Immédiat → On débite tout de suite
            compte.debiter(montant);
            dao.updateAccount(compte);
            typeOpEnregistre = "CB_IMDT"; // Type classique
        } else {
            // Cas 2 : Différé → On ne touche PAS au solde maintenant
            typeOpEnregistre = "CB_DIFF"; // Type spécifique pour le repérer plus tard
        }

        // 4. Enregistrement de l'opération pour l'historique (et le futur calcul du plafond)
        Operation op = new Operation(libelle, -montant, new Date(), typeOpEnregistre, compte, carte);
        dao.createOperation(op);
    }

    final SecureRandom random = new SecureRandom();

    // Générer un numéro (simulation)
    private String generateRandomCardNumber() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public void cloturerComptesDifferes() {
        logger.info(">>> Début Clôture Mensuelle (Basée sur les opérations)");

        // 1. Calcul des dates (Mois dernier)
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);

        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date debut = cal.getTime();

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date fin = cal.getTime();

        // --- CHARGEMENT FORCÉ DES DONNÉES ---
        // On s'assure que l'objet 'bank' est à jour avec la BDD
        // sinon getAllClients() renvoie null au démarrage du serveur
        loadAllClients();

        // 2. On parcourt TOUS les comptes (via les clients)
        Map<String, Client> clients = getAllClients();
        for (Client client : clients.values()) {
            if (client.getAccounts() == null) continue;

            for (Compte compteObs : client.getAccounts().values()) {

                // --- RECHARGEMENT FRAIS ---
                // On récupère la version "Temps Réel" de la BDD (avec les 3000€)
                // et on ignore la version en cache (qui a 70€)
                Compte compte = dao.getAccountById(compteObs.getNumeroCompte());
                if (compte == null) continue;

                // 3. On demande la somme des 'CB_DIFF' du mois dernier
                // (Peu importe quelle carte a généré ces opérations, active ou supprimée)
                double totalADebiter = 0;
                try {
                    totalADebiter = dao.getMontantTotalDepensesDifferees(compte.getNumeroCompte(), debut, fin);
                } catch (Exception e) {
                    logger.error("Erreur lors du calcul du débit différé pour le compte {}", compte.getNumeroCompte(), e);
                    continue;
                }

                // 4. Si on a trouvé des dépenses différées, on prélève
                if (totalADebiter > 0) {
                    logger.info("Prélèvement de {}€ sur le compte {}", totalADebiter, compte.getNumeroCompte());

                    // A. On force le débit
                    compte.debitTechnique(totalADebiter);
                    dao.updateAccount(compte);

                    // B. On trace le prélèvement
                    Operation op = new Operation(
                            "Prélèvement mensuel des cartes différées",
                            -totalADebiter,
                            new Date(),
                            "PRLV_DIFF", // Type spécial pour archive
                            compte,
                            null
                    );
                    dao.createOperation(op);
                }
            }
        }
        logger.info(">>> Fin Clôture");
    }
}
