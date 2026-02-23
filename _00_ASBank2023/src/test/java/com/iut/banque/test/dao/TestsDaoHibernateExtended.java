package com.iut.banque.test.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.iut.banque.interfaces.IDao;
import com.iut.banque.modele.CarteBancaire;
import com.iut.banque.modele.CarteDebitImmediat;
import com.iut.banque.modele.CarteDebitDiffere;
import com.iut.banque.modele.Compte;
import com.iut.banque.modele.Operation;
import com.iut.banque.modele.Utilisateur;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test/resources/TestsDaoHibernate-context.xml")
@Transactional("transactionManager")
public class TestsDaoHibernateExtended {

    @Autowired
    private IDao dao;

    private Compte compteTest;

    @Before
    public void setUp() {
        // On récupère un compte existant dans le script SQL de test
        // Assurez-vous qu'un compte comme "CADV000000" existe bien dans dumpSQL_JUnitTest.sql
        compteTest = dao.getAccountById("CADV000000");
    }

    // --------------------------------------------------------
    // TESTS DES CARTES BANCAIRES
    // --------------------------------------------------------

    @Test
    public void testCreateEtGetCarteBancaire() {
        CarteDebitImmediat nouvelleCarte = new CarteDebitImmediat("1111222233334444", 1500.0, compteTest);

        // Création
        dao.createCarteBancaire(nouvelleCarte);

        // Récupération
        CarteBancaire carteRecuperee = dao.getCarteBancaire("1111222233334444");

        assertNotNull("La carte devrait exister en base", carteRecuperee);
        assertEquals("1111222233334444", carteRecuperee.getNumeroCarte());
        assertEquals(1500.0, carteRecuperee.getPlafond(), 0.001);
        assertTrue(carteRecuperee instanceof CarteDebitImmediat);
    }

    @Test
    public void testUpdateCarteBancaire() {
        CarteDebitDiffere carte = new CarteDebitDiffere("9999888877776666", 2000.0, compteTest);
        dao.createCarteBancaire(carte);

        // Modification
        carte.setPlafond(3000.0);
        carte.setBloquee(true);
        dao.updateCarteBancaire(carte);

        // Vérification en base
        CarteBancaire carteModifiee = dao.getCarteBancaire("9999888877776666");
        assertEquals(3000.0, carteModifiee.getPlafond(), 0.001);
        assertTrue(carteModifiee.isBloquee());
    }

    // --------------------------------------------------------
    // TESTS DES OPÉRATIONS ET DU CALCUL DES PLAFONDS
    // --------------------------------------------------------

    @Test
    public void testCreateOperation() {
        Operation op = new Operation("Achat Test", -50.0, new Date(), "CB_IMDT", compteTest, null);
        Operation savedOp = dao.createOperation(op);

        assertNotNull("L'opération doit avoir un ID généré", savedOp);
        assertTrue("L'ID doit être > 0", savedOp.getIdOperation() > 0);
        assertEquals("Achat Test", savedOp.getLibelle());
    }

    @Test
    public void testGetMontantTotalDepensesDifferees() {
        CarteDebitDiffere carte = new CarteDebitDiffere("CARD_DIFF_TEST", 1000.0, compteTest);
        dao.createCarteBancaire(carte);

        Calendar cal = Calendar.getInstance();
        Date milieuMois = cal.getTime();

        // On simule une dépense différée
        dao.createOperation(new Operation("Achat Différé", -200.0, milieuMois, "CB_DIFF", compteTest, carte));
        // On simule une dépense immédiate (ne doit pas être comptée ici)
        dao.createOperation(new Operation("Achat Immédiat", -50.0, milieuMois, "CB_IMDT", compteTest, carte));

        // Plage de dates très large pour être sûr d'inclure les opérations
        cal.add(Calendar.DAY_OF_YEAR, -50);
        Date debut = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 100);
        Date fin = cal.getTime();

        // Le total des dépenses DIFFÉRÉES doit être de 200.0 (valeur absolue)
        double totalDiff = dao.getMontantTotalDepensesDifferees(compteTest.getNumeroCompte(), debut, fin);
        assertEquals(200.0, totalDiff, 0.001);
    }

    // --------------------------------------------------------
    // TESTS DES TOKENS MOT DE PASSE (Email / ResetToken)
    // --------------------------------------------------------

    @Test
    public void testGetUserByEmail() {
        // Supposons que l'admin (admin@asbank.com) existe dans le dump de test
        // Sinon, on peut créer un utilisateur à la volée
        Utilisateur user = dao.getUserById("admin");
        if (user != null) {
            user.setEmail("admin-test@mail.com");
            dao.updateUser(user);

            Utilisateur found = dao.getUserByEmail("admin-test@mail.com");
            assertNotNull(found);
            assertEquals("admin", found.getUserId());
        }
    }

    @Test
    public void testGetUserByToken() {
        Utilisateur user = dao.getUserById("admin");
        if (user != null) {
            user.setResetToken("MY_SUPER_TOKEN_123");
            dao.updateUser(user);

            Utilisateur found = dao.getUserByToken("MY_SUPER_TOKEN_123");
            assertNotNull(found);
            assertEquals("admin", found.getUserId());

            // Test d'un faux token
            assertNull(dao.getUserByToken("TOKEN_INVALIDE"));
        }
    }
}