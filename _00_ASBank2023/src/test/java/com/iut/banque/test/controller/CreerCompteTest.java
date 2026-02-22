package com.iut.banque.test.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.iut.banque.controller.CreerCompte;
import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.exceptions.TechnicalException;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;

@ExtendWith(MockitoExtension.class)
class CreerCompteTest {

	private CreerCompte controller;

	@Mock
	private BanqueFacade banqueMock;
	@Mock
	private Client clientMock;
	@Mock
	private Compte compteMock;

	@BeforeEach
	void setUp() {
		controller = new CreerCompte();
		controller.setBanqueFacade(banqueMock);
		controller.setClient(clientMock);
	}

	/**
	 * Test la création d'un compte SANS découvert - succès
	 */
	@Test
	void testCreationCompte_SansDecouvert_Success() throws Exception {
		controller.setNumeroCompte("FR12345");
		controller.setAvecDecouvert(false);
		when(banqueMock.getCompte("FR12345")).thenReturn(compteMock);

		String result = controller.creationCompte();

		assertEquals("SUCCESS", result);
		verify(banqueMock).createAccount("FR12345", clientMock);
		verify(banqueMock, never()).createAccount(anyString(), any(), anyDouble());
		assertEquals(compteMock, controller.getCompte());
	}

	/**
	 * Test la création d'un compte AVEC découvert - succès
	 */
	@Test
	void testCreationCompte_AvecDecouvert_Success() throws Exception {
		controller.setNumeroCompte("FR999");
		controller.setAvecDecouvert(true);
		controller.setDecouvertAutorise(500.0);
		when(banqueMock.getCompte("FR999")).thenReturn(compteMock);

		String result = controller.creationCompte();

		assertEquals("SUCCESS", result);
		verify(banqueMock).createAccount("FR999", clientMock, 500.0);
		verify(banqueMock, never()).createAccount("FR999", clientMock);
		assertEquals(compteMock, controller.getCompte());
	}

	/**
	 * Test avec découvert, mais exception IllegalOperationException levée
	 */
	@Test
	void testCreationCompte_AvecDecouvert_IllegalOperationException() throws Exception {
		controller.setNumeroCompte("FR888");
		controller.setAvecDecouvert(true);
		controller.setDecouvertAutorise(1000.0);

		doThrow(new IllegalOperationException("Impossible de créer un compte avec découvert"))
			.when(banqueMock).createAccount("FR888", clientMock, 1000.0);

		String result = controller.creationCompte();

		assertEquals("error", result);

        // Optionnel, mais recommandé : vérifier que getCompte n'a JAMAIS été appelé
        verify(banqueMock, never()).getCompte(anyString());
	}

	/**
	 * Test format invalide - IllegalFormatException levée
	 */
	@Test
	void testCreationCompte_FormatInvalide() throws Exception {
		controller.setNumeroCompte("BAD-FORMAT");
		controller.setAvecDecouvert(false);
		doThrow(new IllegalFormatException()).when(banqueMock).createAccount("BAD-FORMAT", clientMock);

		String result = controller.creationCompte();

		assertEquals("INVALIDFORMAT", result);
		verify(banqueMock).createAccount("BAD-FORMAT", clientMock);
		verify(banqueMock, never()).getCompte(anyString());
	}

	/**
	 * Test numéro de compte déjà existant - TechnicalException levée
	 */
	@Test
	void testCreationCompte_DejaExistant() throws Exception {
		controller.setNumeroCompte("EXISTING");
		controller.setAvecDecouvert(false);

		doThrow(new TechnicalException("Le compte existe déjà")).when(banqueMock)
			.createAccount("EXISTING", clientMock);

		String result = controller.creationCompte();

		assertEquals("NONUNIQUEID", result);
		verify(banqueMock).createAccount("EXISTING", clientMock);
	}



	/**
	 * Test setMessage avec SUCCESS
	 */
	@Test
	void testSetMessage_SUCCESS() {
		controller.setCompte(compteMock);
		when(compteMock.getNumeroCompte()).thenReturn("FR-SUCCESS");
		controller.setMessage("SUCCESS");

		assertEquals("Le compte FR-SUCCESS a bien été créé.", controller.getMessage());
	}

	/**
	 * Test setMessage avec INVALIDFORMAT
	 */
	@Test
	void testSetMessage_INVALIDFORMAT() {
		controller.setMessage("INVALIDFORMAT");
		assertEquals("Ce numéro de compte n'est pas dans un format valide !", controller.getMessage());
	}

	/**
	 * Test setMessage avec NONUNIQUEID
	 */
	@Test
	void testSetMessage_NONUNIQUEID() {
		controller.setMessage("NONUNIQUEID");
		assertEquals("Ce numéro de compte existe déjà !", controller.getMessage());
	}

	/**
	 * Test setMessage avec valeur invalide
	 */
	@Test
	void testSetMessage_UnknownValue() {
		controller.setMessage("UNKNOWN");
        assertEquals("UNKNOWN", controller.getMessage());
	}

	/**
	 * Test getters/setters numeroCompte
	 */
	@Test
	void testSetterGetter_NumeroCompte() {
		controller.setNumeroCompte("FR-TEST-123");
		assertEquals("FR-TEST-123", controller.getNumeroCompte());
	}

	/**
	 * Test getters/setters avecDecouvert
	 */
	@Test
	void testSetterGetter_AvecDecouvert() {
		controller.setAvecDecouvert(true);
		assertTrue(controller.isAvecDecouvert());
		controller.setAvecDecouvert(false);
		assertFalse(controller.isAvecDecouvert());
	}

	/**
	 * Test getters/setters decouvertAutorise
	 */
	@Test
	void testSetterGetter_DecouvertAutorise() {
		controller.setDecouvertAutorise(1500.0);
		assertEquals(1500.0, controller.getDecouvertAutorise());
	}

	/**
	 * Test getters/setters client
	 */
	@Test
	void testSetterGetter_Client() {
		controller.setClient(clientMock);
		assertEquals(clientMock, controller.getClient());
	}

	/**
	 * Test getters/setters compte
	 */
	@Test
	void testSetterGetter_Compte() {
		controller.setCompte(compteMock);
		assertEquals(compteMock, controller.getCompte());
	}

	/**
	 * Test getters/setters error
	 */
	@Test
	void testSetterGetter_Error() {
		controller.setError(true);
		assertTrue(controller.isError());
		controller.setError(false);
		assertFalse(controller.isError());
	}

	/**
	 * Test getters/setters result
	 */
	@Test
	void testSetterGetter_Result() {
		controller.setResult(true);
		assertTrue(controller.isResult());
		controller.setResult(false);
		assertFalse(controller.isResult());
	}

	/**
	 * Test injection du setter BanqueFacade
	 */
	@Test
	void testSetterBanqueFacade() {
		CreerCompte newController = new CreerCompte();
		newController.setBanqueFacade(banqueMock);
		assertNotNull(banqueMock);
	}
}
