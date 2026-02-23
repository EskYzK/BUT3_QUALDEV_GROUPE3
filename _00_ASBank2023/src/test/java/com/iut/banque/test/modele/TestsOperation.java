package com.iut.banque.test.modele;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.junit.Test;

import com.iut.banque.modele.Operation;

public class TestsOperation {

    @Test
    public void testConstructeurVide() {
        Operation op = new Operation();
        assertNotNull("L'objet Operation ne devrait pas être null", op);
        assertNull("Le libellé devrait être null par défaut", op.getLibelle());
        assertEquals("Le montant par défaut doit être 0.0", 0.0, op.getMontant(), 0.001);
    }

    @Test
    public void testConstructeurCompletEtGetters() {
        Date date = new Date();
        // Utilisation de null pour Compte et CarteBancaire pour simplifier le test unitaire
        Operation op = new Operation("Achat Supermarché", -50.0, date, "CB", null, null);

        assertEquals("Achat Supermarché", op.getLibelle());
        assertEquals(-50.0, op.getMontant(), 0.001);
        assertEquals(date, op.getDateOperation());
        assertEquals("CB", op.getTypeOperation());
        assertNull(op.getCompte());
        assertNull(op.getCarte());
    }

    @Test
    public void testSetters() {
        Operation op = new Operation();
        Date date = new Date();

        op.setIdOperation(10);
        op.setLibelle("Virement Salaire");
        op.setMontant(2000.0);
        op.setDateOperation(date);
        op.setTypeOperation("VIREMENT");

        assertEquals(10, op.getIdOperation());
        assertEquals("Virement Salaire", op.getLibelle());
        assertEquals(2000.0, op.getMontant(), 0.001);
        assertEquals(date, op.getDateOperation());
        assertEquals("VIREMENT", op.getTypeOperation());
    }

    @Test
    public void testToString() {
        Date date = new Date();
        Operation op = new Operation("Achat en ligne", -25.50, date, "CB", null, null);
        op.setIdOperation(5);

        String resultatAttendu = "Operation [id=5, montant=-25.5, type=CB, date=" + date + "]";
        assertEquals(resultatAttendu, op.toString());
    }
}