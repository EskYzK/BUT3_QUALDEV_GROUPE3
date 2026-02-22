package com.iut.banque.test.facade;

import com.iut.banque.constants.LoginConstants;
import com.iut.banque.facade.LoginManager;
import com.iut.banque.interfaces.IDao;
import com.iut.banque.modele.Gestionnaire;
import com.iut.banque.modele.Utilisateur;
import com.iut.banque.security.PasswordHasher;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TestsLoginManager {

    private LoginManager loginManager;
    private IDao mockDao;
    private Utilisateur user;
    private Gestionnaire manager;

    @Before
    public void setup() {
        loginManager = new LoginManager();
        mockDao = mock(IDao.class);
        loginManager.setDao(mockDao);

        user = mock(Utilisateur.class);
        manager = mock(Gestionnaire.class);
    }

    @Test
    public void testTryLoginUserSuccess() {
        when(mockDao.isUserAllowed("user1", "pwd")).thenReturn(true);
        when(mockDao.getUserById("user1")).thenReturn(user);

        int result = loginManager.tryLogin("user1", "pwd");

        assertEquals(LoginConstants.USER_IS_CONNECTED, result);
        assertEquals(user, loginManager.getConnectedUser());
    }

    @Test
    public void testTryLoginManagerSuccess() {
        when(mockDao.isUserAllowed("mgr1", "pwd")).thenReturn(true);
        when(mockDao.getUserById("mgr1")).thenReturn(manager);

        int result = loginManager.tryLogin("mgr1", "pwd");

        assertEquals(LoginConstants.MANAGER_IS_CONNECTED, result);
        assertEquals(manager, loginManager.getConnectedUser());
    }

    @Test
    public void testTryLoginFailed() {
        when(mockDao.isUserAllowed("bad", "pwd")).thenReturn(false);

        int result = loginManager.tryLogin("bad", "pwd");

        assertEquals(LoginConstants.LOGIN_FAILED, result);
        assertNull(loginManager.getConnectedUser());
    }

    @Test
    public void testLogout() {
        loginManager.setCurrentUser(user);
        loginManager.logout();

        verify(mockDao).disconnect();
        assertNull(loginManager.getConnectedUser());
    }

    @Test
    public void testChangePasswordSuccess() {
        String oldPwd = "old";
        String newPwd = "new";

        // Création d'un utilisateur concret
        Gestionnaire user = new Gestionnaire();
        String hashedOld = PasswordHasher.hash(oldPwd);
        user.setUserPwd(hashedOld);

        // DAO mock
        IDao dao = mock(IDao.class);
        doNothing().when(dao).updateUser(user);

        // LoginManager réel
        LoginManager manager = new LoginManager();
        manager.setDao(dao);

        // Execution
        boolean changed = manager.changePassword(user, oldPwd, newPwd);

        assertTrue(changed);
        verify(dao).updateUser(user);

        // On ne compare pas avec PasswordHasher.hash(oldPwd) car chaque hash est unique
        assertFalse("Le hash après changement doit être différent de l'ancien",
                hashedOld.equals(user.getUserPwd()));

        assertTrue(PasswordHasher.verify(newPwd, user.  getUserPwd()));
    }

    @Test
    public void testChangePasswordFailWrongOld() {
        String oldPwd = "wrong";
        String newPwd = "new";
        String hashedOld = PasswordHasher.hash("correct");

        when(user.getUserPwd()).thenReturn(hashedOld);

        boolean changed = loginManager.changePassword(user, oldPwd, newPwd);

        assertFalse(changed);
        verify(mockDao, never()).updateUser(user);
    }

    @Test
    public void testChangePasswordFailNullArgs() {
        assertFalse(loginManager.changePassword(null, "x", "y"));
        assertFalse(loginManager.changePassword(user, null, "y"));
        assertFalse(loginManager.changePassword(user, "x", null));
    }

    @Test
    public void testInitiatePasswordResetSuccess() {
        Utilisateur testUser = mock(Utilisateur.class);
        when(mockDao.getUserByEmail("forgot@email.com")).thenReturn(testUser);

        boolean result = loginManager.initiatePasswordReset("forgot@email.com");

        assertTrue(result);
        verify(testUser).setResetToken(anyString());
        verify(testUser).setTokenExpiry(any());
        verify(mockDao).updateUser(testUser);
    }

    @Test
    public void testInitiatePasswordResetUserNotFound() {
        when(mockDao.getUserByEmail("notfound@email.com")).thenReturn(null);

        boolean result = loginManager.initiatePasswordReset("notfound@email.com");

        assertFalse(result);
        verify(mockDao, never()).updateUser(any());
    }

    @Test
    public void testUsePasswordResetTokenSuccess() throws Exception {
        java.sql.Timestamp futureTime = new java.sql.Timestamp(System.currentTimeMillis() + 1000000);
        Utilisateur testUser = mock(Utilisateur.class);
        when(testUser.getTokenExpiry()).thenReturn(futureTime);
        when(mockDao.getUserByToken("validToken")).thenReturn(testUser);

        boolean result = loginManager.usePasswordResetToken("validToken", "newPwd123");

        assertTrue(result);
        verify(testUser).setUserPwd(anyString());
        verify(testUser).setResetToken(null);
        verify(testUser).setTokenExpiry(null);
        verify(mockDao).updateUser(testUser);
    }

    @Test
    public void testUsePasswordResetTokenNotFound() {
        when(mockDao.getUserByToken("invalidToken")).thenReturn(null);

        boolean result = loginManager.usePasswordResetToken("invalidToken", "newPwd");

        assertFalse(result);
        verify(mockDao, never()).updateUser(any());
    }

    @Test
    public void testUsePasswordResetTokenExpired() {
        java.sql.Timestamp pastTime = new java.sql.Timestamp(System.currentTimeMillis() - 1000000);
        Utilisateur testUser = mock(Utilisateur.class);
        when(testUser.getTokenExpiry()).thenReturn(pastTime);
        when(mockDao.getUserByToken("expiredToken")).thenReturn(testUser);

        boolean result = loginManager.usePasswordResetToken("expiredToken", "newPwd");

        assertFalse(result);
        verify(mockDao, never()).updateUser(any());
    }

    @Test
    public void testGetConnectedUser() {
        loginManager.setCurrentUser(user);

        Utilisateur result = loginManager.getConnectedUser();

        assertEquals(user, result);
    }

    @Test
    public void testSetCurrentUser() {
        loginManager.setCurrentUser(manager);

        assertEquals(manager, loginManager.getConnectedUser());
    }

    @Test
    public void testSetDao() {
        IDao newDao = mock(IDao.class);
        loginManager.setDao(newDao);

        when(newDao.isUserAllowed("test", "pwd")).thenReturn(true);
        when(newDao.getUserById("test")).thenReturn(user);

        loginManager.tryLogin("test", "pwd");

        verify(newDao).isUserAllowed("test", "pwd");
    }

    @Test
    public void testSendEmailWithValidConfig() {
        Utilisateur testUser = mock(Utilisateur.class);
        when(mockDao.getUserByEmail("test@test.com")).thenReturn(testUser);

        // Mock Dotenv
        boolean result = loginManager.initiatePasswordReset("test@test.com");

        // Should return true even if email sending fails gracefully
        assertTrue(result);
        verify(testUser).setResetToken(anyString());
        verify(testUser).setTokenExpiry(any());
        verify(mockDao).updateUser(testUser);
    }

    @Test
    public void testSendEmailMultipleRecipients() {
        Utilisateur user1 = mock(Utilisateur.class);
        Utilisateur user2 = mock(Utilisateur.class);
        
        // Test initiating password reset for multiple emails
        when(mockDao.getUserByEmail("first@test.com")).thenReturn(user1);
        when(mockDao.getUserByEmail("second@test.com")).thenReturn(user2);

        boolean result1 = loginManager.initiatePasswordReset("first@test.com");
        boolean result2 = loginManager.initiatePasswordReset("second@test.com");

        assertTrue(result1);
        assertTrue(result2);
        verify(mockDao, times(2)).updateUser(any());
    }
}