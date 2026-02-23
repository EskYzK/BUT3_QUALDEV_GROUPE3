package com.iut.banque.test.modele;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.iut.banque.modele.CarteDebitImmediat;

public class TestsCarteDebitImmediat {

    @Test
    public void testConstructeurVide() {
        CarteDebitImmediat carte = new CarteDebitImmediat();
        assertNotNull("L'objet CarteDebitImmediat ne devrait pas être null", carte);
    }

    @Test
    public void testConstructeurComplet() {
        CarteDebitImmediat carte = new CarteDebitImmediat("8765432187654321", 500.0, null);
        assertNotNull(carte);
        assertEquals("8765432187654321", carte.getNumeroCarte());
        assertEquals(500.0, carte.getPlafond(), 0.001);
    }

    @Test
    public void testGetTypeDeCarte() {
        CarteDebitImmediat carte = new CarteDebitImmediat();
        assertEquals("Débit Immédiat", carte.getTypeDeCarte());
    }

    @Test
    public void testToString() {
        CarteDebitImmediat carte = new CarteDebitImmediat();
        String affichage = carte.toString();
        assertTrue("Le toString doit contenir le nom de la classe", affichage.startsWith("CarteDebitImmediat ["));
    }
}