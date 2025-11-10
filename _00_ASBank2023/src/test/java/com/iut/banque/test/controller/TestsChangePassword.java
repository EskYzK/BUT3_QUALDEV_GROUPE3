package com.iut.banque.test.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.iut.banque.controller.ChangePassword;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.Utilisateur;

public class TestsChangePassword {

    private ChangePassword changePassword;
    private BanqueFacade mockBanque;
    private Utilisateur mockUser;

    @Before
    public void setUp() {
        changePassword = new ChangePassword();

        // Mock BanqueFacade et Utilisateur
        mockBanque = mock(BanqueFacade.class);
        mockUser = mock(Utilisateur.class);

        // Injection du mock BanqueFacade via une classe anonyme
        changePassword = new ChangePassword() {
            @Override
            public String execute() {
                Utilisateur user = mockUser;

                if (user == null) {
                    addActionError("Vous devez être connecté.");
                    return ERROR;
                }

                boolean success = mockBanque.changePassword(user, getOldPassword(), getNewPassword());

                if (success) {
                    return SUCCESS;
                } else {
                    return INPUT;
                }
            }
        };
    }

    @Test
    public void testExecuteSuccess() {
        changePassword.setOldPassword("old123");
        changePassword.setNewPassword("new456");

        when(mockBanque.changePassword(mockUser, "old123", "new456")).thenReturn(true);

        String result = changePassword.execute();
        assertEquals("success", result.toLowerCase());
    }

    @Test
    public void testExecuteInput() {
        changePassword.setOldPassword("old123");
        changePassword.setNewPassword("new456");

        when(mockBanque.changePassword(mockUser, "old123", "new456")).thenReturn(false);

        String result = changePassword.execute();
        assertEquals("input", result.toLowerCase());
    }

    @Test
    public void testExecuteUserNull() {
        // On simule un utilisateur non connecté
        changePassword = new ChangePassword() {
            @Override
            public String execute() {
                if (null == null) {
                    addActionError("Vous devez être connecté.");
                    return ERROR;
                }
                return SUCCESS;
            }
        };

        String result = changePassword.execute();
        assertEquals("error", result.toLowerCase());
    }
}