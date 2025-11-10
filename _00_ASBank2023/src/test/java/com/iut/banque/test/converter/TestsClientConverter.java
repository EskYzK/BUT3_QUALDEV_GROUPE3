package com.iut.banque.test.converter;

import com.iut.banque.converter.ClientConverter;
import com.iut.banque.interfaces.IDao;
import com.iut.banque.modele.Client;
import com.opensymphony.xwork2.conversion.TypeConversionException;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TestsClientConverter {

    private IDao mockDao;
    private ClientConverter converter;
    private Client mockClient;

    @Before
    public void setUp() {
        mockDao = mock(IDao.class);
        converter = new ClientConverter(mockDao);
        mockClient = mock(Client.class);
    }

    @Test
    public void testConvertFromString_Success() {
        when(mockDao.getUserById("C001")).thenReturn(mockClient);

        Object result = converter.convertFromString(new HashMap<>(), new String[]{"C001"}, Client.class);

        assertSame(mockClient, result);
        verify(mockDao).getUserById("C001");
    }

    @Test(expected = TypeConversionException.class)
    public void testConvertFromString_Failure() {
        when(mockDao.getUserById("UNKNOWN")).thenReturn(null);

        converter.convertFromString(new HashMap<>(), new String[]{"UNKNOWN"}, Client.class);
    }

    @Test
    public void testConvertToString_Success() {
        when(mockClient.getIdentity()).thenReturn("C001");

        String result = converter.convertToString(new HashMap<>(), mockClient);

        assertEquals("C001", result);
        verify(mockClient).getIdentity();
    }

    @Test
    public void testConvertToString_NullClient() {
        String result = converter.convertToString(new HashMap<>(), null);

        assertNull(result);
    }

    @Test
    public void testDefaultConstructor_DoesNotThrow() {
        new ClientConverter(); // juste pour s'assurer qu’il ne jette pas d’erreur
    }
}