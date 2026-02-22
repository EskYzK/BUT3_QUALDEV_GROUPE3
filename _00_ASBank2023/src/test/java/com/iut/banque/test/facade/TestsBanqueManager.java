package com.iut.banque.test.facade;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.facade.BanqueManager;

//@RunWith indique à JUnit de prendre le class runner de Spirng
@RunWith(SpringJUnit4ClassRunner.class)
// @ContextConfiguration permet de charger le context utilisé pendant les tests.
// Par défault (si aucun argument n'est précisé), cherche le fichier
/// src/com/iut/banque/test/TestsDaoHibernate-context.xml
@ContextConfiguration("/test/resources/TestsBanqueManager-context.xml")
@Transactional("transactionManager")
public class TestsBanqueManager {

	@Autowired
	private BanqueManager bm;

	// Tests de par rapport à l'ajout d'un client
	@Test
	public void TestCreationDunClient() throws Exception {
		bm.loadAllClients();
		bm.createClient("t.test1", "password", "test1nom", "test1prenom", "test town", true, "mail@test.test", "4242424242");
	}

<<<<<<< HEAD
	@Test(expected = IllegalOperationException.class)
	public void TestCreationDunClientAvecDeuxNumerosDeCompteIdentiques() throws Exception {
		bm.loadAllClients();
		bm.createClient("t.test1", "password", "test1nom", "test1prenom", "test town", true, "mail@test.test", "0101010101");
=======
	@Test
	public void TestCreationDunClientAvecDeuxNumerosDeCompteIdentiques() {
		try {
			bm.loadAllClients();
			bm.createClient("t.test1", "password", "test1nom", "test1prenom", "test town", true, "mail@test.test", "0101010101");
			fail();
		} catch (IllegalOperationException e) {
            // Exception déjà levée
		} catch (Exception te) {
			te.printStackTrace();
			fail("Une Exception " + te.getClass().getSimpleName() + " a été récupérée");
		}
>>>>>>> fa0037a73f51f05bd8c593cf0012b13785e51eab
	}

	// Tests par rapport à la suppression de comptes
	@Test
	public void TestSuppressionDunCompteAvecDecouvertAvecSoldeZero() throws Exception {
		bm.deleteAccount(bm.getAccountById("CADV000000"));
	}

	@Test(expected = IllegalOperationException.class)
	public void TestSuppressionDunCompteAvecDecouvertAvecSoldeDifferentDeZero() throws Exception {
		bm.deleteAccount(bm.getAccountById("CADNV00000"));
	}

	@Test
<<<<<<< HEAD
	public void TestSuppressionDunCompteSansDecouvertAvecSoldeZero() throws Exception {
		bm.deleteAccount(bm.getAccountById("CSDV000000"));
	}

	@Test(expected = IllegalOperationException.class)
	public void TestSuppressionDunCompteSansDecouvertAvecSoldeDifferentDeZero() throws Exception {
		bm.deleteAccount(bm.getAccountById("CSDNV00000"));
=======
	public void TestSuppressionDunCompteAvecDecouvertAvecSoldeDifferentDeZero() {
		try {
			bm.deleteAccount(bm.getAccountById("CADNV00000"));
			fail("Une IllegalOperationException aurait dû être récupérée");
		} catch (IllegalOperationException e) {
            // Exception déjà levée
		} catch (Exception te) {
			fail("Une Exception " + te.getClass().getSimpleName() + " a été récupérée");
		}
	}

	@Test
	public void TestSuppressionDunCompteSansDecouvertAvecSoldeZero() {
		try {
			bm.deleteAccount(bm.getAccountById("CSDV000000"));
		} catch (IllegalOperationException e) {
			e.printStackTrace();
			fail("IllegalOperationException récupérée : " + e.getStackTrace());
		} catch (Exception te) {
			fail("Une Exception " + te.getClass().getSimpleName() + " a été récupérée");
		}
	}

	@Test
	public void TestSuppressionDunCompteSansDecouvertAvecSoldeDifferentDeZero() {
		try {
			bm.deleteAccount(bm.getAccountById("CSDNV00000"));
			fail("Une IllegalOperationException aurait dû être récupérée");
		} catch (IllegalOperationException e) {
            // Exception déjà levée
		} catch (Exception te) {
			fail("Une Exception " + te.getClass().getSimpleName() + " a été récupérée");
		}
>>>>>>> fa0037a73f51f05bd8c593cf0012b13785e51eab
	}

	// Tests en rapport avec la suppression d'utilisateurs
	@Test
	public void TestSuppressionDunUtilisateurSansCompte() throws Exception {
		bm.loadAllClients();
		bm.deleteUser(bm.getUserById("g.pasdecompte"));
	}

	@Test(expected = IllegalOperationException.class)
	public void TestSuppressionDuDernierManagerDeLaBaseDeDonnees() throws Exception {
		bm.loadAllGestionnaires();
<<<<<<< HEAD
		bm.deleteUser(bm.getUserById("admin"));
=======
		try {
			bm.deleteUser(bm.getUserById("admin"));
			fail("Une IllegalOperationException aurait dû être récupérée");
		} catch (IllegalOperationException e) {
            // Exception déjà levée
		} catch (Exception te) {
			te.printStackTrace();
			fail("Une Exception " + te.getClass().getSimpleName() + " a été récupérée");
		}
>>>>>>> fa0037a73f51f05bd8c593cf0012b13785e51eab
	}

	@Test
	public void TestSuppressionDunClientAvecComptesDeSoldeZero() throws Exception {
		bm.loadAllClients();
		bm.deleteUser(bm.getUserById("g.descomptesvides"));
		if (bm.getAccountById("KL4589219196") != null || bm.getAccountById("KO7845154956") != null) {
			fail("Les comptes de l'utilisateur sont encore présents dans la base de données");
		}
	}

<<<<<<< HEAD
	@Test(expected = IllegalOperationException.class)
	public void TestSuppressionDunClientAvecUnCompteDeSoldePositif() throws Exception {
		bm.deleteUser(bm.getUserById("j.doe1"));
	}

	@Test(expected = IllegalOperationException.class)
	public void TestSuppressionDunClientAvecUnCompteAvecDecouvertDeSoldeNegatif() throws Exception {
		bm.deleteUser(bm.getUserById("j.doe1"));
=======
	@Test
	public void TestSuppressionDunClientAvecUnCompteDeSoldePositif() {
		try {
			bm.deleteUser(bm.getUserById("j.doe1"));
			fail("Une IllegalOperationException aurait dû être récupérée");
		} catch (IllegalOperationException e) {
            // Exception déjà levée
		} catch (Exception te) {
			fail("Une Exception " + te.getClass().getSimpleName() + " a été récupérée");
		}
	}

	@Test
	public void TestSuppressionDunClientAvecUnCompteAvecDecouvertDeSoldeNegatif() {
		try {
			bm.deleteUser(bm.getUserById("j.doe1"));
			fail("Une IllegalOperationException aurait dû être récupérée");
		} catch (IllegalOperationException e) {
            // Exception déjà levée
		} catch (Exception te) {
			fail("Une Exception " + te.getClass().getSimpleName() + " a été récupérée");
		}
>>>>>>> fa0037a73f51f05bd8c593cf0012b13785e51eab
	}

}
