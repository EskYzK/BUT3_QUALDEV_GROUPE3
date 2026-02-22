package com.iut.banque.test.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.iut.banque.controller.ListeCompteManager;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.exceptions.TechnicalException;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;

@ExtendWith(MockitoExtension.class)
class ListeCompteManagerTest {

	private ListeCompteManager controller;

	@Mock
	private BanqueFacade banqueMock;
	@Mock
	private Client clientMock;
	@Mock
	private Compte compteMock;

	@BeforeEach
	void setUp() {
		controller = new ListeCompteManager();
		controller.setBanque(banqueMock);
	}

	/**
	 * Test getAllClients avec succès
	 */
	@Test
	void testGetAllClients_Success() {
		Map<String, Client> expectedClients = new HashMap<>();
		expectedClients.put("client1", clientMock);
		
		when(banqueMock.getAllClients()).thenReturn(expectedClients);

		Map<String, Client> result = controller.getAllClients();

		assertEquals(expectedClients, result);
		assertEquals(1, result.size());
		verify(banqueMock).loadClients();
		verify(banqueMock).getAllClients();
	}

	/**
	 * Test getAllClients avec plusieurs clients
	 */
	@Test
	void testGetAllClients_Multiple() {
		Map<String, Client> expectedClients = new HashMap<>();
		Client client2 = mock(Client.class);
		Client client3 = mock(Client.class);
		
		expectedClients.put("client1", clientMock);
		expectedClients.put("client2", client2);
		expectedClients.put("client3", client3);
		
		when(banqueMock.getAllClients()).thenReturn(expectedClients);

		Map<String, Client> result = controller.getAllClients();

		assertEquals(3, result.size());
		verify(banqueMock).loadClients();
	}

	/**
	 * Test getAllClients avec liste vide
	 */
	@Test
	void testGetAllClients_Empty() {
		Map<String, Client> expectedClients = new HashMap<>();
		when(banqueMock.getAllClients()).thenReturn(expectedClients);

		Map<String, Client> result = controller.getAllClients();

		assertTrue(result.isEmpty());
		verify(banqueMock).loadClients();
	}

	/**
	 * Test supprimer un utilisateur avec succès
	 */
	@Test
	void testDeleteUser_Success() throws Exception {
		controller.setClient(clientMock);
		when(clientMock.getIdentity()).thenReturn("Jean Dupont");

		String result = controller.deleteUser();

		assertEquals("SUCCESS", result);
		assertEquals("Jean Dupont", controller.getUserInfo());
		verify(banqueMock).deleteUser(clientMock);
	}

	/**
	 * Test supprimer un utilisateur - compte non vide (IllegalOperationException)
	 */
	@Test
	void testDeleteUser_NonEmptyAccount() throws Exception {
		controller.setClient(clientMock);
		when(clientMock.getIdentity()).thenReturn("Marie Martin");
		
		doThrow(new IllegalOperationException()).when(banqueMock).deleteUser(any());

		String result = controller.deleteUser();

		assertEquals("NONEMPTYACCOUNT", result);
		assertEquals("Marie Martin", controller.getUserInfo());
	}

	/**
	 * Test supprimer un utilisateur - erreur technique (TechnicalException)
	 */
	@Test
	void testDeleteUser_TechnicalError() throws Exception {
		controller.setClient(clientMock);
		when(clientMock.getIdentity()).thenReturn("Pierre Bernard");
		
		doThrow(new TechnicalException()).when(banqueMock).deleteUser(any());

		String result = controller.deleteUser();

		assertEquals("ERROR", result);
		assertEquals("Pierre Bernard", controller.getUserInfo());
	}

	/**
	 * Test supprimer un compte avec succès
	 */
	@Test
	void testDeleteAccount_Success() throws Exception {
		controller.setCompte(compteMock);
		when(compteMock.getNumeroCompte()).thenReturn("FR76123");

		String result = controller.deleteAccount();

		assertEquals("SUCCESS", result);
		assertEquals("FR76123", controller.getCompteInfo());
		verify(banqueMock).deleteAccount(compteMock);
	}

	/**
	 * Test supprimer un compte - compte non vide (IllegalOperationException)
	 */
	@Test
	void testDeleteAccount_NonEmpty() throws Exception {
		controller.setCompte(compteMock);
		when(compteMock.getNumeroCompte()).thenReturn("FR99456");
		
		doThrow(new IllegalOperationException()).when(banqueMock).deleteAccount(any());

		String result = controller.deleteAccount();

		assertEquals("NONEMPTYACCOUNT", result);
		assertEquals("FR99456", controller.getCompteInfo());
	}

	/**
	 * Test supprimer un compte - erreur technique (TechnicalException)
	 */
	@Test
	void testDeleteAccount_TechnicalError() throws Exception {
		controller.setCompte(compteMock);
		when(compteMock.getNumeroCompte()).thenReturn("FR12789");
		
		doThrow(new TechnicalException()).when(banqueMock).deleteAccount(any());

		String result = controller.deleteAccount();

		assertEquals("ERROR", result);
		assertEquals("FR12789", controller.getCompteInfo());
	}

	/**
	 * Test setter/getter aDecouvert (propriété booléenne)
	 */
	@Test
	void testSetterGetter_aDecouvert() {
		assertFalse(controller.isaDecouvert());
		
		controller.setaDecouvert(true);
		assertTrue(controller.isaDecouvert());
		
		controller.setaDecouvert(false);
		assertFalse(controller.isaDecouvert());
	}

	/**
	 * Test setter/getter Compte
	 */
	@Test
	void testSetterGetter_Compte() {
		Compte otherCompte = mock(Compte.class);
		controller.setCompte(otherCompte);
		assertEquals(otherCompte, controller.getCompte());
	}

	/**
	 * Test setter/getter Client
	 */
	@Test
	void testSetterGetter_Client() {
		Client otherClient = mock(Client.class);
		controller.setClient(otherClient);
		assertEquals(otherClient, controller.getClient());
	}

	/**
	 * Test setter/getter UserInfo (via deleteUser)
	 */
	@Test
	void testSetterGetter_UserInfo() throws Exception {
		assertNull(controller.getUserInfo());
		
		controller.setClient(clientMock);
		when(clientMock.getIdentity()).thenReturn("Test User");
		
		controller.deleteUser();
		
		assertEquals("Test User", controller.getUserInfo());
	}

	/**
	 * Test setter/getter CompteInfo (via deleteAccount)
	 */
	@Test
	void testSetterGetter_CompteInfo() throws Exception {
		assertNull(controller.getCompteInfo());
		
		controller.setCompte(compteMock);
		when(compteMock.getNumeroCompte()).thenReturn("FR11111");
		
		controller.deleteAccount();
		
		assertEquals("FR11111", controller.getCompteInfo());
	}

	/**
	 * Test setter BanqueFacade
	 */
	@Test
	void testSetterBanque() {
		ListeCompteManager newController = new ListeCompteManager();
		BanqueFacade newBanqueMock = mock(BanqueFacade.class);
		newController.setBanque(newBanqueMock);
		assertNotNull(newBanqueMock);
	}

	/**
	 * Test loadClients est appelée avant getAllClients
	 */
	@Test
	void testLoadClientsCalledBeforeGetAllClients() {
		Map<String, Client> expectedClients = new HashMap<>();
		when(banqueMock.getAllClients()).thenReturn(expectedClients);
		
		controller.getAllClients();
		
		// Vérifie l'ordre: loadClients doit être appelé en premier
		InOrder inOrder = inOrder(banqueMock);
		inOrder.verify(banqueMock).loadClients();
		inOrder.verify(banqueMock).getAllClients();
	}

	/**
	 * Test deleteUser avec plusieurs appels successifs
	 */
	@Test
	void testDeleteUser_MultipleSuccessful() throws Exception {
		Client client1 = mock(Client.class);
		Client client2 = mock(Client.class);
		
		when(client1.getIdentity()).thenReturn("User 1");
		when(client2.getIdentity()).thenReturn("User 2");
		
		// Premier appel
		controller.setClient(client1);
		String result1 = controller.deleteUser();
		assertEquals("SUCCESS", result1);
		assertEquals("User 1", controller.getUserInfo());
		
		// Deuxième appel
		controller.setClient(client2);
		String result2 = controller.deleteUser();
		assertEquals("SUCCESS", result2);
		assertEquals("User 2", controller.getUserInfo());
		
		verify(banqueMock, times(2)).deleteUser(any());
	}

	/**
	 * Test deleteAccount avec plusieurs appels successifs
	 */
	@Test
	void testDeleteAccount_MultipleSuccessful() throws Exception {
		Compte compte1 = mock(Compte.class);
		Compte compte2 = mock(Compte.class);
		
		when(compte1.getNumeroCompte()).thenReturn("FR00001");
		when(compte2.getNumeroCompte()).thenReturn("FR00002");
		
		// Premier appel
		controller.setCompte(compte1);
		String result1 = controller.deleteAccount();
		assertEquals("SUCCESS", result1);
		assertEquals("FR00001", controller.getCompteInfo());
		
		// Deuxième appel
		controller.setCompte(compte2);
		String result2 = controller.deleteAccount();
		assertEquals("SUCCESS", result2);
		assertEquals("FR00002", controller.getCompteInfo());
		
		verify(banqueMock, times(2)).deleteAccount(any());
	}

	/**
	 * Test deleteUser capture l'identité même en cas d'erreur
	 */
	@Test
	void testDeleteUser_CapturesIdentityOnError() throws Exception {
		controller.setClient(clientMock);
		when(clientMock.getIdentity()).thenReturn("Error User");
		
		doThrow(new TechnicalException()).when(banqueMock).deleteUser(any());
		
		String result = controller.deleteUser();
		
		assertEquals("ERROR", result);
		assertEquals("Error User", controller.getUserInfo());
	}

	/**
	 * Test deleteAccount capture le numéro même en cas d'erreur
	 */
	@Test
	void testDeleteAccount_CapturesNumberOnError() throws Exception {
		controller.setCompte(compteMock);
		when(compteMock.getNumeroCompte()).thenReturn("FR99999");
		
		doThrow(new IllegalOperationException()).when(banqueMock).deleteAccount(any());
		
		String result = controller.deleteAccount();
		
		assertEquals("NONEMPTYACCOUNT", result);
		assertEquals("FR99999", controller.getCompteInfo());
	}

	/**
	 * Test getCompte retourne null par défaut
	 */
	@Test
	void testGetCompte_DefaultNull() {
		assertNull(controller.getCompte());
	}

	/**
	 * Test getClient retourne null par défaut
	 */
	@Test
	void testGetClient_DefaultNull() {
		assertNull(controller.getClient());
	}

	/**
	 * Test isaDecouvert retourne false par défaut
	 */
	@Test
	void testIsADecouvert_DefaultFalse() {
		assertFalse(controller.isaDecouvert());
	}

	/**
	 * Test getUserInfo retourne null par défaut
	 */
	@Test
	void testGetUserInfo_DefaultNull() {
		assertNull(controller.getUserInfo());
	}

	/**
	 * Test getCompteInfo retourne null par défaut
	 */
	@Test
	void testGetCompteInfo_DefaultNull() {
		assertNull(controller.getCompteInfo());
	}
}
