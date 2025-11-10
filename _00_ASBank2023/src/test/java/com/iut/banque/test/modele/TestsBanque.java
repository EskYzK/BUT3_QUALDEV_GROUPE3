package com.iut.banque.test.modele;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.exceptions.InsufficientFundsException;
import com.iut.banque.modele.Banque;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;
import com.iut.banque.modele.CompteAvecDecouvert;
import com.iut.banque.modele.Gestionnaire;

public class TestsBanque {

    private Banque banque;
    private Compte mockCompte;
    private CompteAvecDecouvert mockCompteDecouvert;
    private Map<String, Client> clients;
    private Map<String, Gestionnaire> gestionnaires;
    private Map<String, Compte> comptes;

    @Before
    public void setUp() {
        banque = new Banque();
        mockCompte = mock(Compte.class);
        mockCompteDecouvert = mock(CompteAvecDecouvert.class);

        clients = new HashMap<>();
        gestionnaires = new HashMap<>();
        comptes = new HashMap<>();

        banque.setClients(clients);
        banque.setGestionnaires(gestionnaires);
        banque.setAccounts(comptes);
    }

    /**
     * Test des getters et setters de maps
     */
    @Test
    public void testSettersEtGetters() {
        assertEquals(clients, banque.getClients());
        assertEquals(gestionnaires, banque.getGestionnaires());
        assertEquals(comptes, banque.getAccounts());
    }

    /**
     * Test de la méthode debiter()
     */
    @Test
    public void testDebiterCompteOK() throws Exception {
        doNothing().when(mockCompte).debiter(100.0);
        banque.debiter(mockCompte, 100.0);
        verify(mockCompte).debiter(100.0);
    }

    @Test
    public void testDebiterCompteInsufficientFunds() throws Exception {
        doThrow(new InsufficientFundsException()).when(mockCompte).debiter(100.0);
        try {
            banque.debiter(mockCompte, 100.0);
            fail("Une InsufficientFundsException aurait dû être levée !");
        } catch (InsufficientFundsException e) {
            // OK attendu
        }
    }

    @Test
    public void testDebiterCompteIllegalFormat() throws Exception {
        doThrow(new IllegalFormatException()).when(mockCompte).debiter(50.0);
        try {
            banque.debiter(mockCompte, 50.0);
            fail("Une IllegalFormatException aurait dû être levée !");
        } catch (IllegalFormatException e) {
            // OK attendu
        }
    }

    /**
     * Test de la méthode crediter()
     */
    @Test
    public void testCrediterCompteOK() throws Exception {
        doNothing().when(mockCompte).crediter(100.0);
        banque.crediter(mockCompte, 100.0);
        verify(mockCompte).crediter(100.0);
    }

    @Test
    public void testCrediterCompteIllegalFormat() throws Exception {
        doThrow(new IllegalFormatException()).when(mockCompte).crediter(20.0);
        try {
            banque.crediter(mockCompte, 20.0);
            fail("Une IllegalFormatException aurait dû être levée !");
        } catch (IllegalFormatException e) {
            // OK attendu
        }
    }

    /**
     * Test de la méthode deleteUser()
     */
    @Test
    public void testDeleteUser() {
        Client client = mock(Client.class);
        clients.put("user123", client);

        banque.deleteUser("user123");

        assertFalse(clients.containsKey("user123"));
    }

    /**
     * Test de la méthode changeDecouvert()
     */
    @Test
    public void testChangeDecouvertOK() throws Exception {
        doNothing().when(mockCompteDecouvert).setDecouvertAutorise(500.0);
        banque.changeDecouvert(mockCompteDecouvert, 500.0);
        verify(mockCompteDecouvert).setDecouvertAutorise(500.0);
    }

    @Test
    public void testChangeDecouvertIllegalFormat() throws Exception {
        doThrow(new IllegalFormatException()).when(mockCompteDecouvert).setDecouvertAutorise(-100.0);
        try {
            banque.changeDecouvert(mockCompteDecouvert, -100.0);
            fail("Une IllegalFormatException aurait dû être levée !");
        } catch (IllegalFormatException e) {
            // OK attendu
        }
    }

    @Test
    public void testChangeDecouvertIllegalOperation() throws Exception {
        doThrow(new IllegalOperationException()).when(mockCompteDecouvert).setDecouvertAutorise(9999.0);
        try {
            banque.changeDecouvert(mockCompteDecouvert, 9999.0);
            fail("Une IllegalOperationException aurait dû être levée !");
        } catch (IllegalOperationException e) {
            // OK attendu
        }
    }
}