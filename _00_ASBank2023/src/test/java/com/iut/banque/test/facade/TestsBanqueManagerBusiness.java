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

    // ------------------------------------------------------------------------
    // TESTS SUR LES EXCEPTIONS SILENCIEUSES (CREDITER / DEBITER)
    // ------------------------------------------------------------------------

    @Test
    public void testCrediter_ExceptionLorsDeCreationHistorique() throws Exception {
        // On simule un plantage de la base de données UNIQUEMENT lors de la création de l'historique
        doThrow(new RuntimeException("Erreur BDD simulée")).when(mockDao).createOperation(any());

        // La méthode crediter ne doit PAS planter l'application entière (le try/catch absorbe l'erreur)
        banqueManager.crediter(mockCompte, 100.0);

        // On vérifie que le compte a bien été crédité localement malgré l'erreur d'historique
        verify(mockDao).updateAccount(mockCompte);
    }

    @Test
    public void testDebiter_ExceptionLorsDeCreationHistorique() throws Exception {
        doThrow(new RuntimeException("Erreur BDD simulée")).when(mockDao).createOperation(any());

        // La méthode debiter ne doit PAS planter l'application
        banqueManager.debiter(mockCompte, 50.0);

        verify(mockDao).updateAccount(mockCompte);
    }

    // ------------------------------------------------------------------------
    // TESTS SUR LA MODIFICATION ET LE BLOCAGE DES CARTES
    // ------------------------------------------------------------------------

    @Test
    public void testChangerPlafondCarte_CarteSupprimee() {
        CarteDebitImmediat carte = new CarteDebitImmediat("123", 1000.0, mockCompte);
        carte.setSupprimee(true); // Carte définitivement bloquée
        when(mockDao.getCarteBancaire("123")).thenReturn(carte);

        try {
            banqueManager.changerPlafondCarte("123", 2000.0);
            fail("Aurait dû lever une TechnicalException");
        } catch (TechnicalException e) {
            assertEquals("Impossible de modifier une carte supprimée.", e.getMessage());
        }
    }

    @Test
    public void testBloquerCarte_Definitif() throws Exception {
        CarteDebitImmediat carte = new CarteDebitImmediat("123", 1000.0, mockCompte);
        when(mockDao.getCarteBancaire("123")).thenReturn(carte);

        // Bloque définitivement
        banqueManager.bloquerCarte("123", true);

        assertTrue(carte.isSupprimee());
        verify(mockDao).updateCarteBancaire(carte);
    }

    @Test
    public void testDebloquerCarte_Supprimee() throws Exception {
        CarteDebitImmediat carte = new CarteDebitImmediat("123", 1000.0, mockCompte);
        carte.setSupprimee(true);
        when(mockDao.getCarteBancaire("123")).thenReturn(carte);

        try {
            banqueManager.debloquerCarte("123");
            fail("Aurait dû lever une IllegalOperationException");
        } catch (IllegalOperationException e) {
            assertEquals("Impossible de débloquer une carte supprimée définitivement.", e.getMessage());
        }
    }

    // ------------------------------------------------------------------------
    // TEST DU PAIEMENT DIFFÉRÉ
    // ------------------------------------------------------------------------

    @Test
    public void testPaiementCarte_DiffereNeDebitePasLeCompte() throws Exception {
        CarteDebitDiffere carteDiff = new CarteDebitDiffere("999", 2000.0, mockCompte);
        when(mockDao.getCarteBancaire("999")).thenReturn(carteDiff);
        when(mockDao.getMontantTotalDepensesCarte(eq("999"), any(Date.class))).thenReturn(0.0);

        banqueManager.effectuerPaiementCarte("999", 150.0, "Achat TV");

        // VERIFICATION MAJEURE : On s'assure que le compte n'a JAMAIS été débité en direct !
        verify(mockCompte, never()).debiter(anyDouble());
        verify(mockDao, never()).updateAccount(mockCompte);

        // Par contre, l'opération a bien été sauvegardée dans l'historique
        verify(mockDao, times(1)).createOperation(any());
    }

    // ------------------------------------------------------------------------
    // TEST DE LA CLÔTURE MENSUELLE (BATCH)
    // ------------------------------------------------------------------------

    @Test
    public void testCloturerComptesDifferes_AvecDepenses() throws Exception {
        // Préparation d'un faux client avec un faux compte
        com.iut.banque.modele.Client mockClient = mock(com.iut.banque.modele.Client.class);
        java.util.Map<String, Compte> mapComptes = new java.util.HashMap<>();
        when(mockCompte.getNumeroCompte()).thenReturn("COMPTE_TEST");
        mapComptes.put("COMPTE_TEST", mockCompte);
        when(mockClient.getAccounts()).thenReturn(mapComptes);

        // Remplacement de la méthode getAllClients() locale (via un "spy" si nécessaire,
        // ou on mock directement le comportement de la dao pour charger les clients)
        java.util.Map<String, com.iut.banque.modele.Client> mapClients = new java.util.HashMap<>();
        mapClients.put("client1", mockClient);
        when(mockDao.getAllClients()).thenReturn(mapClients);

        // On injecte manuellement les clients dans la "Banque" interne du Manager
        banqueManager.loadAllClients();

        when(mockDao.getAccountById("COMPTE_TEST")).thenReturn(mockCompte);
        // On simule qu'il y a 350€ de dépenses différées à prélever ce mois-ci
        when(mockDao.getMontantTotalDepensesDifferees(eq("COMPTE_TEST"), any(Date.class), any(Date.class))).thenReturn(350.0);

        // Exécution du batch
        banqueManager.cloturerComptesDifferes();

        // Vérifications :
        // 1. Le compte a bien subi un "debitTechnique" forcé de 350€
        verify(mockCompte).debitTechnique(350.0);

        // 2. Le compte a été mis à jour en BDD
        verify(mockDao).updateAccount(mockCompte);

        // 3. Une opération de prélèvement ("PRLV_DIFF") a été créée en BDD
        verify(mockDao).createOperation(any());
    }
}