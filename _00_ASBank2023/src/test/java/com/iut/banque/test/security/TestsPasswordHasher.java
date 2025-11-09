package com.iut.banque.test.security;

import com.iut.banque.security.PasswordHasher;
import org.junit.Test;
import static org.junit.Assert.*;


public class TestsPasswordHasher {

    @Test
    public void testHashNotNull() {
        String pwd = "monMotDePasse123";
        String hashed = PasswordHasher.hash(pwd);
        assertNotNull("Le hash ne doit pas être null", hashed);
        assertNotEquals("Le hash ne doit pas être égal au mot de passe", pwd, hashed);
    }

    @Test
    public void testVerifyCorrectPassword() {
        String pwd = "monMotDePasse123";
        String hashed = PasswordHasher.hash(pwd);
        assertTrue("La vérification doit réussir pour le bon mot de passe",
                PasswordHasher.verify(pwd, hashed));
    }

    @Test
    public void testVerifyIncorrectPassword() {
        String pwd = "monMotDePasse123";
        String wrongPwd = "autreMotDePasse";
        String hashed = PasswordHasher.hash(pwd);
        assertFalse("La vérification doit échouer pour un mot de passe incorrect",
                PasswordHasher.verify(wrongPwd, hashed));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHashNullPassword() {
        PasswordHasher.hash(null);
    }

    @Test
    public void testVerifyNullPasswordOrHash() {
        assertFalse(PasswordHasher.verify(null, "someHash"));
        assertFalse(PasswordHasher.verify("password", null));
        assertFalse(PasswordHasher.verify(null, null));
    }

    @Test
    public void testEmptyPassword() {
        String empty = "";
        String hashed = PasswordHasher.hash(empty);
        assertTrue("Le hash doit accepter une chaîne vide", PasswordHasher.verify(empty, hashed));
    }

    @Test
    public void testLongPassword() {
        String longPwd = new String(new char[1000]).replace('\0', 'a');
        String hashed = PasswordHasher.hash(longPwd);
        assertTrue("Le hash doit accepter un mot de passe très long", PasswordHasher.verify(longPwd, hashed));
    }

    @Test
    public void testPasswordWithSpecialChars() {
        String specialPwd = "p@ssw0rd 😊çàé";
        String hashed = PasswordHasher.hash(specialPwd);
        assertTrue("Le hash doit accepter des caractères spéciaux", PasswordHasher.verify(specialPwd, hashed));
    }

    @Test
    public void testDifferentHashesForSamePassword() {
        String pwd = "monMotDePasse123";
        String hash1 = PasswordHasher.hash(pwd);
        String hash2 = PasswordHasher.hash(pwd);
        assertNotEquals("Deux hash du même mot de passe doivent être différents", hash1, hash2);
    }

    @Test
    public void testCorruptedHashFails() {
        String pwd = "monMotDePasse123";
        String hashed = PasswordHasher.hash(pwd);
        assertFalse(PasswordHasher.verify(pwd + "x", hashed));
        assertFalse(PasswordHasher.verify(pwd, hashed + "x"));
    }

    @Test
    public void hashAndVerify_withEmptyString() {
        String pwd = "";
        String hash = PasswordHasher.hash(pwd);
        assertTrue(PasswordHasher.verify(pwd, hash));
        assertFalse(PasswordHasher.verify("x", hash));
    }

    @Test
    public void hashAndVerify_withUnicode() {
        String pwd = "Pässwörd🐍漢字";
        String hash = PasswordHasher.hash(pwd);
        assertTrue(PasswordHasher.verify(pwd, hash));
    }

    @Test
    public void hashIsDifferent_eachTime_dueToSalt() {
        String pwd = "SamePassword";
        String h1 = PasswordHasher.hash(pwd);
        String h2 = PasswordHasher.hash(pwd);
        assertNotEquals(h1, h2);
    }

    @Test
    public void longPassword_isSupported() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 2000; i++) sb.append('a');
        String pwd = sb.toString();
        String hash = PasswordHasher.hash(pwd);
        assertTrue(PasswordHasher.verify(pwd, hash));
    }

    @Test(expected = IllegalArgumentException.class)
    public void hash_null_throws() {
        PasswordHasher.hash(null);
    }

    @Test
    public void verify_fails_ifHashCorrupted() {
        String hash = PasswordHasher.hash("secret");
        assertFalse(PasswordHasher.verify("secret", hash + "x"));
    }
}