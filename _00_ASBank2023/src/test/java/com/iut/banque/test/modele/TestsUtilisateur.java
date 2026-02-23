package com.iut.banque.test.modele;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;

import org.junit.Before;
import org.junit.Test;

import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.modele.Utilisateur;

public class TestsUtilisateur {

    // Sous-classe concrète fictive pour pouvoir instancier Utilisateur
    private static class UtilisateurImpl extends Utilisateur {
        private static final long serialVersionUID = 1L;

        public UtilisateurImpl() {
            super();
        }

        public UtilisateurImpl(String nom, String prenom, String adresse, boolean male, String userId,
                               String userPwd, String email, String resetToken, Timestamp tokenExpiry) {
            super(nom, prenom, adresse, male, userId, userPwd, email, resetToken, tokenExpiry);
        }
    }

    private UtilisateurImpl utilisateur;

    @Before
    public void setUp() {
        utilisateur = new UtilisateurImpl();
    }

    @Test
    public void testGettersEtSettersBasiques() {
        Timestamp ts = new Timestamp(System.currentTimeMillis());

        utilisateur.setNom("Doe");
        utilisateur.setPrenom("John");
        utilisateur.setAdresse("1 rue de Paris");
        utilisateur.setMale(true);
        utilisateur.setUserPwd("motdepasse123");
        utilisateur.setEmail("john.doe@email.com");
        utilisateur.setResetToken("tokenABC");
        utilisateur.setTokenExpiry(ts);

        assertEquals("Doe", utilisateur.getNom());
        assertEquals("John", utilisateur.getPrenom());
        assertEquals("1 rue de Paris", utilisateur.getAdresse());
        assertTrue(utilisateur.isMale());
        assertEquals("motdepasse123", utilisateur.getUserPwd());
        assertEquals("john.doe@email.com", utilisateur.getEmail());
        assertEquals("tokenABC", utilisateur.getResetToken());
        assertEquals(ts, utilisateur.getTokenExpiry());
    }

    @Test
    public void testSetUserId_CasNominal() throws IllegalFormatException {
        utilisateur.setUserId("jdoe");
        assertEquals("jdoe", utilisateur.getUserId());
    }

    // Test pour vérifier que l'exception est bien levée si on passe null
    @Test(expected = IllegalFormatException.class)
    public void testSetUserId_Null() throws IllegalFormatException {
        utilisateur.setUserId(null);
    }

    // Test pour vérifier que l'exception est bien levée si on passe une chaîne vide
    @Test(expected = IllegalFormatException.class)
    public void testSetUserId_Vide() throws IllegalFormatException {
        utilisateur.setUserId("");
    }

    @Test
    public void testConstructeurComplet() {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        UtilisateurImpl userComplet = new UtilisateurImpl("Smith", "Alice", "Lyon", false, "asmith", "pwd", "alice@mail.com", "token123", ts);

        assertEquals("Smith", userComplet.getNom());
        assertEquals("Alice", userComplet.getPrenom());
        assertFalse(userComplet.isMale()); // Vérifie que "false" = femme
        assertEquals("asmith", userComplet.getUserId());
    }

    @Test
    public void testToString() throws IllegalFormatException {
        utilisateur.setUserId("userTest");
        utilisateur.setNom("Martin");
        utilisateur.setPrenom("Paul");
        utilisateur.setAdresse("Metz");
        utilisateur.setMale(true);
        utilisateur.setUserPwd("pass");
        utilisateur.setEmail("paul@martin.fr");

        String attendu = "Utilisateur [userId=userTest, nom=Martin, prenom=Paul, adresse=Metz, male=true, userPwd=pass, email=paul@martin.fr]";
        assertEquals(attendu, utilisateur.toString());
    }
}