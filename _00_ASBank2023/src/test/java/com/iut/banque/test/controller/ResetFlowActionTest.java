package com.iut.banque.test.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.iut.banque.controller.ResetFlowAction;
import com.iut.banque.facade.BanqueFacade;

@ExtendWith(MockitoExtension.class)
class ResetFlowActionTest {

	private ResetFlowAction action;

	@Mock
	private BanqueFacade banqueMock;

	@BeforeEach
	void setUp() {
		action = new ResetFlowAction();
		action.setBanque(banqueMock); // Injection du mock
	}

	/**
	 * Test envoi du lien avec succès
	 */
	@Test
	void testSendLink_Success() {
		action.setEmail("test@iut.fr");
		when(banqueMock.initiatePasswordReset("test@iut.fr")).thenReturn(true);

		String result = action.sendLink();

		assertEquals("link_sent", result);
		assertEquals("Lien envoyé à l'adresse mail renseignée si elle existe.", action.getMessage());
		verify(banqueMock).initiatePasswordReset("test@iut.fr");
	}

	/**
	 * Test envoi du lien - email invalide
	 */
	@Test
	void testSendLink_InvalidEmail() {
		action.setEmail("nonexistent@unknown.fr");
		when(banqueMock.initiatePasswordReset("nonexistent@unknown.fr")).thenReturn(false);

		String result = action.sendLink();

		assertEquals("link_sent", result);
        assertEquals("Lien envoyé à l'adresse mail renseignée si elle existe.", action.getMessage());
	}

	/**
	 * Test envoi du lien - email null
	 */
	@Test
	void testSendLink_NullEmail() {
		action.setEmail(null);
		String result = action.sendLink();

		assertEquals("error", result);
        assertEquals("Veuillez renseigner une adresse email.", action.getMessage());
	}

	/**
	 * Test envoi du lien - email vide
	 */
	@Test
	void testSendLink_EmptyEmail() {
		action.setEmail("");
		String result = action.sendLink();

		assertEquals("error", result);
        assertEquals("Veuillez renseigner une adresse email.", action.getMessage());
	}

	/**
	 * Test affichage du formulaire avec un jeton valide
	 */
	@Test
	void testShowForm_WithValidToken() {
		action.setToken("valid-token-123");
		
		String result = action.showForm();
		
		assertEquals("show_form", result);
		assertFalse(action.hasActionErrors());
	}

	/**
	 * Test affichage du formulaire sans jeton
	 */
	@Test
	void testShowForm_NoToken() {
		action.setToken(null);
		
		String result = action.showForm();
		
		assertEquals("error", result);
		assertTrue(action.hasActionErrors());
	}

	/**
	 * Test affichage du formulaire avec jeton vide
	 */
	@Test
	void testShowForm_EmptyToken() {
		action.setToken("");
		
		String result = action.showForm();
		
		assertEquals("error", result);
		assertTrue(action.hasActionErrors());
	}

	/**
	 * Test affichage du formulaire avec jeton contenant des espaces
	 */
	@Test
	void testShowForm_TokenWithSpaces() {
		action.setToken("   ");
		
		String result = action.showForm();
		
		assertEquals("show_form", result);
	}

	/**
	 * Test réinitialisation du mot de passe avec succès
	 */
	@Test
	void testProcessReset_Success() {
		action.setToken("token123");
		action.setNewPassword("NewPass123!");
		when(banqueMock.usePasswordResetToken("token123", "NewPass123!")).thenReturn(true);

		String result = action.processReset();

		assertEquals("reset_success", result);
		assertFalse(action.hasActionErrors());
		verify(banqueMock).usePasswordResetToken("token123", "NewPass123!");
	}

	/**
	 * Test réinitialisation - jeton expiré
	 */
	@Test
	void testProcessReset_ExpiredToken() {
		action.setToken("expired-token");
		action.setNewPassword("NewPassword123");
		when(banqueMock.usePasswordResetToken("expired-token", "NewPassword123")).thenReturn(false);

		String result = action.processReset();

		assertEquals("show_form", result);
		assertTrue(action.hasActionErrors());
	}

	/**
	 * Test réinitialisation - jeton invalide
	 */
	@Test
	void testProcessReset_InvalidToken() {
		action.setToken("invalid-token");
		action.setNewPassword("NewPassword456");
		when(banqueMock.usePasswordResetToken("invalid-token", "NewPassword456")).thenReturn(false);

		String result = action.processReset();

		assertEquals("show_form", result);
		assertTrue(action.hasActionErrors());
	}

	/**
	 * Test réinitialisation avec mot de passe null
	 */
	@Test
	void testProcessReset_NullPassword() {
		action.setToken("token789");
		action.setNewPassword(null);
		when(banqueMock.usePasswordResetToken("token789", null)).thenReturn(false);

		String result = action.processReset();

		assertEquals("show_form", result);
		assertTrue(action.hasActionErrors());
	}

	/**
	 * Test réinitialisation avec mot de passe vide
	 */
	@Test
	void testProcessReset_EmptyPassword() {
		action.setToken("token789");
		action.setNewPassword("");
		when(banqueMock.usePasswordResetToken("token789", "")).thenReturn(false);

		String result = action.processReset();

		assertEquals("show_form", result);
		assertTrue(action.hasActionErrors());
	}

	/**
	 * Test flux complet: demande -> affichage -> soumission
	 */
	@Test
	void testCompleteFlow_Success() {
		// Étape 1: Demande de réinitialisation
		action.setEmail("user@iut.fr");
		when(banqueMock.initiatePasswordReset("user@iut.fr")).thenReturn(true);
		
		String sendResult = action.sendLink();
		assertEquals("link_sent", sendResult);
		
		// Étape 2: Affichage du formulaire avec le jeton reçu par mail
		action.setToken("received-token-from-mail");
		String showResult = action.showForm();
		assertEquals("show_form", showResult);
		
		// Étape 3: Soumission du nouveau mot de passe
		action.setNewPassword("SecureNewPass123!");
		when(banqueMock.usePasswordResetToken("received-token-from-mail", "SecureNewPass123!")).thenReturn(true);
		
		String processResult = action.processReset();
		assertEquals("reset_success", processResult);
		assertFalse(action.hasActionErrors());
	}

	/**
	 * Test flux avec jeton expiré entre affichage et soumission
	 */
	@Test
	void testCompleteFlow_ExpiredTokenDuringProcess() {
		// Étape 1: Demande
		action.setEmail("user@iut.fr");
		when(banqueMock.initiatePasswordReset("user@iut.fr")).thenReturn(true);
		action.sendLink();
		
		// Étape 2: Affichage (valide)
		action.setToken("old-token");
		String showResult = action.showForm();
		assertEquals("show_form", showResult);
		
		// Étape 3: Soumission échouée (jeton expiré)
		action.setNewPassword("NewPass999");
		when(banqueMock.usePasswordResetToken("old-token", "NewPass999")).thenReturn(false);
		
		String processResult = action.processReset();
		assertEquals("show_form", processResult);
		assertTrue(action.hasActionErrors());
	}

	/**
	 * Test setter/getter Email
	 */
	@Test
	void testSetterGetter_Email() {
		assertNull(action.getEmail());
		
		action.setEmail("test@mail.fr");
		assertEquals("test@mail.fr", action.getEmail());
	}

	/**
	 * Test setter/getter Token
	 */
	@Test
	void testSetterGetter_Token() {
		assertNull(action.getToken());
		
		action.setToken("abc123def");
		assertEquals("abc123def", action.getToken());
	}

	/**
	 * Test setter/getter NewPassword
	 */
	@Test
	void testSetterGetter_NewPassword() {
		assertNull(action.getNewPassword());
		
		action.setNewPassword("MyNewPassword");
		assertEquals("MyNewPassword", action.getNewPassword());
	}

	/**
	 * Test setter/getter Message
	 */
	@Test
	void testSetterGetter_Message() {
		assertNull(action.getMessage());
		
		action.setMessage("Mon message de confirmation");
		assertEquals("Mon message de confirmation", action.getMessage());
	}

	/**
	 * Test setter BanqueFacade
	 */
	@Test
	void testSetterBanque() {
		ResetFlowAction newAction = new ResetFlowAction();
		BanqueFacade newMock = mock(BanqueFacade.class);
		
		newAction.setBanque(newMock);
		assertNotNull(newMock);
	}

	/**
	 * Test avec plusieurs tentatives de réinitialisation
	 */
	@Test
	void testMultipleResetAttempts() {
		// Premier essai: succès
		action.setToken("token-1");
		action.setNewPassword("FirstPassword123");
		when(banqueMock.usePasswordResetToken("token-1", "FirstPassword123")).thenReturn(true);
		
		String result1 = action.processReset();
		assertEquals("reset_success", result1);
		
		// Deuxième essai: nouveau token mais échoue
		action.setToken("token-2-expired");
		action.setNewPassword("SecondPassword456");
		when(banqueMock.usePasswordResetToken("token-2-expired", "SecondPassword456")).thenReturn(false);
		
		String result2 = action.processReset();
		assertEquals("show_form", result2);
		assertTrue(action.hasActionErrors());
	}

	/**
	 * Test verify que initiatePasswordReset est bien appelé avec le bon email
	 */
	@Test
	void testSendLink_VerifyFacadeCall() {
		action.setEmail("verify@test.fr");
		when(banqueMock.initiatePasswordReset("verify@test.fr")).thenReturn(true);
		
		action.sendLink();
		
		verify(banqueMock, times(1)).initiatePasswordReset("verify@test.fr");
	}

	/**
	 * Test verify que usePasswordResetToken est bien appelé avec les bons paramètres
	 */
	@Test
	void testProcessReset_VerifyFacadeCall() {
		action.setToken("verify-token");
		action.setNewPassword("VerifyPass789");
		when(banqueMock.usePasswordResetToken("verify-token", "VerifyPass789")).thenReturn(true);
		
		action.processReset();
		
		verify(banqueMock, times(1)).usePasswordResetToken("verify-token", "VerifyPass789");
	}

	/**
	 * Test message d'erreur générique en cas d'erreur lors de l'envoi du lien
	 */
	@Test
	void testSendLink_ErrorMessage() {
        // 1. On configure le Mock pour qu'il échoue (simule erreur technique ou user introuvable)
        action.setEmail("error@test.fr");
        when(banqueMock.initiatePasswordReset("error@test.fr")).thenReturn(false);

        // 2. On exécute l'action
        String result = action.sendLink();

        // 3. VÉRIFICATIONS DE SÉCURITÉ

        // A. On vérifie qu'on retourne bien "link_sent" (le succès apparent)
        // C'est ici que vous aviez "error" avant.
        assertEquals("link_sent", result);

        // B. On vérifie qu'on affiche le message générique (et pas un message d'erreur technique)
        assertEquals("Lien envoyé à l'adresse mail renseignée si elle existe.", action.getMessage());

        // C. On vérifie qu'il n'y a PAS d'erreurs Struts (pour ne pas afficher de rouge à l'écran)
        assertFalse(action.hasActionErrors(), "Il ne doit pas y avoir d'erreurs Struts visibles");
	}

	/**
	 * Test message d'erreur lors de la soumission du nouveau mot de passe
	 */
	@Test
	void testProcessReset_ErrorMessage() {
		action.setToken("expired");
		action.setNewPassword("SomePass");
		when(banqueMock.usePasswordResetToken("expired", "SomePass")).thenReturn(false);
		
		String result = action.processReset();
		
		assertEquals("show_form", result);
		assertTrue(action.hasActionErrors());
		String errorMessage = action.getActionErrors().iterator().next();
		assertEquals("Lien invalide ou expiré.", errorMessage);
	}

	/**
	 * Test avec caractères spéciaux dans l'email
	 */
	@Test
	void testSendLink_SpecialCharactersInEmail() {
		action.setEmail("user+test@university.ac.uk");
		when(banqueMock.initiatePasswordReset("user+test@university.ac.uk")).thenReturn(true);
		
		String result = action.sendLink();
		
		assertEquals("link_sent", result);
	}

	/**
	 * Test message de succès bien défini
	 */
	@Test
	void testSendLink_SuccessMessage() {
		action.setEmail("success@test.fr");
		when(banqueMock.initiatePasswordReset("success@test.fr")).thenReturn(true);
		
		action.sendLink();
		
		assertEquals("Lien envoyé à l'adresse mail renseignée si elle existe.", action.getMessage());
	}
}
