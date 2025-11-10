package com.iut.banque.test.exceptions;

import com.iut.banque.exceptions.IllegalOperationException;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestsIllegalOperationException {

    @Test
    public void defaultConstructor() {
        IllegalOperationException ex = new IllegalOperationException();
        assertNull(ex.getMessage());
        assertNull(ex.getCause());
        assertTrue(ex.getStackTrace().length > 0);
    }

    @Test
    public void messageConstructor() {
        IllegalOperationException ex = new IllegalOperationException("msg");
        assertEquals("msg", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    public void causeConstructor() {
        Throwable cause = new RuntimeException("cause");
        IllegalOperationException ex = new IllegalOperationException(cause);
        assertEquals("java.lang.RuntimeException: cause", ex.getMessage());
        assertSame(cause, ex.getCause());
    }

    @Test
    public void messageCauseConstructor() {
        Throwable cause = new RuntimeException("cause");
        IllegalOperationException ex = new IllegalOperationException("msg", cause);
        assertEquals("msg", ex.getMessage());
        assertSame(cause, ex.getCause());
    }

    @Test
    public void fullConstructor_disableSuppression_disableStackTrace() {
        Throwable cause = new RuntimeException("cause");
        IllegalOperationException ex =
                new IllegalOperationException("msg", cause, false, false);

        // addSuppressed() ne doit pas ajouter d'exception, même si elle ne lève rien
        ex.addSuppressed(new Exception("supp"));

        assertEquals(0, ex.getSuppressed().length);
        assertEquals(0, ex.getStackTrace().length);
        assertSame(cause, ex.getCause());
        assertEquals("msg", ex.getMessage());
    }

    @Test
    public void fullConstructor_enableSuppression_enableStackTrace() {
        Throwable cause = new RuntimeException("cause");
        IllegalOperationException ex =
                new IllegalOperationException("msg", cause, true, true);

        ex.addSuppressed(new Exception("supp"));
        assertEquals(1, ex.getSuppressed().length);
        assertTrue(ex.getStackTrace().length > 0);
    }
}