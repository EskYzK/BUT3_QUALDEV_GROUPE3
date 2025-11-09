package com.iut.banque.security;

import org.mindrot.jbcrypt.BCrypt;

/**
 * PasswordHasher — utilitaire simple pour hashage et vérification de mots de passe.
 * Utilise BCrypt (lib org.mindrot:jbcrypt).
 */
public class PasswordHasher {
    private static final int LOG_ROUNDS = 12;

    public static String hash(String password) {
        if (password == null) throw new IllegalArgumentException("password null");
        return BCrypt.hashpw(password, BCrypt.gensalt(LOG_ROUNDS));
    }

    public static boolean verify(String password, String hashed) {
        if (password == null || hashed == null) return false;
        return BCrypt.checkpw(password, hashed);
    }

    public static boolean modifyPassword(String password) {

    }
}