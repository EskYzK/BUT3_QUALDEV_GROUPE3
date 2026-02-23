package com.iut.banque.test.facade;

import com.iut.banque.constants.LoginConstants;
import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.facade.BanqueManager;
import com.iut.banque.facade.LoginManager;
import com.iut.banque.modele.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TestsBanqueFacade {

    private BanqueManager mockBanqueManager;
    private LoginManager mockLoginManager;
    private BanqueFacade banqueFacade;

    private Client mockClient;
    private Gestionnaire mockGestionnaire;
    private Compte mockCompte;

    @Before
    public void setUp() {
        mockBanqueManager = mock(BanqueManager.class);
        mockLoginManager = mock(LoginManager.class);
        banqueFacade = new BanqueFacade(mockLoginManager, mockBanqueManager);

        mockClient = mock(Client.class);
        mockGestionnaire = mock(Gestionnaire.class);
        mockCompte = mock(Compte.class);
    }

    @Test
    public void testTryLogin_ManagerLoadsClients() {
        when(mockLoginManager.tryLogin("admin", "1234")).thenReturn(LoginConstants.MANAGER_IS_CONNECTED);

        int result = banqueFacade.tryLogin("admin", "1234");

        assertEquals(LoginConstants.MANAGER_IS_CONNECTED, result);
        verify(mockBanqueManager).loadAllClients();
    }

    @Test
    public void testTryLogin_UserDoesNotLoadClients() {
        when(mockLoginManager.tryLogin("user", "1234")).thenReturn(LoginConstants.USER_IS_CONNECTED);

        int result = banqueFacade.tryLogin("user", "1234");

        assertEquals(LoginConstants.USER_IS_CONNECTED, result);
        verify(mockBanqueManager, never()).loadAllClients();
    }

    @Test
    public void testCreditCallsBanqueManager() throws IllegalFormatException {
        banqueFacade.crediter(mockCompte, 100.0);
        verify(mockBanqueManager).crediter(mockCompte, 100.0);
    }

    @Test
    public void testDebitCallsBanqueManager() throws Exception {
        banqueFacade.debiter(mockCompte, 50.0);
        verify(mockBanqueManager).debiter(mockCompte, 50.0);
    }

    @Test
    public void testGetAllClientsReturnsMap() {
        Map<String, Client> expected = new HashMap<>();
        when(mockBanqueManager.getAllClients()).thenReturn(expected);

        Map<String, Client> actual = banqueFacade.getAllClients();

        assertSame(expected, actual);
    }

    @Test
    public void testLogoutCallsLoginManager() {
        banqueFacade.logout();
        verify(mockLoginManager).logout();
    }

    @Test
    public void testCreateAccountOnlyForManager() throws Exception {
        when(mockLoginManager.getConnectedUser()).thenReturn(mockGestionnaire);

        banqueFacade.createAccount("123", mockClient);
        verify(mockBanqueManager).createAccount("123", mockClient);
    }

    @Test
    public void testCreateAccountNotForUser() throws Exception {
        when(mockLoginManager.getConnectedUser()).thenReturn(mockClient);

        banqueFacade.createAccount("123", mockClient);
        verify(mockBanqueManager, never()).createAccount(anyString(), any(Client.class));
    }

    @Test
    public void testChangePasswordDelegatesToLoginManager() {
        when(mockLoginManager.changePassword(mockClient, "old", "new")).thenReturn(true);

        boolean result = banqueFacade.changePassword(mockClient, "old", "new");

        assertTrue(result);
        verify(mockLoginManager).changePassword(mockClient, "old", "new");
    }

    // --- TESTS POUR LA CRÉATION ET GESTION DES UTILISATEURS ---

    @Test
    public void testCreateManager_AsManager() throws Exception {
        when(mockLoginManager.getConnectedUser()).thenReturn(mockGestionnaire);
        banqueFacade.createManager("m1", "pwd", "Nom", "Prenom", "Adresse", true, "mail@mail.com");
        verify(mockBanqueManager).createManager("m1", "pwd", "Nom", "Prenom", "Adresse", true, "mail@mail.com");
    }

    @Test
    public void testCreateManagerAsClient() throws Exception {
        when(mockLoginManager.getConnectedUser()).thenReturn(mockClient);
        banqueFacade.createManager("m1", "pwd", "Nom", "Prenom", "Adresse", true, "mail@mail.com");
        verify(mockBanqueManager, never()).createManager(anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean(), anyString());
    }

    @Test
    public void testDeleteUserAsManager() throws Exception {
        when(mockLoginManager.getConnectedUser()).thenReturn(mockGestionnaire);
        banqueFacade.deleteUser(mockClient);
        verify(mockBanqueManager).deleteUser(mockClient);
    }

    // --- TESTS POUR LA GESTION DES CARTES BANCAIRES ---

    @Test
    public void testCreateCarteAsManager() throws Exception {
        when(mockLoginManager.getConnectedUser()).thenReturn(mockGestionnaire);
        banqueFacade.createCarte("COMPTE1", 1000.0, "IMMEDIAT");
        verify(mockBanqueManager).creerCarte("COMPTE1", 1000.0, "IMMEDIAT");
    }

    @Test
    public void testCreateCarteAsClientRefuse() throws Exception {
        when(mockLoginManager.getConnectedUser()).thenReturn(mockClient);
        banqueFacade.createCarte("COMPTE1", 1000.0, "IMMEDIAT");
        verify(mockBanqueManager, never()).creerCarte(anyString(), anyDouble(), anyString());
    }

    @Test
    public void testBloquerCarte() throws Exception {
        // N'importe qui peut bloquer une carte (pas de vérification instanceof dans la facade)
        banqueFacade.bloquerCarte("12345678", true);
        verify(mockBanqueManager).bloquerCarte("12345678", true);
    }

    @Test
    public void testDebloquerCarteAsManager() throws Exception {
        when(mockLoginManager.getConnectedUser()).thenReturn(mockGestionnaire);
        banqueFacade.debloquerCarte("1234");
        verify(mockBanqueManager).debloquerCarte("1234");
    }

    @Test
    public void testPayerParCarte() throws Exception {
        banqueFacade.payerParCarte("1234", 50.0, "Achat Internet");
        verify(mockBanqueManager).effectuerPaiementCarte("1234", 50.0, "Achat Internet");
    }
}