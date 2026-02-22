package com.iut.banque.security;

import com.iut.banque.dao.DaoHibernate;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.util.logging.Logger;


public class RunHasher {
    private static final Logger logger = Logger.getLogger(RunHasher.class.getName());

    public static void main(String[] args) {
        // Charge le contexte Spring pour récupérer le bean DaoHibernate
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("test/resources/TestsDaoHibernate-context.xml");

        DaoHibernate dao = context.getBean(DaoHibernate.class);

        // Rehash manuel de tous les utilisateurs
        PasswordHasher.rehashAllUsers(dao);

        context.close();
        logger.info("Tous les mots de passe ont été rehashés !");
    }
}
