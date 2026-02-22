package com.iut.banque.test.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.iut.banque.controller.CreerUtilisateur;
import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.exceptions.TechnicalException;
import com.iut.banque.facade.BanqueFacade;

@ExtendWith(MockitoExtension.class)
class CreerUtilisateurTest {

	private CreerUtilisateur controller;

	@Mock
	private BanqueFacade banqueMock;

	@BeforeEach
	void setUp() {
		controller = new CreerUtilisateur();
		controller.setBanque(banqueMock);
	}

	/**
	 * Test création d'un client avec succès
	 */
	@Test
	void testCreationClientSuccess() throws Exception {
        String success="SUCCESS";
        String userId="j.dupont1";
		controller.setClient(true);
		controller.setUserId(userId);
		controller.setUserPwd("password123");
		controller.setNom("Dupont");
		controller.setPrenom("Jean");
		controller.setAdresse("123 Rue de la Paix");
		controller.setMale(true);
		controller.setEmail("jean.dupont@iut.fr");
		controller.setNumClient("1234567890");

		String result = controller.creationUtilisateur();

		assertEquals(success, result);
		assertEquals(success, controller.getResult());
		assertTrue(controller.getMessage().contains(userId));
		verify(banqueMock).createClient(userId, "password123", "Dupont", "Jean", "123 Rue de la Paix",
			true, "jean.dupont@iut.fr", "1234567890");
	}

	/**
	 * Test création d'un client sans email
	 */
	@Test
	void testCreationClientSuccessNoEmail() throws Exception {
		controller.setClient(true);
        String success="SUCCESS";
		controller.setUserId("j.martin");
		controller.setUserPwd("pass");
		controller.setNom("Martin");
		controller.setPrenom("Julie");
		controller.setAdresse("456 Avenue Paul");
		controller.setMale(false);
		controller.setEmail(null);
		controller.setNumClient("0987654321");

		String result = controller.creationUtilisateur();

		assertEquals(success, result);
		assertEquals(success, controller.getResult());
		verify(banqueMock).createClient("j.martin", "pass", "Martin", "Julie", "456 Avenue Paul", 
			false, null, "0987654321");
	}

	/**
	 * Test création d'un manager avec succès
	 */
	@Test
	void testCreationManagerSuccess() throws Exception {
        String userId="m.admin";
        String prenom="Manager";
        String success="SUCCESS";
		controller.setClient(false);
		controller.setUserId(userId);
		controller.setUserPwd("secure123");
		controller.setNom("Admin");
		controller.setPrenom(prenom);
		controller.setAdresse("789 Rue Centrale");
		controller.setMale(true);
		controller.setEmail("admin@iut.fr");

		String result = controller.creationUtilisateur();

		assertEquals(success, result);
		assertEquals(success, controller.getResult());
		assertTrue(controller.getMessage().contains(userId));
		verify(banqueMock).createManager(userId, "secure123", "Admin", prenom, "789 Rue Centrale",
			true, "admin@iut.fr");
	}

	/**
	 * Test email invalide (regex fail)
	 */
	@Test
	void testCreationUtilisateurInvalidEmail() {
        String error="ERROR";
		controller.setClient(true);
		controller.setUserId("j.test");
		controller.setUserPwd("pass");
		controller.setNom("Test");
		controller.setPrenom("Jean");
		controller.setAdresse("Test Addr");
		controller.setMale(true);
		controller.setEmail("pas-un-email-valide");
		controller.setNumClient("1111111111");

		String result = controller.creationUtilisateur();

		assertEquals(error, result);
		assertEquals(error, controller.getResult());
		assertEquals("Le format de l'adresse email est incorrect.", controller.getMessage());
		verifyNoInteractions(banqueMock);
	}

	/**
	 * Test email vide (n'est pas considéré comme erreur)
	 */
	@Test
	void testCreationUtilisateurEmptyEmail() throws Exception {
		controller.setClient(true);
        String success="SUCCESS";
		controller.setUserId("j.empty");
		controller.setUserPwd("pass");
		controller.setNom("Empty");
		controller.setPrenom("Email");
		controller.setAdresse("Addr");
		controller.setMale(false);
		controller.setEmail("   ");
		controller.setNumClient("2222222222");

		String result = controller.creationUtilisateur();

		assertEquals(success, result);
		verify(banqueMock).createClient("j.empty", "pass", "Empty", "Email", "Addr", 
			false, "   ", "2222222222");
	}

	/**
	 * Test UserID déjà existant - IllegalOperationException
	 */
	@Test
	void testCreationUtilisateurUserIdAlreadyExists() throws Exception {
        String error="ERROR";
		controller.setClient(true);
		controller.setUserId("existing.user");
		controller.setUserPwd("pass");
		controller.setNom("X");
		controller.setPrenom("Y");
		controller.setAdresse("Z");
		controller.setMale(true);
		controller.setEmail("x@test.fr");
		controller.setNumClient("3333333333");

		doThrow(new IllegalOperationException("L'ID existe déjà"))
			.when(banqueMock).createClient(anyString(), anyString(), anyString(), anyString(), 
				anyString(), anyBoolean(), anyString(), anyString());

		String result = controller.creationUtilisateur();

		assertEquals(error, result);
		assertEquals(error, controller.getResult());
		assertEquals("L'identifiant à déjà été assigné à un autre utilisateur de la banque.", 
			controller.getMessage());
	}

	/**
	 * Test NumClient déjà assigné - TechnicalException
	 */
	@Test
	void testCreationUtilisateurNumClientAlreadyAssigned() throws Exception {
        String error="ERROR";
		controller.setClient(true);
		controller.setUserId("new.user");
		controller.setUserPwd("pass");
		controller.setNom("New");
		controller.setPrenom("User");
		controller.setAdresse("Addr");
		controller.setMale(true);
		controller.setEmail("new@test.fr");
		controller.setNumClient("existing");

		doThrow(new TechnicalException("NumClient déjà assigné"))
			.when(banqueMock).createClient(anyString(), anyString(), anyString(), anyString(), 
				anyString(), anyBoolean(), anyString(), anyString());

		String result = controller.creationUtilisateur();

		assertEquals(error, result);
		assertEquals(error, controller.getResult());
		assertEquals("Le numéro de client est déjà assigné à un autre client.", 
			controller.getMessage());
	}

	/**
	 * Test format UserID invalide - IllegalArgumentException
	 */
	@Test
	void testCreationUtilisateurInvalidUserIdFormat() throws Exception {
        String error="ERROR";
        String mail="test1@iut.fr";
		controller.setClient(true);
		controller.setUserId("invalid!!!!");
		controller.setUserPwd("pass");
		controller.setNom("Test");
		controller.setPrenom("Format");
		controller.setAdresse("Addr");
		controller.setMale(true);
		controller.setEmail(mail);
		controller.setNumClient("4444444444");

		doThrow(new IllegalArgumentException("Format invalide"))
			.when(banqueMock).createClient(anyString(), anyString(), anyString(), anyString(), 
				anyString(), anyBoolean(), anyString(), anyString());

		String result = controller.creationUtilisateur();

		assertEquals(error, result);
		assertEquals(error, controller.getResult());
		assertEquals("Le format de l'identifiant est incorrect.", controller.getMessage());
	}

	/**
	 * Test format NumClient invalide - IllegalFormatException (client)
	 */
	@Test
	void testCreationUtilisateurInvalidNumClientFormat() throws Exception {
        String error="ERROR";
        String mail="test2@iut.fr";
		controller.setClient(true);
		controller.setUserId("j.correct");
		controller.setUserPwd("pass");
		controller.setNom("Test");
		controller.setPrenom("NumClient");
		controller.setAdresse("Addr");
		controller.setMale(true);
		controller.setEmail(mail);
		controller.setNumClient("123");

		doThrow(new IllegalFormatException("Format incorrect"))
			.when(banqueMock).createClient(anyString(), anyString(), anyString(), anyString(), 
				anyString(), anyBoolean(), anyString(), anyString());

		String result = controller.creationUtilisateur();

		assertEquals(error, result);
		assertEquals(error, controller.getResult());
		assertEquals("Le numéro de client est incorrect (doit contenir 10 chiffres).", 
			controller.getMessage());
	}

	/**
	 * Test format UserID invalide - IllegalFormatException (manager)
	 */
	@Test
	void testCreationUtilisateurInvalidUserIdFormatManager() throws Exception {
        String mail="test3@iut.fr";
        String error="ERROR";
		controller.setClient(false);
		controller.setUserId("invalid-format");
		controller.setUserPwd("pass");
		controller.setNom("Test");
		controller.setPrenom("Manager");
		controller.setAdresse("Addr");
		controller.setMale(true);
		controller.setEmail(mail);

		doThrow(new IllegalFormatException("Format invalide"))
			.when(banqueMock).createManager(anyString(), anyString(), anyString(), anyString(), 
				anyString(), anyBoolean(), anyString());

		String result = controller.creationUtilisateur();

		assertEquals(error, result);
		assertEquals(error, controller.getResult());
		assertEquals("L'identifiant utilisateur est incorrect (ex: j.dupont1).", 
			controller.getMessage());
	}

	/**
	 * Test email valide avec plusieurs formats
	 */
	@Test
	void testCreationUtilisateurValidEmailFormats() throws Exception {
		// Format 1: simple@domain.com
		controller.setClient(true);
		controller.setUserId("u1");
		controller.setUserPwd("p");
		controller.setNom("N");
		controller.setPrenom("P");
		controller.setAdresse("A");
		controller.setMale(true);
		controller.setEmail("test@domain.com");
		controller.setNumClient("5555555555");

		String result = controller.creationUtilisateur();

		assertEquals("SUCCESS", result);
		verify(banqueMock).createClient("u1", "p", "N", "P", "A", true, "test@domain.com", "5555555555");

		reset(banqueMock);
		controller = new CreerUtilisateur();
		controller.setBanque(banqueMock);

		// Format 2: with dots and hyphens
		controller.setClient(true);
		controller.setUserId("u2");
		controller.setUserPwd("p");
		controller.setNom("N");
		controller.setPrenom("P");
		controller.setAdresse("A");
		controller.setMale(true);
		controller.setEmail("test-user.name@sub-domain.co.uk");
		controller.setNumClient("6666666666");

		result = controller.creationUtilisateur();

		assertEquals("SUCCESS", result);
		verify(banqueMock).createClient("u2", "p", "N", "P", "A", true, "test-user.name@sub-domain.co.uk", "6666666666");
	}

	/**
	 * Test getters/setters
	 */
	@Test
	void testSettersGettersUserId() {
		controller.setUserId("test.id");
		assertEquals("test.id", controller.getUserId());
	}

	/**
	 * Test getters/setters Nom
	 */
	@Test
	void testSettersGettersNom() {
		controller.setNom("TestNom");
		assertEquals("TestNom", controller.getNom());
	}

	/**
	 * Test getters/setters Prenom
	 */
	@Test
	void testSettersGettersPrenom() {
		controller.setPrenom("TestPrenom");
		assertEquals("TestPrenom", controller.getPrenom());
	}

	/**
	 * Test getters/setters Adresse
	 */
	@Test
	void testSettersGettersAdresse() {
		controller.setAdresse("123 Test Street");
		assertEquals("123 Test Street", controller.getAdresse());
	}

	/**
	 * Test getters/setters UserPwd
	 */
	@Test
	void testSettersGettersUserPwd() {
		controller.setUserPwd("secret");
		assertEquals("secret", controller.getUserPwd());
	}

	/**
	 * Test getters/setters Male
	 */
	@Test
	void testSettersGettersMale() {
		controller.setMale(true);
		assertTrue(controller.isMale());
		controller.setMale(false);
		assertFalse(controller.isMale());
	}

	/**
	 * Test getters/setters Client
	 */
	@Test
	void testSettersGettersClient() {
		controller.setClient(true);
		assertTrue(controller.isClient());
		controller.setClient(false);
		assertFalse(controller.isClient());
	}

	/**
	 * Test getters/setters NumClient
	 */
	@Test
	void testSettersGettersNumClient() {
		controller.setNumClient("9876543210");
		assertEquals("9876543210", controller.getNumClient());
	}

	/**
	 * Test getters/setters Email
	 */
	@Test
	void testSettersGettersEmail() {
		controller.setEmail("test@example.com");
		assertEquals("test@example.com", controller.getEmail());
	}

	/**
	 * Test getters/setters Message
	 */
	@Test
	void testSettersGettersMessage() {
		controller.setMessage("Test message");
		assertEquals("Test message", controller.getMessage());
	}

	/**
	 * Test getters/setters Result
	 */
	@Test
	void testSettersGettersResult() {
		controller.setResult("SUCCESS");
		assertEquals("SUCCESS", controller.getResult());
	}

	/**
	 * Test injection du setter BanqueFacade
	 */
	@Test
	void testSetterBanque() {
		CreerUtilisateur newController = new CreerUtilisateur();
		newController.setBanque(banqueMock);
		assertNotNull(banqueMock);
	}
}
