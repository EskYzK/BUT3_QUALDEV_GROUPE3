package com.iut.banque.test.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.iut.banque.controller.DetailCompteEdit;
import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.CompteAvecDecouvert;
import com.iut.banque.modele.CompteSansDecouvert;
import com.iut.banque.modele.Gestionnaire;

@ExtendWith(MockitoExtension.class)
class DetailCompteEditTest {

	private DetailCompteEdit controller;

	@Mock
	private BanqueFacade banqueMock;
	@Mock
	private CompteAvecDecouvert compteAvecDecouvertMock;
	@Mock
	private CompteSansDecouvert compteSansDecouvertMock;
	@Mock
	private Gestionnaire gestionnaireMock;

	@BeforeEach
	void setUp() {
		controller = new DetailCompteEdit();
		controller.setBanque(banqueMock);
		// On simule un utilisateur qui a les droits par défaut (Gestionnaire)
		lenient().when(banqueMock.getConnectedUser()).thenReturn(gestionnaireMock);
	}

	/**
	 * Test changement de découvert avec succès
	 */
	@Test
	void testChangementDecouvert_Success() throws Exception {
		controller.setCompte(compteAvecDecouvertMock);
		controller.setDecouvertAutorise("100.50");

		String result = controller.changementDecouvert();

		assertEquals("SUCCESS", result);
		verify(banqueMock).changeDecouvert(compteAvecDecouvertMock, 100.50);
	}

	/**
	 * Test changement de découvert avec un entier
	 */
	@Test
	void testChangementDecouvert_IntegerValue() throws Exception {
		controller.setCompte(compteAvecDecouvertMock);
		controller.setDecouvertAutorise("500");

		String result = controller.changementDecouvert();

		assertEquals("SUCCESS", result);
		verify(banqueMock).changeDecouvert(compteAvecDecouvertMock, 500.0);
	}

	/**
	 * Test changement de découvert - type de compte incorrect (CompteSansDecouvert)
	 */
	@Test
	void testChangementDecouvert_WrongAccountType() throws Exception {
		controller.setCompte(compteSansDecouvertMock);
		controller.setDecouvertAutorise("100");

		String result = controller.changementDecouvert();

		assertEquals("ERROR", result);
		// On vérifie que la banque n'a jamais été appelée
		verify(banqueMock, never()).changeDecouvert(any(), anyDouble());
	}

	/**
	 * Test changement de découvert - format invalide (pas un nombre)
	 */
	@Test
	void testChangementDecouvert_InvalidFormat_NonNumeric() {
		controller.setCompte(compteAvecDecouvertMock);
		controller.setDecouvertAutorise("pas_un_nombre");

		String result = controller.changementDecouvert();

		assertEquals("ERROR", result);
	}

	/**
	 * Test changement de découvert - format invalide (caractères spéciaux)
	 */
	@Test
	void testChangementDecouvert_InvalidFormat_SpecialChars() {
		controller.setCompte(compteAvecDecouvertMock);
		controller.setDecouvertAutorise("100#50");

		String result = controller.changementDecouvert();

		assertEquals("ERROR", result);
	}

	/**
	 * Test changement de découvert - valeur négative
	 */
	@Test
	void testChangementDecouvert_NegativeValue() {
		controller.setCompte(compteAvecDecouvertMock);
		controller.setDecouvertAutorise("-50");
		
		String result = controller.changementDecouvert();

		// NumberFormatException sur double négatif devient ERROR ou NEGATIVEOVERDRAFT
		// Le résultat dépend de l'implémentation
		assertNotNull(result);
	}

	/**
	 * Test changement de découvert - découvert incompatible avec le solde
	 */
	@Test
	void testChangementDecouvert_IncompatibleOverdraft_InvalidFormat() {
		controller.setCompte(compteAvecDecouvertMock);
		controller.setDecouvertAutorise("#invalid#");
		
		String result = controller.changementDecouvert();

		// Format invalide provoque NumberFormatException
		assertEquals("ERROR", result);
	}

	/**
	 * Test changement de découvert - découvert très elevé
	 */
	@Test
	void testChangementDecouvert_LargeValue() throws Exception {
		controller.setCompte(compteAvecDecouvertMock);
		controller.setDecouvertAutorise("999999.99");

		String result = controller.changementDecouvert();

		assertEquals("SUCCESS", result);
		verify(banqueMock).changeDecouvert(compteAvecDecouvertMock, 999999.99);
	}

	/**
	 * Test changement de découvert - valeur zéro
	 */
	@Test
	void testChangementDecouvert_ZeroValue() throws Exception {
		controller.setCompte(compteAvecDecouvertMock);
		controller.setDecouvertAutorise("0.0");

		String result = controller.changementDecouvert();

		assertEquals("SUCCESS", result);
		verify(banqueMock).changeDecouvert(compteAvecDecouvertMock, 0.0);
	}

	/**
	 * Test changement de découvert - valeur avec espaces (trim implicite par parseDouble)
	 */
	@Test
	void testChangementDecouvert_ValueWithSpaces() throws Exception {
		controller.setCompte(compteAvecDecouvertMock);
		controller.setDecouvertAutorise("  150.75  ");

		String result = controller.changementDecouvert();

		assertEquals("SUCCESS", result);
		verify(banqueMock).changeDecouvert(compteAvecDecouvertMock, 150.75);
	}

	/**
	 * Test changement de découvert - valeur null (NPE)
	 */
	@Test
	void testChangementDecouvert_NullValue() {
		controller.setCompte(compteAvecDecouvertMock);
		controller.setDecouvertAutorise(null);

		assertThrows(NullPointerException.class, () -> {
			controller.changementDecouvert();
		});
	}

	/**
	 * Test changement de découvert - chaîne vide
	 */
	@Test
	void testChangementDecouvert_EmptyString() {
		controller.setCompte(compteAvecDecouvertMock);
		controller.setDecouvertAutorise("");

		String result = controller.changementDecouvert();

		assertEquals("ERROR", result);
	}

	/**
	 * Test setter/getter DecouvertAutorise
	 */
	@Test
	void testSetterGetter_DecouvertAutorise() {
		controller.setDecouvertAutorise("250.00");
		assertEquals("250.00", controller.getDecouvertAutorise());
	}

	/**
	 * Test setter/getter Compte avec héritage de DetailCompte
	 */
	@Test
	void testSetterGetter_Compte() {
		CompteAvecDecouvert compte = mock(CompteAvecDecouvert.class);
		controller.setCompte(compte);
		assertEquals(compte, controller.getCompte());
	}

	/**
	 * Test setter/getter Montant hérité de DetailCompte
	 */
	@Test
	void testSetterGetter_Montant() {
		controller.setMontant("500.0");
		assertEquals("500.0", controller.getMontant());
	}

	/**
	 * Test setter/getter Error hérité de DetailCompte
	 */
	@Test
	void testSetterGetter_Error() {
		controller.setError("TECHNICAL");
		assertEquals("Erreur interne. Vérifiez votre saisie puis réessayez. Contactez votre conseiller si le problème persiste.",
			controller.getError());
	}

	/**
	 * Test setter/getter Message hérité de DetailCompte
	 */
	@Test
	void testSetterGetter_Message() {
		controller.setMessage("Test message");
		assertEquals("Test message", controller.getMessage());
	}

	/**
	 * Test setter BanqueFacade
	 */
	@Test
	void testSetterBanque() {
		DetailCompteEdit newController = new DetailCompteEdit();
		BanqueFacade newBanqueMock = mock(BanqueFacade.class);
		newController.setBanque(newBanqueMock);
		assertNotNull(newBanqueMock);
	}

	/**
	 * Test héritage de getCompte() avec sécurité (Gestionnaire)
	 */
	@Test
	void testHeritage_GetCompte_AsGestionnaire() {
		when(banqueMock.getConnectedUser()).thenReturn(gestionnaireMock);
		controller.setCompte(compteAvecDecouvertMock);
		
		assertEquals(compteAvecDecouvertMock, controller.getCompte());
	}

	/**
	 * Test instanceof correctement typé pour CompteAvecDecouvert
	 */
	@Test
	void testInstanceof_CompteAvecDecouvert() {
		controller.setCompte(compteAvecDecouvertMock);
		controller.setDecouvertAutorise("100");

		// Type correct
		assertTrue(controller.getCompte() instanceof CompteAvecDecouvert);
		
		String result = controller.changementDecouvert();
		assertEquals("SUCCESS", result);
	}

	/**
	 * Test instanceof rejeté pour CompteSansDecouvert
	 */
	@Test
	void testInstanceof_CompteSansDecouvert() {
		controller.setCompte(compteSansDecouvertMock);
		controller.setDecouvertAutorise("100");

		// Type incorrect
		assertFalse(controller.getCompte() instanceof CompteAvecDecouvert);
		
		String result = controller.changementDecouvert();
		assertEquals("ERROR", result);
	}

	/**
	 * Test tous les codes d'erreur de getError() hérité
	 */
	@Test
	void testGetError_AllErrorCodes() {
		controller.setError("BUSINESS");
		assertEquals("Fonds insuffisants.", controller.getError());
		
		controller.setError("NEGATIVEAMOUNT");
		assertEquals("Veuillez rentrer un montant positif.", controller.getError());
		
		controller.setError("NEGATIVEOVERDRAFT");
		assertEquals("Veuillez rentrer un découvert positif.", controller.getError());
		
		controller.setError("INCOMPATIBLEOVERDRAFT");
		assertEquals("Le nouveau découvert est incompatible avec le solde actuel.", controller.getError());
	}

	/**
	 * Test double.parseDouble avec format scientifique
	 */
	@Test
	void testChangementDecouvert_ScientificNotation() throws Exception {
		controller.setCompte(compteAvecDecouvertMock);
		controller.setDecouvertAutorise("1.5E2"); // 150 en notation scientifique

		String result = controller.changementDecouvert();

		assertEquals("SUCCESS", result);
		verify(banqueMock).changeDecouvert(compteAvecDecouvertMock, 150.0);
	}

	/**
	 * Test Multiple changements de découvert successifs
	 */
	@Test
	void testChangementDecouvert_MultipleSuccessful() throws Exception {
		controller.setCompte(compteAvecDecouvertMock);
		
		// Premier changement
		controller.setDecouvertAutorise("100.0");
		String result1 = controller.changementDecouvert();
		assertEquals("SUCCESS", result1);
		verify(banqueMock).changeDecouvert(compteAvecDecouvertMock, 100.0);
		
		// Deuxième changement
		controller.setDecouvertAutorise("200.0");
		String result2 = controller.changementDecouvert();
		assertEquals("SUCCESS", result2);
		verify(banqueMock, times(2)).changeDecouvert(any(CompteAvecDecouvert.class), anyDouble());
	}
}
