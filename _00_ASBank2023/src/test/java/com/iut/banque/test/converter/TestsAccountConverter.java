package com.iut.banque.test.converter;

import com.iut.banque.converter.AccountConverter;
import com.iut.banque.interfaces.IDao;
import com.iut.banque.modele.Compte;
import com.opensymphony.xwork2.conversion.TypeConversionException;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TestsAccountConverter {

    private IDao mockDao;
    private AccountConverter converter;
    private Compte mockCompte;

    @Before
    public void setUp() {
        mockDao = mock(IDao.class);
        converter = new AccountConverter(mockDao);
        mockCompte = mock(Compte.class);
    }

    @Test
    public void testConvertFromString_Success() {
        when(mockDao.getAccountById("123")).thenReturn(mockCompte);

        Object result = converter.convertFromString(new HashMap<>(), new String[]{"123"}, Compte.class);

        assertSame(mockCompte, result);
        verify(mockDao).getAccountById("123");
    }

    @Test(expected = TypeConversionException.class)
    public void testConvertFromString_Failure() {
        when(mockDao.getAccountById("999")).thenReturn(null);

        converter.convertFromString(new HashMap<>(), new String[]{"999"}, Compte.class);
    }

    @Test
    public void testConvertToString_Success() {
        when(mockCompte.getNumeroCompte()).thenReturn("ABC123");

        String result = converter.convertToString(new HashMap<>(), mockCompte);

        assertEquals("ABC123", result);
        verify(mockCompte).getNumeroCompte();
    }

    @Test
    public void testConvertToString_NullCompte() {
        String result = converter.convertToString(new HashMap<>(), null);

        assertNull(result);
    }
}