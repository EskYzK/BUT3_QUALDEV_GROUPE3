package com.iut.banque.facade;

import com.iut.banque.constants.LoginConstants;
import com.iut.banque.interfaces.IDao;
import com.iut.banque.modele.Gestionnaire;
import com.iut.banque.modele.Utilisateur;
import com.iut.banque.security.PasswordHasher;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;         // Import SLF4J
import org.slf4j.LoggerFactory;  // Import SLF4J

public class LoginManager {

    private static final Logger logger = LoggerFactory.getLogger(LoginManager.class);
	private IDao dao;
	private Utilisateur user;

	/**
	 * Setter pour la DAO.
	 * Utilisé par Spring par Injection de Dependence
	 * 
	 * @param dao
	 *            : la dao nécessaire pour le LoginManager
	 */
	public void setDao(IDao dao) {
		this.dao = dao;
	}

	/**
	 * Méthode pour permettre la connection de l'utilisateur via un login en
	 * confrontant le mdp d'un utilisateur de la base de données avec le mot de
	 * passe donné en paramètre
	 * 
	 * @param userCde
	 *            : un String correspondant à l'userID de l'utilisateur qui
	 *            cherche à se connecter
	 * @param userPwd
	 *            : un String correspondant au mot de passe qui doit être
	 *            confronté avec celui de la base de données
	 * @return int correspondant aux constantes LoginConstants pour informer de
	 *         l'état du login
	 */
	public int tryLogin(String userCde, String userPwd) {
		if (dao.isUserAllowed(userCde, userPwd)) {
			user = dao.getUserById(userCde);
			if (user instanceof Gestionnaire) {
				return LoginConstants.MANAGER_IS_CONNECTED;
			} else {
				return LoginConstants.USER_IS_CONNECTED;
			}
		} else {
			return LoginConstants.LOGIN_FAILED;
		}
	}

	/**
	 * Getter pour avoir l'objet Utilisateur de celui qui est actuellement
	 * connecté à l'application
	 * 
	 * @return Utilisateur : l'objet Utilisateur de celui qui est connecté
	 */
	public Utilisateur getConnectedUser() {
		return user;
	}

	/**
	 * Setter pour changer l'utilisateur actuellement connecté à l'application
	 * 
	 * @param user
	 *            : un objet de type Utilisateur (Client ou Gestionnaire) que
	 *            l'on veut définir comme utilisateur actuellement connecté à
	 *            l'application
	 */
	public void setCurrentUser(Utilisateur user) {
		this.user = user;
	}

	/**
	 * Remet l'utilisateur à null et déconnecte la DAO.
	 */
	public void logout() {
		this.user = null;
		dao.disconnect();
	}

	public boolean changePassword(Utilisateur user, String oldPwd, String newPwd) {
		if (user == null || oldPwd == null || newPwd == null) return false;

		// Vérification de l'ancien mot de passe
		if (!PasswordHasher.verify(oldPwd, user.getUserPwd())) {
			return false;
		}

		// Hash du nouveau mot de passe
		String hashed = PasswordHasher.hash(newPwd);
		user.setUserPwd(hashed);

		// Mise à jour dans la base
		dao.updateUser(user);
		return true;
	}

    /**
     * Étape 1 : Génère le lien
     */
    public boolean initiatePasswordReset(String email) {
        Utilisateur user1 = dao.getUserByEmail(email);
        if (user1 == null) return false;

        // Générer un token unique (UUID)
        String token = java.util.UUID.randomUUID().toString();
        user1.setResetToken(token);

        // Expiration dans 15 minutes
        long timeout = 15 * 60 * 1000;
        user1.setTokenExpiry(new java.sql.Timestamp(System.currentTimeMillis() + timeout));

        dao.updateUser(user1);

        // Construction du lien
        String link = "http://localhost:8080/_00_ASBank2023/resetPasswordForm.action?token=" + token;

        // Envoi du mail
        sendEmail(email, link);

        return true;
    }

    /**
     * Méthode privée pour envoyer le mail via SMTP (Gmail par défaut ici)
     */
    private void sendEmail(String recipientEmail, String resetLink) {
        // 1. Charger le .env
        // ignoreIfMissing() évite le crash si le fichier n'est pas là (ex: sur un serveur de prod qui utilise des vraies variables d'env)
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        // 2. Récupérer les variables (avec une sécurité si c'est vide)
        final String username = dotenv.get("MAIL_USER");
        final String password = dotenv.get("MAIL_PASSWORD");

        if (username == null || password == null) {
            logger.error("ERREUR : Les identifiants mail ne sont pas configurés dans le fichier .env !");
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("ASBank - Réinitialisation de mot de passe");

            String content = "Bonjour, <br><br>"
                    + "Quelqu'un a effectué une demande de réinitialisation du mot de passe de votre compte.<br>"
                    + "Si ce n'est pas vous, veuillez ignorer ce mail.<br>"
                    + "Si c'est bien vous, veuillez cliquer sur le lien ci-dessous : <br><br>"
                    + "<a href='" + resetLink + "'>Réinitialiser mon mot de passe</a><br><br>"
                    + "Ce lien expire dans 15 minutes.";

            message.setContent(content, "text/html; charset=utf-8");

            Transport.send(message);
            logger.info("Email envoyé avec succès à {}", recipientEmail);

        } catch (MessagingException e) {
            logger.error("Erreur lors de l'envoi de l'email", e);
        }
    }

    /**
     * Étape 2 : Valide le token et change le mot de passe
     */
    public boolean usePasswordResetToken(String token, String newPassword) {
        Utilisateur user2 = dao.getUserByToken(token);

        // Vérifications : User existe ? Token expiré ?
        if (user2 == null) return false;
        if (user2.getTokenExpiry().before(new java.sql.Timestamp(System.currentTimeMillis()))) return false;

        // Tout est bon : Hash du mdp et nettoyage du token
        String hashed = PasswordHasher.hash(newPassword);
        user2.setUserPwd(hashed);
        user2.setResetToken(null); // Invalide le token immédiatement
        user2.setTokenExpiry(null);

        dao.updateUser(user2);
        return true;
    }
}
