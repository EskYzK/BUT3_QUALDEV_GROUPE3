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
    public void testCreateAccount_OnlyForManager() throws Exception {
        when(mockLoginManager.getConnectedUser()).thenReturn(mockGestionnaire);

        banqueFacade.createAccount("123", mockClient);
        verify(mockBanqueManager).createAccount("123", mockClient);
    }

    @Test
    public void testCreateAccount_NotForUser() throws Exception {
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

    @Test
    public void testTryLogin_LoginFailed() {
        when(mockLoginManager.tryLogin("bad", "bad")).thenReturn(LoginConstants.LOGIN_FAILED);

        int result = banqueFacade.tryLogin("bad", "bad");

        assertEquals(LoginConstants.LOGIN_FAILED, result);
        verify(mockBanqueManager, never()).loadAllClients();
    }

    @Test
    public void testGetConnectedUser() {
        when(mockLoginManager.getConnectedUser()).thenReturn(mockClient);

        Utilisateur result = banqueFacade.getConnectedUser();

        assertEquals(mockClient, result);
    }

    @Test
    public void testDeleteAccount_OnlyForManager() throws Exception {
        when(mockLoginManager.getConnectedUser()).thenReturn(mockGestionnaire);

        banqueFacade.deleteAccount(mockCompte);

        verify(mockBanqueManager).deleteAccount(mockCompte);
    }

    @Test
    public void testDeleteAccount_NotForUser() throws Exception {
        when(mockLoginManager.getConnectedUser()).thenReturn(mockClient);

        banqueFacade.deleteAccount(mockCompte);

        verify(mockBanqueManager, never()).deleteAccount(mockCompte);
    }

    @Test
    public void testCreateAccountWithDecouvert_OnlyForManager() throws Exception {
        when(mockLoginManager.getConnectedUser()).thenReturn(mockGestionnaire);

        banqueFacade.createAccount("456", mockClient, 1000.0);

        verify(mockBanqueManager).createAccount("456", mockClient, 1000.0);
    }

    @Test
    public void testCreateAccountWithDecouvert_NotForUser() throws Exception {
        when(mockLoginManager.getConnectedUser()).thenReturn(mockClient);

        banqueFacade.createAccount("456", mockClient, 1000.0);

        verify(mockBanqueManager, never()).createAccount(anyString(), any(Client.class), anyDouble());
    }

    @Test
    public void testDeleteUser_OnlyForManager() throws Exception {
        when(mockLoginManager.getConnectedUser()).thenReturn(mockGestionnaire);

        banqueFacade.deleteUser(mockClient);

        verify(mockBanqueManager).deleteUser(mockClient);
    }

    @Test
    public void testDeleteUser_NotForUser() throws Exception {
        when(mockLoginManager.getConnectedUser()).thenReturn(mockClient);

        banqueFacade.deleteUser(mockClient);

        verify(mockBanqueManager, never()).deleteUser(mockClient);
    }

    @Test
    public void testCreateManager_OnlyForManager() throws Exception {
        when(mockLoginManager.getConnectedUser()).thenReturn(mockGestionnaire);

        banqueFacade.createManager("newmgr", "pwd", "Dupont", "Jean", "123 rue", true, "jean@email.com");

        verify(mockBanqueManager).createManager("newmgr", "pwd", "Dupont", "Jean", "123 rue", true, "jean@email.com");
    }

    @Test
    public void testCreateClient_OnlyForManager() throws Exception {
        when(mockLoginManager.getConnectedUser()).thenReturn(mockGestionnaire);

        banqueFacade.createClient("newclient", "pwd", "Martin", "Alice", "456 ave", false, "alice@email.com", "NC001");

        verify(mockBanqueManager).createClient("newclient", "pwd", "Martin", "Alice", "456 ave", false, "alice@email.com", "NC001");
    }

    @Test
    public void testLoadClients_OnlyForManager() {
        when(mockLoginManager.getConnectedUser()).thenReturn(mockGestionnaire);

        banqueFacade.loadClients();

        verify(mockBanqueManager).loadAllClients();
    }

    @Test
    public void testLoadClients_NotForUser() {
        when(mockLoginManager.getConnectedUser()).thenReturn(mockClient);

        banqueFacade.loadClients();

        verify(mockBanqueManager, never()).loadAllClients();
    }

    @Test
    public void testGetCompte() {
        when(mockBanqueManager.getAccountById("ACC001")).thenReturn(mockCompte);

        Compte result = banqueFacade.getCompte("ACC001");

        assertEquals(mockCompte, result);
    }

    @Test
    public void testChangeDecouvert_OnlyForManager() throws Exception {
        when(mockLoginManager.getConnectedUser()).thenReturn(mockGestionnaire);

        banqueFacade.changeDecouvert(mockCompteDecouvert, 2000.0);

        verify(mockBanqueManager).changeDecouvert(mockCompteDecouvert, 2000.0);
    }

    @Test
    public void testChangeDecouvert_NotForUser() throws Exception {
        when(mockLoginManager.getConnectedUser()).thenReturn(mockClient);

        banqueFacade.changeDecouvert(mockCompteDecouvert, 2000.0);

        verify(mockBanqueManager, never()).changeDecouvert(any(), anyDouble());
    }

    @Test
    public void testInitiatePasswordReset() {
        when(mockLoginManager.initiatePasswordReset("test@email.com")).thenReturn(true);

        boolean result = banqueFacade.initiatePasswordReset("test@email.com");

        assertTrue(result);
        verify(mockLoginManager).initiatePasswordReset("test@email.com");
    }

    @Test
    public void testUsePasswordResetToken() {
        when(mockLoginManager.usePasswordResetToken("token123", "newPwd")).thenReturn(true);

        boolean result = banqueFacade.usePasswordResetToken("token123", "newPwd");

        assertTrue(result);
        verify(mockLoginManager).usePasswordResetToken("token123", "newPwd");
    }

    @Test
    public void testGetCarte() {
        CarteBancaire mockCarte = mock(CarteBancaire.class);
        when(mockBanqueManager.getCarte("CARD001")).thenReturn(mockCarte);

        CarteBancaire result = banqueFacade.getCarte("CARD001");

        assertEquals(mockCarte, result);
    }

    @Test
    public void testCreateCarte_OnlyForManager() throws Exception {
        when(mockLoginManager.getConnectedUser()).thenReturn(mockGestionnaire);

        banqueFacade.createCarte("ACC001", 5000.0, "IMMEDIAT");

        verify(mockBanqueManager).creerCarte("ACC001", 5000.0, "IMMEDIAT");
    }

    @Test
    public void testCreateCarte_NotForUser() throws Exception {
        when(mockLoginManager.getConnectedUser()).thenReturn(mockClient);

        banqueFacade.createCarte("ACC001", 5000.0, "IMMEDIAT");

        verify(mockBanqueManager, never()).creerCarte(anyString(), anyDouble(), anyString());
    }

    @Test
    public void testChangerPlafondCarte_OnlyForManager() throws Exception {
        when(mockLoginManager.getConnectedUser()).thenReturn(mockGestionnaire);

        banqueFacade.changerPlafondCarte("CARD001", 8000.0);

        verify(mockBanqueManager).changerPlafondCarte("CARD001", 8000.0);
    }

    @Test
    public void testChangerCompteLieCarte_OnlyForManager() throws Exception {
        when(mockLoginManager.getConnectedUser()).thenReturn(mockGestionnaire);

        banqueFacade.changerCompteLieCarte("CARD001", "ACC002");

        verify(mockBanqueManager).changerCompteLieCarte("CARD001", "ACC002");
    }

    @Test
    public void testBloquerCarte() throws Exception {
        when(mockLoginManager.getConnectedUser()).thenReturn(mockClient);

        banqueFacade.bloquerCarte("CARD001", false);

        verify(mockBanqueManager).bloquerCarte("CARD001", false);
    }

    @Test
    public void testBloquerCarteDefinitif() throws Exception {
        when(mockLoginManager.getConnectedUser()).thenReturn(mockGestionnaire);

        banqueFacade.bloquerCarte("CARD001", true);

        verify(mockBanqueManager).bloquerCarte("CARD001", true);
    }

    @Test
    public void testDebloquerCarte_OnlyForManager() throws Exception {
        when(mockLoginManager.getConnectedUser()).thenReturn(mockGestionnaire);

        banqueFacade.debloquerCarte("CARD001");

        verify(mockBanqueManager).debloquerCarte("CARD001");
    }

    @Test
    public void testPayerParCarte() throws Exception {
        when(mockLoginManager.getConnectedUser()).thenReturn(mockClient);

        banqueFacade.payerParCarte("CARD001", 150.0, "Achat");

        verify(mockBanqueManager).effectuerPaiementCarte("CARD001", 150.0, "Achat");
    }
}