package com.iut.banque.modele;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import com.iut.banque.exceptions.IllegalFormatException;

/**
 * Classe représentant un utilisateur quelconque.
 * La stratégie d'héritage choisie est SINGLE_TABLE. Cela signifie que tous les
 * objets de cette classe et des classes filles sont enregistrés dans une seule
 * table dans la base de donnée. Les champs non utilisés par la classe sont
 * NULL.
 * Lors d'un chargement d'un objet appartenant à une des classes filles, le type
 * de l'objet est choisi grâce à la colonne "type" (c'est une colonne de
 * discrimination).
 */
@Entity
@Table(name = "Utilisateur")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING, length = 15)
public abstract class Utilisateur implements Serializable {

    private static final long serialVersionUID = 1L;

	/**
	 * L'identifiant (unique) de l'utilisateur.
	 * Correspond à la clé primaire de l'utilisateur dans la BDD.
	 */
	@Id
	@Column(name = "userId")
	private String userId;

	/**
	 * Le mot de passe de l'utilisateur.
	 * 
	 */
	@Column(name = "userPwd")
	private String userPwd;

	/**
	 * Le nom de l'utilisateur.
	 */
	@Column(name = "nom")
	private String nom;

	/**
	 * Le prénom de l'utilisateur.
	 */
	@Column(name = "prenom")
	private String prenom;

	/**
	 * L'adresse physique de l'utilisateur.
	 */
	@Column(name = "adresse")
	private String adresse;

	/**
	 * Le sexe de l'utilisateur. Vrai si c'est un homme faux sinon.
	 */
	@Column(name = "male")
	private boolean male;

    /**
     * L'email de l'utilisateur.
     */
    @Column(name = "email")
    private String email;

    /**
     * Le jeton associé à l'utilisateur permettant la réinitialisation du mot de passe.
     */
    @Column(name = "resetToken")
    private String resetToken;

    /**
     * La date d'expiration du jeton de réinitialisation du mot de passe associé à l'utilisateur.
     */
    @Column(name = "tokenExpiry")
    private java.sql.Timestamp tokenExpiry;

	/**
	 * @return String, le nom de l'utilisateur.
	 */
	public String getNom() {
		return nom;
	}

	/**
	 * @param nom
	 *            : le nom de l'utilisateur
	 */
	public void setNom(String nom) {
		this.nom = nom;
	}

	/**
	 * @return String, le prénom de l'utilisateur
	 */
	public String getPrenom() {
		return prenom;
	}

	/**
	 * @param prenom
	 *            : le prénom de l'utilisateur
	 */
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	/**
	 * @return String, l'adresse physique de l'utilisateur
	 */
	public String getAdresse() {
		return adresse;
	}

	/**
	 * @param adresse
	 *            : l'adresse physique de l'utilisateur
	 */
	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}

	/**
	 * @return male : vrai si l'utilisateur est un homme, faux sinon
	 */
	public boolean isMale() {
		return male;
	}

	/**
	 * @param male
	 *            : vrai si l'utilisateur est un homme, faux sinon
	 */
	public void setMale(boolean male) {
		this.male = male;
	}

    /**
     * @return String, l'email de l'utilisateur.
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email
     *            : l'email de l'utilisateur
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return String, le jeton associé à l'utilisateur permettant la réinitialisation du mot de passe.
     */
    public String getResetToken() {
        return resetToken;
    }

    /**
     * @param resetToken
     *            : le jeton associé à l'utilisateur permettant la réinitialisation du mot de passe
     */
    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    /**
     * @return String, la date d'expiration du jeton de réinitialisation du mot de passe associé à l'utilisateur.
     */
    public java.sql.Timestamp getTokenExpiry() {
        return tokenExpiry;
    }

    /**
     * @param tokenExpiry
     *            : la date d'expiration du jeton de réinitialisation du mot de passe associé à l'utilisateur.
     */
    public void setTokenExpiry(java.sql.Timestamp tokenExpiry) {
        this.tokenExpiry = tokenExpiry;
    }

	/**
	 * @return userId : l'identifiant de l'utilisateur
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            : l'identifiant de l'utilisateur
     */
    public void setUserId(String userId) throws IllegalFormatException {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalFormatException("userId ne peut pas être vide");
        }
        this.userId = userId;
    }

    /**
	 * @return userPwd : le mot de passe de l'utilisateur
	 */
	public String getUserPwd() {
		return userPwd;
	}

	/**
	 * @param userPwd
	 *            : le mot de passe de l'utilisateur
	 */
	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	/**
	 * Constructeur d'Utilisateur avec tous les champs de la classe comme
	 * paramètres.
	 * Il est préférable d'utiliser une classe implémentant IDao pour créer un
	 * objet au lieu d'appeler ce constructeur.
	 *
     */
	protected Utilisateur(String nom, String prenom, String adresse, boolean male, String userId, String userPwd, String email, String resetToken,  java.sql.Timestamp tokenExpiry) {
		super();
		this.nom = nom;
		this.prenom = prenom;
		this.adresse = adresse;
		this.male = male;
		this.userId = userId;
		this.userPwd = userPwd;
        this.email = email;
        this.resetToken = resetToken;
        this.tokenExpiry = tokenExpiry;
	}

	/**
	 * Constructeur sans paramètre d'Utilisateur.
	 * Nécessaire pour Hibernate.
	 * Il est préférable d'utiliser une classe implémentant IDao pour créer un
	 * objet au lieu d'appeler ce constructeur.
	 */
	protected Utilisateur() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Utilisateur [userId=" + userId + ", nom=" + nom + ", prenom=" + prenom + ", adresse=" + adresse
				+ ", male=" + male + ", userPwd=" + userPwd + ", email=" + email + "]";
	}

}
