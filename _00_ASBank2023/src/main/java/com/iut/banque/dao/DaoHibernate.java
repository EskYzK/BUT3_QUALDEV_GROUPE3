package com.iut.banque.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.iut.banque.modele.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.exceptions.TechnicalException;
import com.iut.banque.interfaces.IDao;
import com.iut.banque.security.PasswordHasher;


/**
 * Implémentation de IDao utilisant Hibernate.
 *
 * Les transactions sont gerés par Spring et utilise le transaction manager
 * défini dans l'application Context.
 *
 * Par défaut, la propagation des transactions est REQUIRED, ce qui signifie que
 * si une transaction est déjà commencé elle va être réutilisée. Cela est util
 * pour les tests unitaires de la DAO.
 */
@Transactional
public class DaoHibernate implements IDao {

	private SessionFactory sessionFactory;

	public DaoHibernate() {
		System.out.println("==================");
		System.out.println("Création de la Dao");
	}

	/**
	 * Setter pour la SessionFactory.
	 *
	 * Cette méthode permet à Spring d'injecter la factory au moment de la
	 * construction de la DAO.
	 *
	 * @param sessionFactory
	 *            : la session factory nécessaire à la gestion des sessions
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * {@inheritDoc}
	 * @throws IllegalOperationException
	 */
	@Override
	public CompteAvecDecouvert createCompteAvecDecouvert(double solde, String numeroCompte, double decouvertAutorise,
			Client client) throws TechnicalException, IllegalFormatException, IllegalOperationException {
		Session session = sessionFactory.getCurrentSession();
		CompteAvecDecouvert compte = session.get(CompteAvecDecouvert.class, numeroCompte);
		if (compte != null) {
			throw new TechnicalException("Numéro de compte déjà utilisé.");
		}

		compte = new CompteAvecDecouvert(numeroCompte, solde, decouvertAutorise, client);
		client.addAccount(compte);
		session.save(compte);

		return compte;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompteSansDecouvert createCompteSansDecouvert(double solde, String numeroCompte, Client client)
			throws TechnicalException, IllegalFormatException {
		Session session = sessionFactory.getCurrentSession();
		CompteSansDecouvert compte = session.get(CompteSansDecouvert.class, numeroCompte);
		if (compte != null) {
			throw new TechnicalException("Numéro de compte déjà utilisé.");
		}

		compte = new CompteSansDecouvert(numeroCompte, solde, client);
		session.save(compte);
		client.addAccount(compte);
		session.save(compte);

		return compte;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateAccount(Compte c) {
		Session session = sessionFactory.getCurrentSession();
		session.update(c);
	}

    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAccount(Compte c) throws TechnicalException {
		Session session = sessionFactory.getCurrentSession();
		if (c == null) {
			throw new TechnicalException("Ce compte n'existe plus");
		}
		session.delete(c);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Compte> getAccountsByClientId(String id) {
		Session session = sessionFactory.getCurrentSession();
		Client client = session.get(Client.class, id);
		if (client != null) {
			return client.getAccounts();
		} else {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Compte getAccountById(String id) {
		Session session = sessionFactory.getCurrentSession();
		return session.get(Compte.class, id);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws IllegalFormatException
	 * @throws IllegalArgumentException
	 */
	@Override
	public Utilisateur createUser(String nom, String prenom, String adresse, boolean male, String userId,
			String userPwd, String email, boolean manager, String numClient)
			throws TechnicalException, IllegalArgumentException, IllegalFormatException {
		Session session = sessionFactory.getCurrentSession();

		Utilisateur user = session.get(Utilisateur.class, userId);
		if (user != null) {
			throw new TechnicalException("User Id déjà utilisé.");
		}
		String hashedPwd = PasswordHasher.hash(userPwd);

		if (manager) {
			user = new Gestionnaire(nom, prenom, adresse, male, userId, hashedPwd, email);
		} else {
			user = new Client(nom, prenom, adresse, male, userId, hashedPwd, email, numClient);
		}
		session.save(user);

		return user;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteUser(Utilisateur u) throws TechnicalException {
		Session session = sessionFactory.getCurrentSession();
		if (u == null) {
			throw new TechnicalException("Cet utilisateur n'existe plus");
		}
		session.delete(u);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateUser(Utilisateur u) {
		Session session = sessionFactory.getCurrentSession();
		session.update(u);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isUserAllowed(String userId, String userPwd) {
		if (userId == null || userPwd == null) return false;

		Session session = sessionFactory.getCurrentSession();
		userId = userId.trim();
		if (userId.isEmpty() || userPwd.isEmpty()) return false;

		Utilisateur user = session.get(Utilisateur.class, userId);
		if (user == null) return false;

		// Vérifie le mot de passe hashé avec BCrypt
		return PasswordHasher.verify(userPwd, user.getUserPwd());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Utilisateur getUserById(String id) {
		Session session = sessionFactory.getCurrentSession();
		Utilisateur user = session.get(Utilisateur.class, id);
		return user;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Client> getAllClients() {
		Session session = sessionFactory.getCurrentSession();
		@SuppressWarnings("unchecked")
		List<Object> res = session.createCriteria(Client.class).list();
		Map<String, Client> ret = new HashMap<String, Client>();
		for (Object client : res) {
			ret.put(((Client) client).getUserId(), (Client) client);
		}
		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Gestionnaire> getAllGestionnaires() {
		Session session = sessionFactory.getCurrentSession();
		@SuppressWarnings("unchecked")
		List<Object> res = session.createCriteria(Gestionnaire.class).list();
		Map<String, Gestionnaire> ret = new HashMap<String, Gestionnaire>();
		for (Object gestionnaire : res) {
			ret.put(((Gestionnaire) gestionnaire).getUserId(), (Gestionnaire) gestionnaire);
		}
		return ret;
	}

    public Utilisateur getUserByEmail(String email) {
        Session session = sessionFactory.getCurrentSession();
        // Utilisation de Criteria ou HQL
        return (Utilisateur) session.createQuery("FROM Utilisateur u WHERE u.email = :email")
                .setParameter("email", email)
                .uniqueResult();
    }

    public Utilisateur getUserByToken(String token) {
        Session session = sessionFactory.getCurrentSession();
        return (Utilisateur) session.createQuery("FROM Utilisateur u WHERE u.resetToken = :token")
                .setParameter("token", token)
                .uniqueResult();
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void disconnect() {
		System.out.println("Déconnexion de la DAO.");
	}

    @Override
    public void updateUserPassword(String userId, String newPlainPassword) throws TechnicalException {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("userId null ou vide");
        }
        if (newPlainPassword == null) {
            throw new IllegalArgumentException("newPlainPassword null");
        }

        Session session = sessionFactory.getCurrentSession();
        Utilisateur user = session.get(Utilisateur.class, userId);
        if (user == null) {
            throw new TechnicalException("Utilisateur introuvable pour l'id : " + userId);
        }

        // Hash le mot de passe avant stockage
        String hashed = PasswordHasher.hash(newPlainPassword);
        user.setUserPwd(hashed);

        session.update(user); // transaction gérée par Spring/@Transactional
    }

    /**
     * Implémentation de la création de carte.
     */
    @Override
    public CarteBancaire createCarteBancaire(CarteBancaire carte) {
        try {
            this.sessionFactory.getCurrentSession().save(carte);
        } catch (Exception e) {
            // Gestion basique, on pourrait logger l'erreur ici
            e.printStackTrace();
            return null;
        }
        return carte;
    }

    /**
     * Implémentation de la récupération de carte.
     */
    @Override
    public CarteBancaire getCarteBancaire(String numeroCarte) {
        // On utilise get() qui renvoie null si l'objet n'existe pas
        return (CarteBancaire) this.sessionFactory.getCurrentSession().get(CarteBancaire.class, numeroCarte);
    }

    /**
     * Implémentation de la mise à jour.
     */
    @Override
    public void updateCarteBancaire(CarteBancaire carte) {
        this.sessionFactory.getCurrentSession().update(carte);
    }

    /**
     * Implémentation de la création d'opération.
     */
    @Override
    public Operation createOperation(Operation operation) {
        try {
            this.sessionFactory.getCurrentSession().save(operation);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return operation;
    }

    /**
     * Implémentation du calcul des dépenses sur 30 jours.
     */
    @Override
    public double getMontantTotalDepensesCarte(String numeroCarte, java.util.Date dateDebut) {
        Session session = sessionFactory.getCurrentSession();
        // On somme les montants négatifs (dépenses) depuis la date donnée
        String hql = "SELECT sum(o.montant) FROM Operation o " +
                     "WHERE o.carte.numeroCarte = :numCarte " +
                     "AND o.dateOperation >= :date " +
                     "AND o.montant < 0";

        Double resultat = (Double) session.createQuery(hql)
                .setParameter("numCarte", numeroCarte)
                .setParameter("date", dateDebut)
                .uniqueResult();

        // Si aucune dépense, retourne 0. Sinon, retourne la valeur positive (Math.abs)
        return resultat == null ? 0.0 : Math.abs(resultat);
    }

    /**
     * Calcule le total des dépenses en attente (Différé) pour un compte sur une période.
     * On ne filtre QUE sur le type 'CB_DIFF'.
     */
    public double getMontantTotalDepensesDifferees(String numeroCompte, Date debut, Date fin) {
        // On somme les opérations 'CB_DIFF' sur la période donnée
        String hql = "SELECT sum(op.montant) FROM Operation op " +
                "WHERE op.compte.numeroCompte = :numCompte " +
                "AND op.dateOperation BETWEEN :debut AND :fin " +
                "AND op.typeOperation = 'CB_DIFF'";

        Double somme = (Double) sessionFactory.getCurrentSession()
                .createQuery(hql)
                .setParameter("numCompte", numeroCompte)
                .setParameter("debut", debut)
                .setParameter("fin", fin)
                .uniqueResult();

        return (somme == null) ? 0.0 : Math.abs(somme);
    }
}
