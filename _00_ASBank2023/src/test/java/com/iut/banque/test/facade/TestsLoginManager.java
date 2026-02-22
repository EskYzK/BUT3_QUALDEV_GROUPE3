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
        String usrId="user1";
        when(mockDao.isUserAllowed(usrId, "pwd")).thenReturn(true);
        when(mockDao.getUserById(usrId)).thenReturn(user);

        int result = loginManager.tryLogin(usrId, "pwd");

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
        Gestionnaire user3 = new Gestionnaire();
        String hashedOld = PasswordHasher.hash(oldPwd);
        user3.setUserPwd(hashedOld);

        // DAO mock
        IDao dao = mock(IDao.class);
        doNothing().when(dao).updateUser(user3);

        // LoginManager réel
        LoginManager manager1 = new LoginManager();
        manager1.setDao(dao);

        // Execution
        boolean changed = manager1.changePassword(user3, oldPwd, newPwd);

        assertTrue(changed);
        verify(dao).updateUser(user3);

        // On ne compare pas avec PasswordHasher.hash(oldPwd) car chaque hash est unique
        assertFalse("Le hash après changement doit être différent de l'ancien",
                hashedOld.equals(user3.getUserPwd()));

        assertTrue(PasswordHasher.verify(newPwd, user3.  getUserPwd()));
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
}