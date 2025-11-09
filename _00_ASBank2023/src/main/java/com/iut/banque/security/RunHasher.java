package com.iut.banque.security;

import com.iut.banque.dao.DaoHibernate;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RunHasher {
    public static void main(String[] args) {
        // Charge le contexte Spring pour récupérer le bean DaoHibernate
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("test/resources/TestsDaoHibernate-context.xml");

        DaoHibernate dao = context.getBean(DaoHibernate.class);

        // Rehash manuel de tous les utilisateurs
        PasswordHasher.rehashAllUsers(dao);

        context.close();
        System.out.println("Tous les mots de passe ont été rehashés !");
    }
}
