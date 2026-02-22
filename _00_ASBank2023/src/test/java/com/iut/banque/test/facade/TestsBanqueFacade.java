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
}