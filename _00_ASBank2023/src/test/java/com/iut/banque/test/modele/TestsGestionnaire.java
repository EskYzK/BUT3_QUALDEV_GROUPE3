package com.iut.banque.test.modele;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.modele.Gestionnaire;

public class TestsGestionnaire {

    private Gestionnaire gestionnaire;

    @Before
    public void setUp() throws IllegalFormatException {
        gestionnaire = new Gestionnaire("Dupont", "Jean", "Paris", true, "jdupont", "1234", "email@test.test");
    }

    /**
     * Test du constructeur avec paramètres
     */
    @Test
    public void testConstructeurCompletOK() {
        assertEquals("Dupont", gestionnaire.getNom());
        assertEquals("Jean", gestionnaire.getPrenom());
        assertEquals("Paris", gestionnaire.getAdresse());
        assertEquals(true, gestionnaire.isMale());
        assertEquals("jdupont", gestionnaire.getUserId());
        assertEquals("1234", gestionnaire.getUserPwd());
    }

    /**
     * Test du constructeur avec usrId vide
     */
    @Test
    public void testConstructeurCompletUsrIdVide() {
        try {
            new Gestionnaire("Durand", "Paul", "Lyon", false, "", "azerty", "email@test.test");
            fail("Une IllegalArgumentException aurait dû être levée !");
        } catch (IllegalArgumentException e) {
            // OK attendu
        } catch (Exception e) {
            fail("Exception inattendue : " + e.getClass().getSimpleName());
        }
    }

    /**
     * Test du constructeur vide (nécessaire pour Hibernate)
     */
    @Test
    public void testConstructeurSansParametre() {
        Gestionnaire g = new Gestionnaire();
        assertNotNull(g);
    }

    /**
     * Test de la méthode toString()
     */
    @Test
    public void testToString() {
        String resultat = gestionnaire.toString();

        // Vérifie que tous les champs importants sont présents dans la chaîne
        if (!resultat.contains("Dupont") || !resultat.contains("Jean")
                || !resultat.contains("Paris") || !resultat.contains("jdupont")) {
            fail("La méthode toString() ne contient pas toutes les informations attendues !");
        }
    }
}