package com.iut.banque.test.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.iut.banque.controller.ResultatSuppression;

public class ResultatSuppressionTest {

	private ResultatSuppression controller;

	@BeforeEach
	void setUp() {
		controller = new ResultatSuppression();
	}

	/**
	 * Test logique d'erreur - initialisation sans erreur
	 */
	@Test
	void testErrorLogic_InitialState() {
		assertFalse(controller.isError());
		assertNull(controller.getErrorMessage());
	}

	/**
	 * Test logique d'erreur - quand un message est défini
	 */
	@Test
	void testErrorLogic_WhenMessageIsSet() {
		controller.setErrorMessage("Le compte n'est pas vide !");
		
		assertTrue(controller.isError());
		assertEquals("Le compte n'est pas vide !", controller.getErrorMessage());
	}

	/**
	 * Test logique d'erreur - quand le message est null
	 */
	@Test
	void testErrorLogic_WhenMessageIsNull() {
		// D'abord on met un message
		controller.setErrorMessage("Erreur temporaire");
		assertTrue(controller.isError());
		
		// Puis on le réinitialise à null
		controller.setErrorMessage(null);
		assertFalse(controller.isError());
		assertNull(controller.getErrorMessage());
	}

	/**
	 * Test logique d'erreur - quand le message est vide
	 */
	@Test
	void testErrorLogic_WhenMessageIsEmpty() {
		controller.setErrorMessage("");
		assertFalse(controller.isError());
		assertEquals("", controller.getErrorMessage());
	}

	/**
	 * Test logique d'erreur - avec message long
	 */
	@Test
	void testErrorLogic_LongErrorMessage() {
		String longMessage = "Impossible de supprimer le compte car il contient des opérations en cours";
		controller.setErrorMessage(longMessage);
		
		assertTrue(controller.isError());
		assertEquals(longMessage, controller.getErrorMessage());
	}

	/**
	 * Test logique d'erreur - avec un seul espace
	 */
	@Test
	void testErrorLogic_SingleSpace() {
		controller.setErrorMessage(" ");
		
		// Un seul espace n'est pas considéré comme vide
		assertTrue(controller.isError());
		assertEquals(" ", controller.getErrorMessage());
	}

	/**
	 * Test getter/setter Error manuel (sans setErrorMessage)
	 */
	@Test
	void testSetError_Manual() {
		controller.setError(true);
		assertTrue(controller.isError());
		
		controller.setError(false);
		assertFalse(controller.isError());
	}

	/**
	 * Test getter/setter compteInfo
	 */
	@Test
	void testGettersSetters_CompteInfo() {
		assertNull(controller.getCompteInfo());
		
		controller.setCompteInfo("FR12345678");
		assertEquals("FR12345678", controller.getCompteInfo());
		
		controller.setCompteInfo("FR00012");
		assertEquals("FR00012", controller.getCompteInfo());
	}

	/**
	 * Test getter/setter userInfo
	 */
	@Test
	void testGettersSetters_UserInfo() {
		assertNull(controller.getUserInfo());
		
		controller.setUserInfo("jean.dupont");
		assertEquals("jean.dupont", controller.getUserInfo());
		
		controller.setUserInfo("marie.martin");
		assertEquals("marie.martin", controller.getUserInfo());
	}

	/**
	 * Test getter/setter isAccount
	 */
	@Test
	void testGettersSetters_IsAccount() {
		assertFalse(controller.isAccount());
		
		controller.setAccount(true);
		assertTrue(controller.isAccount());
		
		controller.setAccount(false);
		assertFalse(controller.isAccount());
	}

	/**
	 * Test getter/setter Compte (objet)
	 */
	@Test
	void testGettersSetters_Compte() {
		assertNull(controller.getCompte());
		controller.setCompte(null);
		assertNull(controller.getCompte());
	}

	/**
	 * Test getter/setter Client (objet)
	 */
	@Test
	void testGettersSetters_Client() {
		assertNull(controller.getClient());
		controller.setClient(null);
		assertNull(controller.getClient());
	}

	/**
	 * Test scénario: Suppression de compte réussie
	 */
	@Test
	void testScenario_SuccessfulAccountDeletion() {
		// Pas d'erreur lors d'une suppression réussie
		controller.setAccount(true);
		controller.setCompteInfo("FR98765432");
		controller.setErrorMessage(null);
		
		assertTrue(controller.isAccount());
		assertEquals("FR98765432", controller.getCompteInfo());
		assertFalse(controller.isError());
		assertNull(controller.getErrorMessage());
	}

	/**
	 * Test scénario: Suppression d'utilisateur réussie
	 */
	@Test
	void testScenario_SuccessfulUserDeletion() {
		controller.setAccount(false);
		controller.setUserInfo("marie.dupont");
		controller.setErrorMessage(null);
		
		assertFalse(controller.isAccount());
		assertEquals("marie.dupont", controller.getUserInfo());
		assertFalse(controller.isError());
	}

	/**
	 * Test scénario: Suppression échouée pour compte
	 */
	@Test
	void testScenario_FailedAccountDeletion() {
		controller.setAccount(true);
		controller.setCompteInfo("FR11111111");
		controller.setErrorMessage("Le compte contient de l'argent");
		
		assertTrue(controller.isAccount());
		assertEquals("FR11111111", controller.getCompteInfo());
		assertTrue(controller.isError());
		assertEquals("Le compte contient de l'argent", controller.getErrorMessage());
	}

	/**
	 * Test scénario: Suppression échouée pour utilisateur
	 */
	@Test
	void testScenario_FailedUserDeletion() {
		controller.setAccount(false);
		controller.setUserInfo("pierre.martin");
		controller.setErrorMessage("L'utilisateur possède des comptes actifs");
		
		assertFalse(controller.isAccount());
		assertEquals("pierre.martin", controller.getUserInfo());
		assertTrue(controller.isError());
		assertEquals("L'utilisateur possède des comptes actifs", controller.getErrorMessage());
	}

	/**
	 * Test enchaînement multiple de modifications
	 */
	@Test
	void testChainedModifications() {
		// Étape 1: Initialisation partiellement réussie
		controller.setAccount(true);
		controller.setCompteInfo("FR77777777");
		
		assertTrue(controller.isAccount());
		assertFalse(controller.isError());
		
		// Étape 2: Erreur détectée
		controller.setErrorMessage("Solde insuffisant");
		assertTrue(controller.isError());
		
		// Étape 3: Réinitialisation
		controller.setErrorMessage(null);
		assertFalse(controller.isError());
	}

	/**
	 * Test setErrorMessage avec des caractères spéciaux
	 */
	@Test
	void testErrorMessage_SpecialCharacters() {
		String messageWithSpecialChars = "Erreur: Caractères invalides (é, à, ç) détectés!";
		controller.setErrorMessage(messageWithSpecialChars);
		
		assertTrue(controller.isError());
		assertEquals(messageWithSpecialChars, controller.getErrorMessage());
	}

	/**
	 * Test setErrorMessage avec des guillemets
	 */
	@Test
	void testErrorMessage_WithQuotes() {
		String messageWithQuotes = "Erreur 'critique' : \"Impossible à supprimer\"";
		controller.setErrorMessage(messageWithQuotes);
		
		assertTrue(controller.isError());
		assertEquals(messageWithQuotes, controller.getErrorMessage());
	}

	/**
	 * Test setCompteInfo avec null
	 */
	@Test
	void testCompteInfo_Null() {
		controller.setCompteInfo("FR12345");
		assertEquals("FR12345", controller.getCompteInfo());
		
		controller.setCompteInfo(null);
		assertNull(controller.getCompteInfo());
	}

	/**
	 * Test setUserInfo avec null
	 */
	@Test
	void testUserInfo_Null() {
		controller.setUserInfo("username");
		assertEquals("username", controller.getUserInfo());
		
		controller.setUserInfo(null);
		assertNull(controller.getUserInfo());
	}

	/**
	 * Test getters par défaut pour les objets Compte et Client
	 */
	@Test
	void testDefaultObjectGetters() {
		assertNull(controller.getCompte());
		assertNull(controller.getClient());
	}

	/**
	 * Test indépendance des champs isAccount et error
	 */
	@Test
	void testFieldIndependence_AccountAndError() {
		// isAccount n'affecte pas error
		controller.setAccount(true);
		
		assertFalse(controller.isError());
		
		// error n'affecte pas isAccount
		controller.setError(true);
		assertTrue(controller.isError());
		assertTrue(controller.isAccount());
		
		// Les deux peuvent être indépendants
		controller.setAccount(false);
		assertTrue(controller.isError());
		assertFalse(controller.isAccount());
	}

	/**
	 * Test modification successive du même champ
	 */
	@Test
	void testSuccessiveModifications() {
		// Première modification
		controller.setCompteInfo("COMPTE-1");
		assertEquals("COMPTE-1", controller.getCompteInfo());
		
		// Deuxième modification
		controller.setCompteInfo("COMPTE-2");
		assertEquals("COMPTE-2", controller.getCompteInfo());
		
		// Vérifier que c'est bien la dernière valeur
		controller.setCompteInfo("COMPTE-3");
		assertEquals("COMPTE-3", controller.getCompteInfo());
	}

	/**
	 * Test avec des chiffres dans les strings
	 */
	@Test
	void testNumericStrings() {
		controller.setCompteInfo("123456789");
		assertEquals("123456789", controller.getCompteInfo());
		
		controller.setUserInfo("12345");
		assertEquals("12345", controller.getUserInfo());
	}

	/**
	 * Test setErrorMessage interaction avec boolean error
	 */
	@Test
	void testErrorMessageBooleanInteraction() {
		// Ajout d'un message d'erreur
		controller.setErrorMessage("Erreur 1");
		assertTrue(controller.isError());
		
		// Changement du message d'erreur (error reste true)
		controller.setErrorMessage("Erreur 2");
		assertTrue(controller.isError());
		assertEquals("Erreur 2", controller.getErrorMessage());
		
		// Suppression du message (error devient false)
		controller.setErrorMessage(null);
		assertFalse(controller.isError());
		assertNull(controller.getErrorMessage());
	}

	/**
	 * Test reset complet du controller
	 */
	@Test
	void testCompleteReset() {
		// Remplir le controller
		controller.setAccount(true);
		controller.setCompteInfo("FR99999");
		controller.setUserInfo("user123");
		controller.setErrorMessage("Une erreur");
		
		// Vérifier que tout est rempli
		assertTrue(controller.isAccount());
		assertEquals("FR99999", controller.getCompteInfo());
		assertEquals("user123", controller.getUserInfo());
		assertTrue(controller.isError());
		assertEquals("Une erreur", controller.getErrorMessage());
		
		// Réinitialiser (créer une nouvelle instance)
		controller = new ResultatSuppression();
		
		// Vérifier que tout est réinitialisé
		assertFalse(controller.isAccount());
		assertNull(controller.getCompteInfo());
		assertNull(controller.getUserInfo());
		assertFalse(controller.isError());
		assertNull(controller.getErrorMessage());
		assertNull(controller.getCompte());
		assertNull(controller.getClient());
	}

	/**
	 * Test avec des valeurs limites (très longs strings)
	 */
	@Test
	void testLongStrings() {
		String longCompteInfo = "FR" + generateString("0", 100);
		controller.setCompteInfo(longCompteInfo);
		assertEquals(longCompteInfo, controller.getCompteInfo());
		
		String longUserInfo = "user_" + generateString("x", 100);
		controller.setUserInfo(longUserInfo);
		assertEquals(longUserInfo, controller.getUserInfo());
	}
	
	/**
	 * Helper pour générer des strings répétées
	 */
	private String generateString(String character, int count) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++) {
			sb.append(character);
		}
		return sb.toString();
	}

	/**
	 * Test alternance entre succès et erreurs
	 */
	@Test
	void testAlternationSuccessError() {
		// Succès 1
		controller.setErrorMessage(null);
		assertFalse(controller.isError());
		
		// Erreur 1
		controller.setErrorMessage("Erreur A");
		assertTrue(controller.isError());
		
		// Succès 2
		controller.setErrorMessage(null);
		assertFalse(controller.isError());
		
		// Erreur 2
		controller.setErrorMessage("Erreur B");
		assertTrue(controller.isError());
	}

	/**
	 * Test compteInfo avec différents formats IBAN
	 */
	@Test
	void testCompteInfo_DifferentIbanFormats() {
		// Format standard
		controller.setCompteInfo("FR1420041010050500013M02606");
		assertEquals("FR1420041010050500013M02606", controller.getCompteInfo());
		
		// Format court
		controller.setCompteInfo("FR1");
		assertEquals("FR1", controller.getCompteInfo());
		
		// Format vide
		controller.setCompteInfo("");
		assertEquals("", controller.getCompteInfo());
	}

	/**
	 * Test userInfo avec différents formats d'identifiants
	 */
	@Test
	void testUserInfo_DifferentFormats() {
		// Format avec point
		controller.setUserInfo("jean.dupont");
		assertEquals("jean.dupont", controller.getUserInfo());
		
		// Format avec underscore
		controller.setUserInfo("jean_dupont");
		assertEquals("jean_dupont", controller.getUserInfo());
		
		// Format avec tiret
		controller.setUserInfo("jean-dupont");
		assertEquals("jean-dupont", controller.getUserInfo());
	}

	/**
	 * Test message d'erreur vide vs null
	 */
	@Test
	void testErrorLogic_EmptyVsNull() {
		// Vide = pas d'erreur
		controller.setErrorMessage("");
		assertFalse(controller.isError());
		assertEquals("", controller.getErrorMessage());
		
		// Null = pas d'erreur
		controller.setErrorMessage(null);
		assertFalse(controller.isError());
		assertNull(controller.getErrorMessage());
	}

	/**
	 * Test cohérence entre isAccount et userInfo
	 */
	@Test
	void testConsistency_AccountVsUserInfo() {
		// Cas 1: Suppression de compte
		controller.setAccount(true);
		controller.setCompteInfo("COMPTE123");
		assertNotNull(controller.getCompteInfo());
		
		// Cas 2: Suppression d'utilisateur
		controller.setAccount(false);
		controller.setUserInfo("USER456");
		assertNotNull(controller.getUserInfo());
	}
}
