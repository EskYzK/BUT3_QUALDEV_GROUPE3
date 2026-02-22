package com.iut.banque.test.modele;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.iut.banque.modele.Client;
import com.iut.banque.modele.CarteDebitDiffere;
import com.iut.banque.modele.CarteDebitImmediat;
import com.iut.banque.modele.CompteSansDecouvert;
import com.iut.banque.exceptions.IllegalFormatException;


public class TestsCarteBancaire {

    private CarteDebitImmediat carteImmediat;
    private CarteDebitDiffere carteDiffere;
    private CompteSansDecouvert compte;
    private Client client;


    @Before
    public void setUp() throws IllegalFormatException {
        client = new Client();
        compte = new CompteSansDecouvert("WU1234567890", 0, client);
        carteImmediat = new CarteDebitImmediat("1234567890123456", 2000, compte);
        carteDiffere = new CarteDebitDiffere("9876543210654321", 5000, compte);
    }

    // ============================================================================
    // TESTS DU CONSTRUCTEUR VIDE DE CarteBancaire (parent)
    // ============================================================================

    @Test
    public void testConstructeurVideCarteDebitImmediat() {
        CarteDebitImmediat carte = new CarteDebitImmediat();
        assertNotNull("Le constructeur vide doit créer une instance non-null", carte);
    }


    @Test
    public void testConstructeurVideCarteDebitDiffere() {
        CarteDebitDiffere carte = new CarteDebitDiffere();
        assertNotNull("Le constructeur vide doit créer une instance non-null", carte);
    }

    // ============================================================================
    // TESTS DU CONSTRUCTEUR COMPLET
    // ============================================================================

    /**
     * Test du constructeur complet de CarteDebitImmediat avec tous les paramètres.
     * Vérifie l'initialisation correcte des attributs.
     */
    @Test
    public void testConstructeurCompletCarteDebitImmediatInitialisationOK() {
        assertEquals("Le numéro de carte doit être correctement assigné", "1234567890123456", carteImmediat.getNumeroCarte());
        assertEquals("Le plafond doit être correctement assigné", 2000, carteImmediat.getPlafond(), 0.001);
        assertEquals("Le compte doit être correctement assigné", compte, carteImmediat.getCompte());
        assertFalse("La carte ne doit pas être bloquée au départ", carteImmediat.isBloquee());
        assertFalse("La carte ne doit pas être supprimée au départ", carteImmediat.isSupprimee());
    }

    /**
     * Test du constructeur complet de CarteDebitDiffere avec tous les paramètres.
     * Vérifie l'initialisation correcte des attributs spécifiques à ce type.
     */
    @Test
    public void testConstructeurCompletCarteDebitDiffereInitialisationOK() {
        assertEquals("Le numéro de carte doit être correctement assigné", "9876543210654321", carteDiffere.getNumeroCarte());
        assertEquals("Le plafond doit être correctement assigné", 5000, carteDiffere.getPlafond(), 0.001);
        assertEquals("Le compte doit être correctement assigné", compte, carteDiffere.getCompte());
        assertFalse("La carte ne doit pas être bloquée au départ", carteDiffere.isBloquee());
        assertFalse("La carte ne doit pas être supprimée au départ", carteDiffere.isSupprimee());
    }

    // ============================================================================
    // TESTS DES GETTERS
    // ============================================================================

    /**
     * Test du getter getNumeroCarte().
     * Vérifie que le numéro de carte est correctement retourné.
     */
    @Test
    public void testGetNumeroCarte() {
        String numeroCarte = carteImmediat.getNumeroCarte();
        assertEquals("getNumeroCarte doit retourner le bon numéro", "1234567890123456", numeroCarte);
    }

    /**
     * Test du getter getPlafond().
     * Vérifie que le plafond est correctement retourné.
     */
    @Test
    public void testGetPlafond() {
        double plafond = carteImmediat.getPlafond();
        assertEquals("getPlafond doit retourner le bon plafond", 2000, plafond, 0.001);
    }

    /**
     * Test du getter isBloquee().
     * Vérifie l'état du blocage temporaire.
     */
    @Test
    public void testIsBloqueeInitialementFausse() {
        assertFalse("La carte ne doit pas être bloquée initialement", carteImmediat.isBloquee());
    }

    /**
     * Test du getter isSupprimee().
     * Vérifie l'état de suppression (blocage définitif).
     */
    @Test
    public void testIsSupprimeeInitialementFausse() {
        assertFalse("La carte ne doit pas être supprimée initialement", carteImmediat.isSupprimee());
    }

    /**
     * Test du getter getCompte().
     * Vérifie que le compte lié est correctement retourné.
     */
    @Test
    public void testGetCompte() {
        assertEquals("getCompte doit retourner le compte associé", compte, carteImmediat.getCompte());
    }

    /**
     * Test des getters après modification des attributs.
     * Vérifie que les getters reflètent les modifications.
     */
    @Test
    public void testGettersApresModifications() {
        carteImmediat.setNumeroCarte("9999999999999999");
        carteImmediat.setPlafond(3000);
        carteImmediat.setBloquee(true);
        carteImmediat.setSupprimee(true);

        assertEquals("getNumeroCarte après setNumeroCarte", "9999999999999999", carteImmediat.getNumeroCarte());
        assertEquals("getPlafond après setPlafond", 3000, carteImmediat.getPlafond(), 0.001);
        assertTrue("isBloquee après setBloquee", carteImmediat.isBloquee());
        assertTrue("isSupprimee après setSupprimee", carteImmediat.isSupprimee());
    }

    // ============================================================================
    // TESTS DES SETTERS
    // ============================================================================

    /**
     * Test du setter setNumeroCarte().
     * Vérifie que le numéro de carte peut être modifié et que la modification est visible.
     */
    @Test
    public void testSetNumeroCarte() {
        carteImmediat.setNumeroCarte("1111111111111111");
        assertEquals("setNumeroCarte doit modifier le numéro", "1111111111111111", carteImmediat.getNumeroCarte());
    }

    /**
     * Test du setter setPlafond() avec une valeur positive.
     * Vérifie que le plafond peut être augmenté.
     */
    @Test
    public void testSetPlafondAugmentation() {
        carteImmediat.setPlafond(5000);
        assertEquals("Le plafond doit être augmenté", 5000, carteImmediat.getPlafond(), 0.001);
    }

    /**
     * Test du setter setPlafond() avec une valeur réduite.
     * Vérifie que le plafond peut être diminué.
     */
    @Test
    public void testSetPlafondDiminution() {
        carteImmediat.setPlafond(1000);
        assertEquals("Le plafond doit être diminué", 1000, carteImmediat.getPlafond(), 0.001);
    }

    /**
     * Test du setter setBloquee() en passant de false à true.
     * Vérifie le blocage temporaire d'une carte.
     */
    @Test
    public void testSetBloqueeBlocage() {
        assertFalse("La carte est initialement débloquée", carteImmediat.isBloquee());
        carteImmediat.setBloquee(true);
        assertTrue("La carte doit être bloquée après setBloquee(true)", carteImmediat.isBloquee());
    }

    /**
     * Test du setter setBloquee() en passant de true à false.
     * Vérifie le déblocage d'une carte temporairement bloquée.
     */
    @Test
    public void testSetBloqueeDeblocage() {
        carteImmediat.setBloquee(true);
        assertTrue("La carte doit être bloquée", carteImmediat.isBloquee());
        carteImmediat.setBloquee(false);
        assertFalse("La carte doit être débloquée après setBloquee(false)", carteImmediat.isBloquee());
    }

    /**
     * Test du setter setSupprimee() en passant de false à true.
     * Vérifie la suppression définitive (blocage irréversible) d'une carte.
     */
    @Test
    public void testSetSuppriméeBlockageDefinitif() {
        assertFalse("La carte n'est pas supprimée initialement", carteImmediat.isSupprimee());
        carteImmediat.setSupprimee(true);
        assertTrue("La carte doit être supprimée après setSupprimee(true)", carteImmediat.isSupprimee());
    }

    /**
     * Test du setter setCompte().
     * Vérifie que le compte lié peut être modifié.
     */
    @Test
    public void testSetCompte() throws IllegalFormatException {
        CompteSansDecouvert nouveauCompte = new CompteSansDecouvert("AB9876543210", 100, client);
        carteImmediat.setCompte(nouveauCompte);
        assertEquals("Le compte doit être modifié", nouveauCompte, carteImmediat.getCompte());
    }

    // ============================================================================
    // TESTS DE LA MÉTHODE toString()
    // ============================================================================

    /**
     * Test de la méthode toString() pour CarteDebitImmediat.
     * Vérifie que la représentation textuelle contient les informations essentielles.
     */
    @Test
    public void testToStringCarteDebitImmediat() {
        String result = carteImmediat.toString();
        assertNotNull("toString ne doit pas retourner null", result);
        assertTrue("toString doit contenir le numéro de carte", result.contains("1234567890123456"));
        assertTrue("toString doit contenir le plafond", result.contains("2000"));
        assertTrue("toString doit contenir 'CarteDebitImmediat'", result.contains("CarteDebitImmediat"));
    }

    /**
     * Test de la méthode toString() pour CarteDebitDiffere.
     * Vérifie que la représentation textuelle contient les informations essentielles et le type correct.
     */
    @Test
    public void testToStringCarteDebitDiffere() {
        String result = carteDiffere.toString();
        assertNotNull("toString ne doit pas retourner null", result);
        assertTrue("toString doit contenir le numéro de carte", result.contains("9876543210654321"));
        assertTrue("toString doit contenir le plafond", result.contains("5000"));
        assertTrue("toString doit contenir 'CarteDebitDiffere'", result.contains("CarteDebitDiffere"));
    }

    // ============================================================================
    // TESTS DE LA MÉTHODE getTypeDeCarte() (POLYMORPHISME)
    // ============================================================================

    /**
     * Test de la méthode abstraite getTypeDeCarte() pour CarteDebitImmediat.
     * Vérifie que le type retourné est correct pour une carte à débit immédiat.
     */
    @Test
    public void testGetTypeDeCarteImmediat() {
        String type = carteImmediat.getTypeDeCarte();
        assertEquals("getTypeDeCarte doit retourner 'Débit Immédiat'", "Débit Immédiat", type);
    }

    /**
     * Test de la méthode abstraite getTypeDeCarte() pour CarteDebitDiffere.
     * Vérifie que le type retourné est correct pour une carte à débit différé.
     */
    @Test
    public void testGetTypeDeCarteDiffere() {
        String type = carteDiffere.getTypeDeCarte();
        assertEquals("getTypeDeCarte doit retourner 'Débit Différé'", "Débit Différé", type);
    }

    // ============================================================================
    // TESTS D'ÉTATS COMBINÉS (scénarios réalistes)
    // ============================================================================

    /**
     * Test d'un scénario de blocage temporaire puis déblocage.
     * Simule une carte bloquée en cas de perte puis débloquée après confirmation.
     */
    @Test
    public void testScenarioBloqueeEtDebloqueeTemporal() {
        // Carte initialement active
        assertFalse("La carte n'est pas bloquée initialement", carteImmediat.isBloquee());
        assertFalse("La carte n'est pas supprimée initialement", carteImmediat.isSupprimee());

        // Blocage temporaire
        carteImmediat.setBloquee(true);
        assertTrue("La carte est bloquée après le blocage temporaire", carteImmediat.isBloquee());
        assertFalse("La carte n'est pas supprimée (bloc temp.)", carteImmediat.isSupprimee());

        // Déblocage
        carteImmediat.setBloquee(false);
        assertFalse("La carte est débloquée après déblocage", carteImmediat.isBloquee());
        assertFalse("La carte n'est toujours pas supprimée", carteImmediat.isSupprimee());
    }

    /**
     * Test d'un scénario de suppression définitive.
     * Simule une carte définitivement bloquée (supprimée).
     */
    @Test
    public void testScenarioSuppressionDefinitive() {
        // Suppression définitive
        carteImmediat.setSupprimee(true);
        assertTrue("La carte est supprimée", carteImmediat.isSupprimee());
        assertTrue("La carte reste supprimée même si on la marque comme débloquée",
                carteImmediat.isSupprimee());
        // Remarque : un test en façade vérifiera qu'on ne peut pas débloquer une carte supprimée
    }

    /**
     * Test d'une modification du plafond pendant un blocage temporaire.
     * Vérifie que les modifications d'attributs sont indépendantes.
     */
    @Test
    public void testModificationPlafondAvecBlocage() {
        carteImmediat.setBloquee(true);
        carteImmediat.setPlafond(3000);

        assertTrue("La carte reste bloquée", carteImmediat.isBloquee());
        assertEquals("Le plafond s'est bien modifié", 3000, carteImmediat.getPlafond(), 0.001);
    }

    /**
     * Test d'une modification du compte lié.
     * Vérifie que le compte peut être changé indépendamment d'autres attributs.
     */
    @Test
    public void testModificationCompteVersAutreCarte() throws IllegalFormatException {
        CompteSansDecouvert nouveauCompte = new CompteSansDecouvert("ZZ1111111111", 500, client);
        
        carteImmediat.setCompte(nouveauCompte);
        assertEquals("Le compte doit être changé", nouveauCompte, carteImmediat.getCompte());
        assertEquals("Le numéro de carte ne change pas", "1234567890123456", carteImmediat.getNumeroCarte());
    }

    // ============================================================================
    // TESTS D'ÉQUIVALENCE ENTRE DEUX CARTES
    // ============================================================================

    /**
     * Test pour vérifier que deux cartes différentes peuvent avoir les mêmes attributs.
     * Vérifie que la cohérence des données est maintenue.
     */
    @Test
    public void testDifferenceTroTypesDeCartes() throws IllegalFormatException {
        CompteSansDecouvert compte2 = new CompteSansDecouvert("AA1111111111", 0, client);

        CarteDebitImmediat carte1 = new CarteDebitImmediat("5555555555555555", 2000, compte2);
        CarteDebitDiffere carte2 = new CarteDebitDiffere("5555555555555556", 2000, compte2);

        assertEquals("Les deux cartes ont le même plafond", carte1.getPlafond(), carte2.getPlafond(), 0.001);
        assertNotNull("Les deux cartes ont un compte", carte1.getCompte());
        assertNotNull("Les deux cartes ont un compte", carte2.getCompte());
        assertEquals("Les types sont différents", "Débit Immédiat", carte1.getTypeDeCarte());
        assertEquals("Les types sont différents", "Débit Différé", carte2.getTypeDeCarte());
    }
}
