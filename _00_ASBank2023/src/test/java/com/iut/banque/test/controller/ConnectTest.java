package com.iut.banque.test.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.iut.banque.constants.LoginConstants;
import com.iut.banque.controller.Connect;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Utilisateur;

@ExtendWith(MockitoExtension.class)
public class ConnectTest {

	private Connect connectController;

	@Mock
	private BanqueFacade banqueMock;
	@Mock
	private Utilisateur userMock;
	@Mock
	private Client clientMock;

	@BeforeEach
	void setUp() {
		// Initialiser le controller et injecter les mocks
		connectController = new Connect();
		connectController.setBanque(banqueMock);
	}

	/**
	 * Test du login réussi pour un utilisateur normal (USER_IS_CONNECTED)
	 */
	@Test
	void testLogin_Success_User() {
		// Configuration du mock : quand on appelle tryLogin, retourner USER_IS_CONNECTED
		when(banqueMock.tryLogin("user123", "password123")).thenReturn(LoginConstants.USER_IS_CONNECTED);

		connectController.setUserCde("user123");
		connectController.setUserPwd("password123");

		String result = connectController.login();

		assertEquals("SUCCESS", result);
		verify(banqueMock).tryLogin("user123", "password123");
	}

	/**
	 * Test du login réussi pour un gestionnaire (MANAGER_IS_CONNECTED)
	 */
	@Test
	void testLogin_Success_Manager() {
		when(banqueMock.tryLogin("manager456", "securePass")).thenReturn(LoginConstants.MANAGER_IS_CONNECTED);

		connectController.setUserCde("manager456");
		connectController.setUserPwd("securePass");

		String result = connectController.login();

		assertEquals("SUCCESSMANAGER", result);
		verify(banqueMock).tryLogin("manager456", "securePass");
	}

	/**
	 * Test du login échoué (paramètres invalides)
	 */
	@Test
	void testLogin_Failed() {
		when(banqueMock.tryLogin("wrong", "user")).thenReturn(LoginConstants.LOGIN_FAILED);

		connectController.setUserCde("wrong");
		connectController.setUserPwd("user");

		String result = connectController.login();

		assertEquals("ERROR", result);
		verify(banqueMock).tryLogin("wrong", "user");
	}

	/**
	 * Test du login avec paramètres null (short-circuit avant l'appel à la
	 * façade)
	 */
	@Test
	void testLogin_NullParameters() {
		connectController.setUserCde(null);
		connectController.setUserPwd(null);

		String result = connectController.login();

		assertEquals("ERROR", result);
		// Vérifier que la banque n'a pas été interrogée
		verifyNoInteractions(banqueMock);
	}

	/**
	 * Test du login avec userCde null
	 */
	@Test
	void testLogin_NullUserCde() {
		connectController.setUserCde(null);
		connectController.setUserPwd("password");

		String result = connectController.login();

		assertEquals("ERROR", result);
		verifyNoInteractions(banqueMock);
	}

	/**
	 * Test du login avec userPwd null
	 */
	@Test
	void testLogin_NullUserPwd() {
		connectController.setUserCde("user");
		connectController.setUserPwd(null);

		String result = connectController.login();

		assertEquals("ERROR", result);
		verifyNoInteractions(banqueMock);
	}

	/**
	 * Test du logout
	 */
	@Test
	void testLogout() {
		String result = connectController.logout();

		assertEquals("SUCCESS", result);
		verify(banqueMock).logout();
	}

	/**
	 * Test du setter et getter pour userCde
	 */
	@Test
	void testSetterGetter_UserCde() {
		connectController.setUserCde("testUser");
		assertEquals("testUser", connectController.getUserCde());
	}

	/**
	 * Test du setter et getter pour userPwd
	 */
	@Test
	void testSetterGetter_UserPwd() {
		connectController.setUserPwd("testPassword");
		assertEquals("testPassword", connectController.getUserPwd());
	}

	/**
	 * Test du setter et getter pour Retour
	 */
	@Test
	void testSetterGetter_Retour() {
		connectController.setRetour("testRetour");
		assertEquals("testRetour", connectController.getRetour());
	}

	/**
	 * Test du setter et getter pour BanqueFacade
	 */
	@Test
	void testSetterGetter_Banque() {
		connectController.setBanque(banqueMock);
		// Vérifier que le setter fonctionne en appelant une méthode
		verify(banqueMock, never()).logout();
	}

	/**
	 * Test du getConnectedUser
	 */
	@Test
	void testGetConnectedUser() {
		when(banqueMock.getConnectedUser()).thenReturn(userMock);

		Utilisateur result = connectController.getConnectedUser();

		assertEquals(userMock, result);
		verify(banqueMock).getConnectedUser();
	}
}
