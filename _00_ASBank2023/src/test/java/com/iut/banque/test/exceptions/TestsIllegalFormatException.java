package com.iut.banque.test.exceptions;

import com.iut.banque.exceptions.IllegalFormatException;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestsIllegalFormatException {

    @Test
    public void defaultConstructor() {
        IllegalFormatException ex = new IllegalFormatException();
        assertNull(ex.getMessage());
        assertNull(ex.getCause());
        assertTrue(ex.getStackTrace().length > 0);
    }

    @Test
    public void messageConstructor() {
        IllegalFormatException ex = new IllegalFormatException("msg");
        assertEquals("msg", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    public void causeConstructor() {
        Throwable cause = new RuntimeException("cause");
        IllegalFormatException ex = new IllegalFormatException(cause);
        assertEquals("java.lang.RuntimeException: cause", ex.getMessage());
        assertSame(cause, ex.getCause());
    }

    @Test
    public void messageCauseConstructor() {
        Throwable cause = new RuntimeException("cause");
        IllegalFormatException ex = new IllegalFormatException("msg", cause);
        assertEquals("msg", ex.getMessage());
        assertSame(cause, ex.getCause());
    }

    @Test
    public void fullConstructor_disableSuppression_disableStackTrace() {
        Throwable cause = new RuntimeException("cause");
        IllegalFormatException ex =
                new IllegalFormatException("msg", cause,false,false);

        assertEquals("msg", ex.getMessage());
        assertSame(cause, ex.getCause());


        try {
            ex.addSuppressed(new Exception("supp"));
            fail("addSuppressed should throw when suppression is disabled");
        } catch (IllegalStateException expected) {
            // ok
        }

        assertEquals(0, ex.getStackTrace().length);
    }

    @Test
    public void fullConstructor_enableSuppression_enableStackTrace() {
        Throwable cause = new RuntimeException("cause");
        IllegalFormatException ex =
                new IllegalFormatException("msg", cause, true, true);

        ex.addSuppressed(new Exception("supp"));
        assertEquals(1, ex.getSuppressed().length);
        assertTrue(ex.getStackTrace().length > 0);
    }
}