package com.iut.banque.security;

import java.util.List;
import com.iut.banque.dao.DaoHibernate;
import com.iut.banque.modele.Utilisateur;
import org.hibernate.Transaction;
import org.mindrot.jbcrypt.BCrypt;
import org.hibernate.Session;
import org.hibernate.HibernateException;

import java.util.logging.Logger;

/**
 * PasswordHasher — utilitaire simple pour hashage et vérification de mots de passe.
 * Utilise BCrypt (lib org.mindrot:jbcrypt).
 */
public class PasswordHasher {
    private static final Logger logger = Logger.getLogger(PasswordHasher.class.getName());
    private static final int LOG_ROUNDS = 12;

    public static String hash(String password) {
        if (password == null) throw new IllegalArgumentException("password null");
        return BCrypt.hashpw(password, BCrypt.gensalt(LOG_ROUNDS));
    }

    public static boolean verify(String password, String hashed) {
        if (password == null || hashed == null) return false;
        return BCrypt.checkpw(password, hashed);
    }

    /**
     * Rehash tous les utilisateurs existants (Clients et Gestionnaires)
     * en utilisant la méthode updateUserPassword du DAO.
     */
    public static void rehashAllUsers(DaoHibernate dao) {
        Session session = dao.getSessionFactory().openSession(); // Session indépendante
        Transaction tx = session.beginTransaction();

        try {
            @SuppressWarnings("unchecked")
            List<Utilisateur> users = session.createQuery("FROM Utilisateur").list();

            for (Utilisateur user : users) {
                String oldPwd = user.getUserPwd();
                String newHashed = PasswordHasher.hash(oldPwd);
                user.setUserPwd(newHashed);
                session.update(user);
            }

            tx.commit(); // Commit des changements
        } catch (HibernateException e) { // plus précis qu'Exception
            if (tx != null) tx.rollback();
            logger.info("Erreur lors du rehash des mots de passe : " + e.getMessage());
        } finally {
            session.close();
        }
    }
}