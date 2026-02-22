package com.iut.banque.test.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import com.iut.banque.facade.BanqueManager;
import com.iut.banque.service.BatchClotureService;

public class TestsBatchClotureService {

	@Test
	public void testLancerClotureMensuelleSuccessful() throws Exception {
		BanqueManager banqueManager = mock(BanqueManager.class);
		BatchClotureService batchService = new BatchClotureService();
		batchService.setBanqueManager(banqueManager);
		
		batchService.lancerClotureMensuelle();
		verify(banqueManager, times(1)).cloturerComptesDifferes();
	}

	@Test
	public void testLancerClotureMensuelleWithException() throws Exception {
		BanqueManager banqueManager = mock(BanqueManager.class);
		org.mockito.Mockito.doThrow(new RuntimeException("Erreur de clôture"))
			.when(banqueManager).cloturerComptesDifferes();

		BatchClotureService batchService = new BatchClotureService();
		batchService.setBanqueManager(banqueManager);
		
		// La méthode ne devrait pas lever d'exception (elle la catch)
		batchService.lancerClotureMensuelle();

		// Vérifier que la méthode a bien été appelée
		verify(banqueManager, times(1)).cloturerComptesDifferes();
	}

	@Test
	public void testSetBanqueManager() throws Exception {
		BanqueManager newManager = mock(BanqueManager.class);
		BatchClotureService batchService = new BatchClotureService();
		batchService.setBanqueManager(newManager);
		batchService.lancerClotureMensuelle();
		verify(newManager, times(1)).cloturerComptesDifferes();
	}
}
