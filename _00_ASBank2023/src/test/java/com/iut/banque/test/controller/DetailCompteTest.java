package com.iut.banque.test.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.iut.banque.controller.DetailCompte;
import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.InsufficientFundsException;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.CarteBancaire;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;
import com.iut.banque.modele.Gestionnaire;

@ExtendWith(MockitoExtension.class)
class DetailCompteTest {

	private DetailCompte controller;

	@Mock
	private BanqueFacade banqueMock;
	@Mock
	private Compte compteMock;
	@Mock
	private Client clientMock;
	@Mock
	private Gestionnaire gestionnaireMock;
	@Mock
	private CarteBancaire carteMock;

	@BeforeEach
	void setUp() {
		controller = new DetailCompte();
		controller.setBanque(banqueMock);
		controller.setCompte(compteMock);
	}

	/**
	 * Test getCompte() avec un gestionnaire (accès autorisé)
	 */
	@Test
	void testGetCompte_AsGestionnaire() {
		when(banqueMock.getConnectedUser()).thenReturn(gestionnaireMock);
		
		Compte result = controller.getCompte();
		
		assertEquals(compteMock, result);
	}

	/**
	 * Test getCompte() avec un client propriétaire du compte (accès autorisé)
	 */
	@Test
	void testGetCompte_AsClient_Owner() {
		when(banqueMock.getConnectedUser()).thenReturn(clientMock);
		when(compteMock.getNumeroCompte()).thenReturn("FR123");
		
		// Le client possède ce compte
		Map<String, Compte> accounts = new HashMap<>();
		accounts.put("FR123", compteMock);
		when(clientMock.getAccounts()).thenReturn(accounts);

		Compte result = controller.getCompte();
		
		assertEquals(compteMock, result);
	}

	/**
	 * Test getCompte() avec un client NON propriétaire du compte (accès refusé - SÉCURITÉ)
	 */
	@Test
	void testGetCompte_AsClient_NotOwner() {
		when(banqueMock.getConnectedUser()).thenReturn(clientMock);
		when(compteMock.getNumeroCompte()).thenReturn("FR_HACKER");
		
		// Le client n'a pas ce compte dans sa liste
		when(clientMock.getAccounts()).thenReturn(new HashMap<>());

		Compte result = controller.getCompte();
		
		assertNull(result, "Un client ne doit pas accéder à un compte qu'il ne possède pas");
	}

	/**
	 * Test debit() avec succès
	 */
	@Test
	void testDebit_Success() throws Exception {
		when(banqueMock.getConnectedUser()).thenReturn(gestionnaireMock);
		controller.setMontant("100.50");

		String result = controller.debit();

		assertEquals("SUCCESS", result);
		verify(banqueMock).debiter(compteMock, 100.50);
	}

	/**
	 * Test debit() avec montant avec espaces (trim() doit fonctionner)
	 */
	@Test
	void testDebit_MontantWithTrim() throws Exception {
		when(banqueMock.getConnectedUser()).thenReturn(gestionnaireMock);
		controller.setMontant("  75.25  ");

		String result = controller.debit();

		assertEquals("SUCCESS", result);
		verify(banqueMock).debiter(compteMock, 75.25);
	}

	/**
	 * Test debit() avec fonds insuffisants
	 */
	@Test
	void testDebit_InsufficientFunds() throws Exception {
		when(banqueMock.getConnectedUser()).thenReturn(gestionnaireMock);
		controller.setMontant("10000.0");
		
		doThrow(new InsufficientFundsException())
			.when(banqueMock).debiter(any(), anyDouble());

		String result = controller.debit();

		assertEquals("NOTENOUGHFUNDS", result);
	}

	/**
	 * Test debit() avec format de montant invalide (NumberFormatException)
	 */
	@Test
	void testDebit_InvalidNumberFormat() throws Exception {
		when(banqueMock.getConnectedUser()).thenReturn(gestionnaireMock);
		controller.setMontant("abc123");

		String result = controller.debit();

		assertEquals("ERROR", result);

	}

	/**
	 * Test debit() avec montant négatif (IllegalFormatException)
	 */
	@Test
	void testDebit_NegativeAmount() throws Exception {
		when(banqueMock.getConnectedUser()).thenReturn(gestionnaireMock);
		controller.setMontant("-50.0");
		
		doThrow(new IllegalFormatException())
			.when(banqueMock).debiter(any(), anyDouble());

		String result = controller.debit();

		assertEquals("NEGATIVEAMOUNT", result);
	}

	/**
	 * Test credit() avec succès
	 */
	@Test
	void testCredit_Success() throws Exception {
		when(banqueMock.getConnectedUser()).thenReturn(gestionnaireMock);
		controller.setMontant("50.0");

		String result = controller.credit();

		assertEquals("SUCCESS", result);
		verify(banqueMock).crediter(compteMock, 50.0);
	}

	/**
	 * Test credit() avec montant positif et trim()
	 */
	@Test
	void testCredit_PositiveAmountWithTrim() throws Exception {
		when(banqueMock.getConnectedUser()).thenReturn(gestionnaireMock);
		controller.setMontant("  99.99  ");

		String result = controller.credit();

		assertEquals("SUCCESS", result);
		verify(banqueMock).crediter(compteMock, 99.99);
	}

	/**
	 * Test credit() avec montant négatif
	 */
	@Test
	void testCredit_NegativeAmount() throws Exception {
		when(banqueMock.getConnectedUser()).thenReturn(gestionnaireMock);
		controller.setMontant("-25.0");
		
		doThrow(new IllegalFormatException())
			.when(banqueMock).crediter(any(), anyDouble());

		String result = controller.credit();

		assertEquals("NEGATIVEAMOUNT", result);
	}

	/**
	 * Test credit() avec format de montant invalide (NumberFormatException)
	 */
	@Test
	void testCredit_InvalidNumberFormat() throws Exception {
		when(banqueMock.getConnectedUser()).thenReturn(gestionnaireMock);
		controller.setMontant("xyz.123");

		String result = controller.credit();

		assertEquals("ERROR", result);

	}

	/**
	 * Test getError() - TECHNICAL
	 */
	@Test
	void testGetError_Technical() {
		controller.setError("TECHNICAL");
		assertEquals("Erreur interne. Vérifiez votre saisie puis réessayez. Contactez votre conseiller si le problème persiste.",
			controller.getError());
	}

	/**
	 * Test getError() - BUSINESS (insufficient funds)
	 */
	@Test
	void testGetError_Business() {
		controller.setError("BUSINESS");
		assertEquals("Fonds insuffisants.", controller.getError());
	}

	/**
	 * Test getError() - OVER_LIMIT
	 */
	@Test
	void testGetError_OverLimit() {
		controller.setError("OVER_LIMIT");
		assertEquals("Paiement refusé : Le plafond de la carte (sur 30 jours) ou le solde du compte est dépassé.", 
			controller.getError());
	}

	/**
	 * Test getError() - MISSING_ACCOUNT
	 */
	@Test
	void testGetError_MissingAccount() {
		controller.setError("MISSING_ACCOUNT");
		assertEquals("Numéro de compte manquant.", controller.getError());
	}

	/**
	 * Test getError() - CARD_NOT_FOUND
	 */
	@Test
	void testGetError_CardNotFound() {
		controller.setError("CARD_NOT_FOUND");
		assertEquals("Carte introuvable.", controller.getError());
	}

	/**
	 * Test getError() - NEGATIVEAMOUNT
	 */
	@Test
	void testGetError_NegativeAmount() {
		controller.setError("NEGATIVEAMOUNT");
		assertEquals("Veuillez rentrer un montant positif.", controller.getError());
	}

	/**
	 * Test getError() - NEGATIVEOVERDRAFT
	 */
	@Test
	void testGetError_NegativeOverdraft() {
		controller.setError("NEGATIVEOVERDRAFT");
		assertEquals("Veuillez rentrer un découvert positif.", controller.getError());
	}

	/**
	 * Test getError() - INCOMPATIBLEOVERDRAFT
	 */
	@Test
	void testGetError_IncompatibleOverdraft() {
		controller.setError("INCOMPATIBLEOVERDRAFT");
		assertEquals("Le nouveau découvert est incompatible avec le solde actuel.", 
			controller.getError());
	}

	/**
	 * Test getError() - EMPTY/null
	 */
	@Test
	void testGetError_Empty() {
		controller.setError(null);
		assertEquals("", controller.getError());
		
		controller.setError("");
		assertEquals("", controller.getError());
		
		controller.setError("EMPTY");
		assertEquals("", controller.getError());
	}

	/**
	 * Test getError() - UNKNOWN case
	 */
	@Test
	void testGetError_Unknown() {
		controller.setError("UNKNOWN_CODE");
		assertEquals("Erreur inconnue", controller.getError());
	}

	/**
	 * Test setter/getter Montant
	 */
	@Test
	void testSetterGetter_Montant() {
		controller.setMontant("150.75");
		assertEquals("150.75", controller.getMontant());
	}

	/**
	 * Test setter/getter Error
	 */
	@Test
	void testSetterGetter_Error() {
		controller.setError("BUSINESS");
		assertEquals("Fonds insuffisants.", controller.getError());
	}

	/**
	 * Test setter/getter Message
	 */
	@Test
	void testSetterGetter_Message() {
		controller.setMessage("Test message");
		assertEquals("Test message", controller.getMessage());
	}

	/**
	 * Test setter/getter Compte
	 */
	@Test
	void testSetterGetter_Compte() {		when(banqueMock.getConnectedUser()).thenReturn(gestionnaireMock);		Compte otherCompte = mock(Compte.class);
		controller.setCompte(otherCompte);
		assertEquals(otherCompte, controller.getCompte());
	}

	/**
	 * Test setter BanqueFacade
	 */
	@Test
	void testSetterBanque() {
		DetailCompte newController = new DetailCompte();
		BanqueFacade newBanqueMock = mock(BanqueFacade.class);
		newController.setBanque(newBanqueMock);
		assertNotNull(newBanqueMock);
	}

	/**
	 * Test getCartesTriees() - avec cartes normales
	 */
	@Test
	void testGetCartesTriees_WithCards() {
		when(banqueMock.getConnectedUser()).thenReturn(gestionnaireMock);
		
		Set<CarteBancaire> cartes = new HashSet<>();
		CarteBancaire carte1 = mock(CarteBancaire.class);
		CarteBancaire carte2 = mock(CarteBancaire.class);
		
		when(carte1.getNumeroCarte()).thenReturn("1111");
		when(carte2.getNumeroCarte()).thenReturn("2222");
		
		cartes.add(carte1);
		cartes.add(carte2);
		when(compteMock.getCartes()).thenReturn(cartes);

		List<CarteBancaire> result = controller.getCartesTriees();

		assertEquals(2, result.size());
	}

	/**
	 * Test getCartesTriees() - compte null
	 */
	@Test
	void testGetCartesTriees_CompteNull() {
		when(banqueMock.getConnectedUser()).thenReturn(clientMock);
		when(clientMock.getAccounts()).thenReturn(new HashMap<>()); // Client sans accès
		
		List<CarteBancaire> result = controller.getCartesTriees();

		assertTrue(result.isEmpty());
	}

	/**
	 * Test getCartesTriees() - cartes null
	 */
	@Test
	void testGetCartesTriees_CartesNull() {
		when(banqueMock.getConnectedUser()).thenReturn(gestionnaireMock);
		when(compteMock.getCartes()).thenReturn(null);

		List<CarteBancaire> result = controller.getCartesTriees();

		assertTrue(result.isEmpty());
	}

	/**
	 * Test getCartesTriees() - avec Collection de cartes
	 */
	@Test
	void testGetCartesTriees_AsCollection() {
		when(banqueMock.getConnectedUser()).thenReturn(gestionnaireMock);
		
		Set<CarteBancaire> cartesList = new HashSet<>();
		CarteBancaire carte1 = mock(CarteBancaire.class);
		CarteBancaire carte2 = mock(CarteBancaire.class);
		
		when(carte1.getNumeroCarte()).thenReturn("3333");
		when(carte2.getNumeroCarte()).thenReturn("1111");
		
		cartesList.add(carte1);
		cartesList.add(carte2);
		when(compteMock.getCartes()).thenReturn(cartesList);

		List<CarteBancaire> result = controller.getCartesTriees();

		assertEquals(2, result.size());
	}

	/**
	 * Test getCartesTriees() - tri correct avec plusieurs cartes
	 */
	@Test
	void testGetCartesTriees_CorrectSorting() {
		when(banqueMock.getConnectedUser()).thenReturn(gestionnaireMock);
		
		Set<CarteBancaire> cartes = new HashSet<>();
		CarteBancaire carte1 = mock(CarteBancaire.class);
		CarteBancaire carte2 = mock(CarteBancaire.class);
		CarteBancaire carte3 = mock(CarteBancaire.class);
		
		when(carte1.getNumeroCarte()).thenReturn("5555");
		when(carte2.getNumeroCarte()).thenReturn("1111");
		when(carte3.getNumeroCarte()).thenReturn("3333");
		
		cartes.add(carte1);
		cartes.add(carte2);
		cartes.add(carte3);
		when(compteMock.getCartes()).thenReturn(cartes);

		List<CarteBancaire> result = controller.getCartesTriees();

		assertEquals("1111", result.get(0).getNumeroCarte());
		assertEquals("3333", result.get(1).getNumeroCarte());
		assertEquals("5555", result.get(2).getNumeroCarte());
	}
}
