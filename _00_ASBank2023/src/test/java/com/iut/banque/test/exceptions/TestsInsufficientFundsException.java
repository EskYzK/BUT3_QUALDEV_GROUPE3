package com.iut.banque.test.exceptions;

import com.iut.banque.exceptions.InsufficientFundsException;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestsInsufficientFundsException {

    @Test
    public void defaultConstructor() {
        InsufficientFundsException ex = new InsufficientFundsException();
        assertNull(ex.getMessage());
        assertNull(ex.getCause());
        assertTrue(ex.getStackTrace().length > 0);
    }

    @Test
    public void messageConstructor() {
        InsufficientFundsException ex = new InsufficientFundsException("msg");
        assertEquals("msg", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    public void causeConstructor() {
        Throwable cause = new RuntimeException("cause");
        InsufficientFundsException ex = new InsufficientFundsException(cause);
        assertEquals("java.lang.RuntimeException: cause", ex.getMessage());
        assertSame(cause, ex.getCause());
    }

    @Test
    public void messageCauseConstructor() {
        Throwable cause = new RuntimeException("cause");
        InsufficientFundsException ex = new InsufficientFundsException("msg", cause);
        assertEquals("msg", ex.getMessage());
        assertSame(cause, ex.getCause());
    }

    @Test
    public void fullConstructor_disableSuppression_disableStackTrace() {
        Throwable cause = new RuntimeException("cause");
        InsufficientFundsException ex =
                new InsufficientFundsException("msg", cause, false, false);

        try {
            ex.addSuppressed(new Exception("supp"));
        } catch (IllegalStateException e) {
            // Ancien comportement (Java 8) → OK aussi
        }

        // Dans tous les cas, aucun suppressed ne doit être ajouté
        assertEquals(0, ex.getSuppressed().length);
        assertEquals(0, ex.getStackTrace().length);
    }

    @Test
    public void fullConstructor_enableSuppression_enableStackTrace() {
        Throwable cause = new RuntimeException("cause");
        InsufficientFundsException ex =
                new InsufficientFundsException("msg", cause, true, true);

        ex.addSuppressed(new Exception("supp"));
        assertEquals(1, ex.getSuppressed().length);
        assertTrue(ex.getStackTrace().length > 0);
    }
}