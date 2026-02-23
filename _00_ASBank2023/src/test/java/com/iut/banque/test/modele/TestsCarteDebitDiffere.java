package com.iut.banque.test.modele;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.iut.banque.modele.CarteDebitDiffere;

public class TestsCarteDebitDiffere {

    @Test
    public void testConstructeurVide() {
        CarteDebitDiffere carte = new CarteDebitDiffere();
        assertNotNull("L'objet CarteDebitDiffere ne devrait pas être null", carte);
    }

    @Test
    public void testConstructeurComplet() {
        // On passe null pour le compte pour isoler le test unitaire
        CarteDebitDiffere carte = new CarteDebitDiffere("1234567812345678", 1500.0, null);
        assertNotNull(carte);
        assertEquals("1234567812345678", carte.getNumeroCarte());
        assertEquals(1500.0, carte.getPlafond(), 0.001);
    }

    @Test
    public void testGetTypeDeCarte() {
        CarteDebitDiffere carte = new CarteDebitDiffere();
        assertEquals("Débit Différé", carte.getTypeDeCarte());
    }

    @Test
    public void testToString() {
        CarteDebitDiffere carte = new CarteDebitDiffere();
        String affichage = carte.toString();
        assertTrue("Le toString doit contenir le nom de la classe", affichage.startsWith("CarteDebitDiffere ["));
    }
}