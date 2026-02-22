package com.iut.banque.test.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.iut.banque.controller.GestionCartes;
import com.iut.banque.exceptions.InsufficientFundsException;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.CarteBancaire;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;
import com.opensymphony.xwork2.Action;

@ExtendWith(MockitoExtension.class)
class GestionCartesTest {

    private GestionCartes gestionCartes;

    @Mock
    private BanqueFacade banqueFacadeMock;
    @Mock
    private CarteBancaire carteMock;
    @Mock
    private Compte compteMock;
    @Mock
    private Client clientMock;

    @BeforeEach
    void setUp() {
        gestionCartes = new GestionCartes();
        gestionCartes.setBanqueFacade(banqueFacadeMock);
    }

    // ================================================================
    // ACTION 1 : Formulaire & Création
    // ================================================================

    @Test
    void testFormulaireCreation_Succes() {
        gestionCartes.setNumeroCompte("CPT001");
        String result = gestionCartes.formulaireCreation();
        assertEquals(Action.SUCCESS, result);
    }

    @Test
    void testFormulaireCreation_ManqueCompte() {
        gestionCartes.setNumeroCompte(null); // ou ""
        String result = gestionCartes.formulaireCreation();
        assertEquals(Action.ERROR, result);
        assertEquals("MISSING_ACCOUNT", gestionCartes.getMessage());
    }

    @Test
    void testCreerCarte_Succes() throws Exception {
        gestionCartes.setNumeroCompte("CPT001");
        gestionCartes.setPlafond(500.0);
        gestionCartes.setTypeDebit("IMMEDIAT");

        String result = gestionCartes.creerCarte();

        verify(banqueFacadeMock).createCarte("CPT001", 500.0, "IMMEDIAT");
        assertEquals("Carte créée avec succès.", gestionCartes.getMessage());
        assertEquals(Action.SUCCESS, result);
    }

    @Test
    void testCreerCarte_PlafondInvalide() {
        gestionCartes.setPlafond(-10.0);
        String result = gestionCartes.creerCarte();
        assertEquals(Action.INPUT, result);
        assertTrue(gestionCartes.getFieldErrors().containsKey("plafond"));
    }

    @Test
    void testCreerCarte_ExceptionTechnique() throws Exception {
        gestionCartes.setPlafond(100.0);
        doThrow(new RuntimeException("Erreur DB")).when(banqueFacadeMock).createCarte(anyString(), anyDouble(), anyString());

        String result = gestionCartes.creerCarte();
        assertEquals(Action.ERROR, result);
        assertEquals("TECHNICAL", gestionCartes.getMessage());
    }

    // ================================================================
    // ACTION 2 : Blocage / Déblocage
    // ================================================================

    @Test
    void testBloquerCarte_Temporaire() throws Exception {
        gestionCartes.setNumeroCarte("1111");
        gestionCartes.setDefinitif(false); // Temporaire

        String result = gestionCartes.bloquerCarte();

        verify(banqueFacadeMock).bloquerCarte("1111", false);
        assertEquals("Carte bloquée temporairement.", gestionCartes.getMessage());
        assertEquals(Action.SUCCESS, result);
    }

    @Test
    void testBloquerCarte_Definitif() throws Exception {
        gestionCartes.setNumeroCarte("1111");
        gestionCartes.setDefinitif(true); // Suppression

        String result = gestionCartes.bloquerCarte();

        verify(banqueFacadeMock).bloquerCarte("1111", true);
        assertEquals("Carte supprimée définitivement.", gestionCartes.getMessage());
        assertEquals(Action.SUCCESS, result);
    }

    @Test
    void testBloquerCarte_Exception() throws Exception {
        doThrow(new RuntimeException()).when(banqueFacadeMock).bloquerCarte(anyString(), anyBoolean());
        String result = gestionCartes.bloquerCarte();
        assertEquals(Action.ERROR, result);
    }

    @Test
    void testDebloquerCarte_Succes() throws Exception {
        gestionCartes.setNumeroCarte("1111");
        String result = gestionCartes.debloquerCarte();

        verify(banqueFacadeMock).debloquerCarte("1111");
        assertEquals("Carte réactivée avec succès.", gestionCartes.getMessage());
        assertEquals(Action.SUCCESS, result);
    }

    @Test
    void testDebloquerCarte_Exception() throws Exception {
        doThrow(new RuntimeException()).when(banqueFacadeMock).debloquerCarte(anyString());
        String result = gestionCartes.debloquerCarte();
        assertEquals(Action.ERROR, result);
    }

    // ================================================================
    // ACTION 3 : Modification (Plafond & Compte lié)
    // ================================================================

    @Test
    void testFormulaireModification_Succes() {
        String numCarte = "1111";
        gestionCartes.setNumeroCarte(numCarte);

        Map<String, Compte> comptes = new HashMap<>();
        when(banqueFacadeMock.getCarte(numCarte)).thenReturn(carteMock);
        when(carteMock.getCompte()).thenReturn(compteMock);
        when(compteMock.getOwner()).thenReturn(clientMock);
        when(clientMock.getAccounts()).thenReturn(comptes);

        String result = gestionCartes.formulaireModification();

        assertEquals(Action.SUCCESS, result);
        assertEquals(carteMock, gestionCartes.getCarte());
        assertEquals(comptes, gestionCartes.getComptesClient());
    }

    @Test
    void testFormulaireModification_CarteNull() {
        // Fix: On définit un numéro de carte, sinon Mockito panique sur getCarte(null)
        gestionCartes.setNumeroCarte("UNKNOWN");
        when(banqueFacadeMock.getCarte(anyString())).thenReturn(null);
        String result = gestionCartes.formulaireModification();
        assertEquals(Action.ERROR, result);
        assertEquals("CARD_NOT_FOUND", gestionCartes.getMessage());
    }

    @Test
    void testFormulaireModification_Exception() {
        when(banqueFacadeMock.getCarte(anyString())).thenThrow(new RuntimeException());
        String result = gestionCartes.formulaireModification();
        assertEquals(Action.ERROR, result);
    }

    @Test
    void testModifierCarte_PlafondInvalide() {
        gestionCartes.setPlafond(-50);
        String result = gestionCartes.modifierCarte();
        assertEquals(Action.INPUT, result);
    }

    @Test
    void testModifierCarte_Succes_ChangementCompte() throws Exception {
        // GIVEN
        String numCarte = "CARD1";
        String ancienCpt = "CPT_OLD";
        String nouveauCpt = "CPT_NEW";

        gestionCartes.setNumeroCarte(numCarte);
        gestionCartes.setPlafond(1000.0);
        gestionCartes.setNouveauNumeroCompte(nouveauCpt);

        // Simulation pour la vérification du changement de compte
        when(banqueFacadeMock.getCarte(numCarte)).thenReturn(carteMock);
        when(carteMock.getCompte()).thenReturn(compteMock);
        when(compteMock.getNumeroCompte()).thenReturn(ancienCpt); // L'ancien compte est différent du nouveau

        // WHEN
        String result = gestionCartes.modifierCarte();

        // THEN
        verify(banqueFacadeMock).changerPlafondCarte(numCarte, 1000.0);
        verify(banqueFacadeMock).changerCompteLieCarte(numCarte, nouveauCpt); // Doit être appelé
        assertEquals(Action.SUCCESS, result);
        assertEquals("Carte modifiée avec succès.", gestionCartes.getMessage());
    }

    @Test
    void testModifierCarte_Succes_MemeCompte() throws Exception {
        // GIVEN
        String numCarte = "CARD1";
        String memeCompte = "CPT_SAME";

        gestionCartes.setNumeroCarte(numCarte);
        gestionCartes.setPlafond(1000.0);
        gestionCartes.setNouveauNumeroCompte(memeCompte);

        when(banqueFacadeMock.getCarte(numCarte)).thenReturn(carteMock);
        when(carteMock.getCompte()).thenReturn(compteMock);
        when(compteMock.getNumeroCompte()).thenReturn(memeCompte); // C'est le même !

        // WHEN
        String result = gestionCartes.modifierCarte();

        // THEN
        verify(banqueFacadeMock).changerPlafondCarte(numCarte, 1000.0);
        verify(banqueFacadeMock, never()).changerCompteLieCarte(anyString(), anyString()); // NE DOIT PAS être appelé
        assertEquals(Action.SUCCESS, result);
    }

    @Test
    void testModifierCarte_Exception() throws Exception {
        gestionCartes.setPlafond(100.0);
        doThrow(new RuntimeException()).when(banqueFacadeMock).changerPlafondCarte(anyString(), anyDouble());
        String result = gestionCartes.modifierCarte();
        assertEquals(Action.ERROR, result);
    }

    // ================================================================
    // ACTION 4 : Paiement
    // ================================================================

    @ParameterizedTest
    @CsvSource(value = {
            "NULL, Paiement CB Internet",  // Cas null
            "'', Paiement CB Internet",    // Cas vide
            "Mon Achat, Mon Achat"         // Cas nominal
    }, nullValues = "NULL")
    void testPayer_Succes_Parametre(String inputLibelle, String expectedLibelle) throws Exception {
        // GIVEN
        gestionCartes.setNumeroCarte("1234");
        gestionCartes.setMontantPaiement(50.0);
        gestionCartes.setLibellePaiement(inputLibelle);

        // WHEN
        String result = gestionCartes.payer();

        // THEN
        verify(banqueFacadeMock).payerParCarte("1234", 50.0, expectedLibelle);
        assertEquals(Action.SUCCESS, result);
    }

    @Test
    void testPayer_Succes_LibellePersonnalise() throws Exception {
        gestionCartes.setNumeroCarte("1234");
        gestionCartes.setMontantPaiement(50.0);
        gestionCartes.setLibellePaiement("Mon Achat");

        String result = gestionCartes.payer();

        verify(banqueFacadeMock).payerParCarte("1234", 50.0, "Mon Achat");
        assertEquals(Action.SUCCESS, result);
    }

    @Test
    void testPayer_MontantNegatif() {
        gestionCartes.setMontantPaiement(0.0); // ou négatif
        String result = gestionCartes.payer();
        assertEquals(Action.ERROR, result);
        assertEquals("NEGATIVEAMOUNT", gestionCartes.getMessage());
    }

    @Test
    void testPayer_FondsInsuffisants() throws Exception {
        // Fix: On définit la carte pour éviter le null dans payerParCarte(null, ...)
        gestionCartes.setNumeroCarte("1234");
        gestionCartes.setMontantPaiement(100.0);
        doThrow(new InsufficientFundsException("")).when(banqueFacadeMock).payerParCarte(anyString(), anyDouble(), anyString());

        String result = gestionCartes.payer();
        assertEquals(Action.ERROR, result);
        assertEquals("OVER_LIMIT", gestionCartes.getMessage());
    }

    @Test
    void testPayer_AutreErreur() throws Exception {
        gestionCartes.setNumeroCarte("1234");
        gestionCartes.setMontantPaiement(100.0);
        doThrow(new RuntimeException("Crash")).when(banqueFacadeMock).payerParCarte(anyString(), anyDouble(), anyString());

        String result = gestionCartes.payer();
        assertEquals(Action.ERROR, result);
        assertEquals("TECHNICAL", gestionCartes.getMessage());
    }

    // ================================================================
    // TESTS COMPLÉMENTAIRES POUR 100% BRANCH COVERAGE
    // ================================================================

    @Test
    void testFormulaireCreation_CompteVide() {
        // Couvre la branche : numeroCompte.isEmpty()
        gestionCartes.setNumeroCompte("");
        String result = gestionCartes.formulaireCreation();
        assertEquals(Action.ERROR, result);
        assertEquals("MISSING_ACCOUNT", gestionCartes.getMessage());
    }

    @Test
    void testModifierCarte_PasDeChangementDeCompte() throws Exception {
        // Couvre la branche : nouveauNumeroCompte == null
        // On ne définit PAS nouveauNumeroCompte (il reste null)
        gestionCartes.setNumeroCarte("123");
        gestionCartes.setPlafond(500.0);

        // Pas besoin de mocker le compte ou le propriétaire car on n'entrera pas dans le if complexe
        String result = gestionCartes.modifierCarte();

        assertEquals(Action.SUCCESS, result);
        verify(banqueFacadeMock).changerPlafondCarte("123", 500.0);
        // Vérifie qu'on n'a JAMAIS tenté de changer le compte
        verify(banqueFacadeMock, never()).changerCompteLieCarte(anyString(), anyString());
    }

    @Test
    void testModifierCarte_NouveauCompteVide() throws Exception {
        // Couvre la branche : !nouveauNumeroCompte.isEmpty() est faux
        gestionCartes.setNumeroCarte("123");
        gestionCartes.setPlafond(500.0);
        gestionCartes.setNouveauNumeroCompte(""); // Vide

        String result = gestionCartes.modifierCarte();

        assertEquals(Action.SUCCESS, result);
        verify(banqueFacadeMock, never()).changerCompteLieCarte(anyString(), anyString());
    }

    @Test
    void testPayer_LibelleVide() throws Exception {
        // Couvre la branche : libellePaiement.isEmpty()
        gestionCartes.setNumeroCarte("1234");
        gestionCartes.setMontantPaiement(50.0);
        gestionCartes.setLibellePaiement(""); // Vide

        String result = gestionCartes.payer();

        // Doit mettre le libellé par défaut
        verify(banqueFacadeMock).payerParCarte("1234", 50.0, "Paiement CB Internet");
        assertEquals(Action.SUCCESS, result);
    }

    @Test
    void testFormulaireModification_CarteSansCompte() {
        // Couvre la branche : carte.getCompte() == null
        String numCarte = "ORPHELIN";
        gestionCartes.setNumeroCarte(numCarte);

        // On retourne une carte mockée
        when(banqueFacadeMock.getCarte(numCarte)).thenReturn(carteMock);
        // MAIS cette carte n'a pas de compte (retourne null par défaut)
        when(carteMock.getCompte()).thenReturn(null);

        String result = gestionCartes.formulaireModification();

        assertEquals(Action.SUCCESS, result);
        // Vérifie que la carte est chargée mais que la liste des comptes est restée vide/null
        assertEquals(carteMock, gestionCartes.getCarte());
        assertNull(gestionCartes.getComptesClient());
    }

    @Test
    void testFormulaireModification_CompteSansProprietaire() {
        // Cas : La carte a un compte, mais ce compte n'a pas de client associé (Owner == null)
        String numCarte = "1234";
        gestionCartes.setNumeroCarte(numCarte);

        // 1. La carte existe
        when(banqueFacadeMock.getCarte(numCarte)).thenReturn(carteMock);
        // 2. Le compte existe
        when(carteMock.getCompte()).thenReturn(compteMock);
        // 3. MAIS le propriétaire est null
        when(compteMock.getOwner()).thenReturn(null);

        String result = gestionCartes.formulaireModification();

        assertEquals(Action.SUCCESS, result);
        // La carte est chargée
        assertEquals(carteMock, gestionCartes.getCarte());
        // Mais la liste des comptes n'est pas chargée (car pas de propriétaire)
        assertNull(gestionCartes.getComptesClient());
    }

    // ================================================================
    // Getters / Setters COMPLETS pour 100% Coverage
    // ================================================================
    @Test
    void testGettersSetters_Exhaustif() {
        // 1. NumeroCompte
        gestionCartes.setNumeroCompte("TEST_CPT");
        assertEquals("TEST_CPT", gestionCartes.getNumeroCompte());

        // 2. NumeroCarte
        gestionCartes.setNumeroCarte("TEST_CARD");
        assertEquals("TEST_CARD", gestionCartes.getNumeroCarte());

        // 3. TypeDebit
        gestionCartes.setTypeDebit("DIFFERE");
        assertEquals("DIFFERE", gestionCartes.getTypeDebit());

        // 4. Plafond
        gestionCartes.setPlafond(123.45);
        assertEquals(123.45, gestionCartes.getPlafond(), 0.001);

        // 5. Definitif
        gestionCartes.setDefinitif(true);
        assertTrue(gestionCartes.isDefinitif());

        // 6. MontantPaiement
        gestionCartes.setMontantPaiement(99.99);
        assertEquals(99.99, gestionCartes.getMontantPaiement(), 0.001);

        // 7. LibellePaiement
        gestionCartes.setLibellePaiement("Achat");
        assertEquals("Achat", gestionCartes.getLibellePaiement());

        // 8. NouveauNumeroCompte
        gestionCartes.setNouveauNumeroCompte("NEW_CPT");
        assertEquals("NEW_CPT", gestionCartes.getNouveauNumeroCompte());

        // 9. Message
        gestionCartes.setMessage("Hello");
        assertEquals("Hello", gestionCartes.getMessage());

        // 10. Carte (Objet)
        gestionCartes.setCarte(carteMock);
        assertEquals(carteMock, gestionCartes.getCarte());

        // 11. ComptesClient (Map)
        Map<String, Compte> map = new HashMap<>();
        gestionCartes.setComptesClient(map);
        assertEquals(map, gestionCartes.getComptesClient());
    }
}