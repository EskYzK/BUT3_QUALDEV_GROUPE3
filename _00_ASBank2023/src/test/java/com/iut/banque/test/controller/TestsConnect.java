package com.iut.banque.test.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.iut.banque.dao.DaoHibernate;
import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.exceptions.TechnicalException;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;
import com.iut.banque.modele.CompteAvecDecouvert;
import com.iut.banque.modele.CompteSansDecouvert;
import com.iut.banque.modele.Gestionnaire;
import com.iut.banque.modele.Utilisateur;
import com.iut.banque.security.PasswordHasher;
import com.iut.banque.controller.Connect;


// @RunWith indique à JUnit de prendre le class runner de Spirng
@RunWith(SpringJUnit4ClassRunner.class)
// @ContextConfiguration permet de charger le context utilisé pendant les tests.
// Par défault (si aucun argument n'est précisé), cherche le fichier
// TestsDaoHibernate-context.xml dans le même dosssier que la classe
@ContextConfiguration("/test/resources/TestsDaoHibernate-context.xml")
@Transactional("transactionManager")
public class TestsConnect {

    @Autowired
    private Connect connect;

    @Test
    public void testLogout(){
        try {
            String result = connect.logout();
            assertEquals("SUCCESS", result);
            // l'action ne doit pas enregistrer d'erreurs lors du logout
            assertTrue("Aucune erreur ne doit être présente après logout",
                    connect.getActionErrors().isEmpty());
        } catch (Exception e) {
            fail("Logout ne doit pas lever d'exception : " + e.getMessage());
        }
    }

    @Test
    public void testLoginParametresNuls(){
        // Struts a besoin d'une request pour créer la session dans login()
        org.springframework.mock.web.MockHttpServletRequest req =
                new org.springframework.mock.web.MockHttpServletRequest();
        org.apache.struts2.ServletActionContext.setRequest(req);

        // Cas: identifiants non fournis
        connect.setUserCde(null);
        connect.setUserPwd(null);

        String result = connect.login();
        assertEquals("ERROR", result);
    }

    @Test
    public void testLoginDonnéesInconnues(){
        org.springframework.mock.web.MockHttpServletRequest req =
                new org.springframework.mock.web.MockHttpServletRequest();
        org.apache.struts2.ServletActionContext.setRequest(req);

        connect.setUserCde("unknown.user");
        connect.setUserPwd("badpass");

        String result = connect.login();
        assertEquals("ERROR", result);
    }
}
