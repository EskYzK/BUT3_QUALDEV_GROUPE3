package com.iut.banque.constants;

/**
 * Classe utilitaire contenant des constantes statiques utilisées pour indiquer
 * le status d'une tentative de login.
 */
public final class LoginConstants {

	public static final int USER_IS_CONNECTED = 1;
	public static final int MANAGER_IS_CONNECTED = 2;
	public static final int LOGIN_FAILED = -1;
	public static final int ERROR = -2;

	/**
	 * Constructeur privé pour empêcher l'instanciation
	 */
	private LoginConstants() {
		throw new AssertionError("Classe utilitaire, ne peut pas être instanciée");
	}
}
