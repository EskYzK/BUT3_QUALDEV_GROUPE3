package com.iut.banque.test.constants;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.iut.banque.constants.LoginConstants;

/**
 * Classe de test pour vérifier les constantes de LoginConstants
 */
public class TestsLoginConstants {

	@Test
	public void testUserIsConnected() {
		assertEquals("USER_IS_CONNECTED devrait valoir 1", 1, LoginConstants.USER_IS_CONNECTED);
	}

	@Test
	public void testManagerIsConnected() {
		assertEquals("MANAGER_IS_CONNECTED devrait valoir 2", 2, LoginConstants.MANAGER_IS_CONNECTED);
	}

	@Test
	public void testLoginFailed() {
		assertEquals("LOGIN_FAILED devrait valoir -1", -1, LoginConstants.LOGIN_FAILED);
	}

	@Test
	public void testError() {
		assertEquals("ERROR devrait valoir -2", -2, LoginConstants.ERROR);
	}

	@Test
	public void testConstantsAreDistinct() {
		// Vérifier que toutes les constantes sont différentes
		int[] values = {
			LoginConstants.USER_IS_CONNECTED,
			LoginConstants.MANAGER_IS_CONNECTED,
			LoginConstants.LOGIN_FAILED,
			LoginConstants.ERROR
		};
		
		for (int i = 0; i < values.length; i++) {
			for (int j = i + 1; j < values.length; j++) {
				assertEquals("Les constantes doivent avoir des valeurs distinctes", 
					false, values[i] == values[j]);
			}
		}
	}

	@Test
	public void testConstructorPrivate() throws Exception {
		// Vérifier que le constructeur privé lève une exception si on essaie de l'appeler
		java.lang.reflect.Constructor<?> constructor = LoginConstants.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		try {
			constructor.newInstance();
			fail("Le constructeur devrait lever une exception");
		} catch (java.lang.reflect.InvocationTargetException e) {
			// L'AssertionError est enveloppé dans InvocationTargetException
			assertEquals(AssertionError.class, e.getCause().getClass());
		}
	}
}
