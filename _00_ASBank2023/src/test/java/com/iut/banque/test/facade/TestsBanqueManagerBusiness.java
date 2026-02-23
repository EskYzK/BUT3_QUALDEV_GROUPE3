package com.iut.banque.test.facade;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.exceptions.InsufficientFundsException;
import com.iut.banque.exceptions.TechnicalException;
import com.iut.banque.facade.BanqueManager;
import com.iut.banque.interfaces.IDao;
import com.iut.banque.modele.CarteDebitDiffere;
import com.iut.banque.modele.CarteDebitImmediat;
import com.iut.banque.modele.Compte;

public class TestsBanqueManagerBusiness {

    private BanqueManager banqueManager;
    private IDao mockDao;
    private Compte mockCompte;

    @Before
    public void setUp() {
        banqueManager = new BanqueManager();
        mockDao = mock(IDao.class);
        banqueManager.setDao(mockDao);
        mockCompte = mock(Compte.class);
    }

    @Test
    public void testCreerCarteCompteInconnu() {
        when(mockDao.getAccountById("INCONNU")).thenReturn(null);
        try {
            banqueManager.creerCarte("INCONNU", 500, "IMMEDIAT");
            fail("Aurait dû lever une TechnicalException");
        } catch (Exception e) {
            assertTrue(e instanceof TechnicalException);
        }
    }

    @Test
    public void testChangerCompteLieCarteInterditPourDiffere() throws Exception {
        CarteDebitDiffere carteDiff = new CarteDebitDiffere("123", 1000.0, mockCompte);
        when(mockDao.getCarteBancaire("123")).thenReturn(carteDiff);

        try {
            banqueManager.changerCompteLieCarte("123", "NOUVEAU_CPT");
            fail("Aurait dû lever une IllegalOperationException");
        } catch (IllegalOperationException e) {
            assertEquals("Impossible de changer le compte d'une carte à débit différé (Cohérence comptable).", e.getMessage());
        }
    }

    @Test
    public void testPaiementCarteBloquee() throws Exception {
        CarteDebitImmediat carte = new CarteDebitImmediat("123", 1000.0, mockCompte);
        carte.setBloquee(true);
        when(mockDao.getCarteBancaire("123")).thenReturn(carte);

        try {
            banqueManager.effectuerPaiementCarte("123", 50.0, "Achat");
            fail("Aurait dû lever une IllegalOperationException");
        } catch (IllegalOperationException e) {
            assertEquals("Carte bloquée ou supprimée. Paiement refusé.", e.getMessage());
        }
    }

    @Test
    public void testPaiementCartePlafondDepasse() throws Exception {
        CarteDebitImmediat carte = new CarteDebitImmediat("123", 100.0, mockCompte);
        when(mockDao.getCarteBancaire("123")).thenReturn(carte);
        // On simule qu'il a déjà dépensé 80€ ce mois-ci
        when(mockDao.getMontantTotalDepensesCarte(eq("123"), any(Date.class))).thenReturn(80.0);

        try {
            // 80 + 30 = 110 > Plafond (100)
            banqueManager.effectuerPaiementCarte("123", 30.0, "Achat");
            fail("Aurait dû lever une InsufficientFundsException");
        } catch (InsufficientFundsException e) {
            assertTrue(e.getMessage().contains("Plafond de carte dépassé"));
        }
    }

    @Test
    public void testPaiementCarteSucces() throws Exception {
        CarteDebitImmediat carte = new CarteDebitImmediat("123", 1000.0, mockCompte);
        when(mockDao.getCarteBancaire("123")).thenReturn(carte);
        when(mockDao.getMontantTotalDepensesCarte(eq("123"), any(Date.class))).thenReturn(0.0);

        banqueManager.effectuerPaiementCarte("123", 50.0, "Achat OK");

        // Vérifie que le compte a bien été débité localement
        verify(mockCompte).debiter(50.0);
        // Vérifie la mise à jour BDD
        verify(mockDao).updateAccount(mockCompte);
        // Vérifie qu'une opération a été créée
        verify(mockDao).createOperation(any());
    }
}