package com.iut.banque.test.service;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import com.iut.banque.facade.BanqueManager;
import com.iut.banque.service.BatchClotureService;

public class TestsBatchClotureService {

    private BatchClotureService batchClotureService;
    private BanqueManager mockBanqueManager;

    @Before
    public void setUp() {
        // Initialisation du service
        batchClotureService = new BatchClotureService();

        // Création d'un mock pour BanqueManager
        mockBanqueManager = mock(BanqueManager.class);

        // Injection du mock via le setter
        batchClotureService.setBanqueManager(mockBanqueManager);
    }

    @Test
    public void testLancerClotureMensuelle_Succes() throws Exception {
        // Exécution de la méthode
        batchClotureService.lancerClotureMensuelle();

        // Vérification : on s'assure que la méthode cloturerComptesDifferes() a été appelée exactement 1 fois
        verify(mockBanqueManager, times(1)).cloturerComptesDifferes();
    }

    @Test
    public void testLancerClotureMensuelle_AvecException() throws Exception {
        // Configuration du mock : forcer la levée d'une exception lors de l'appel
        doThrow(new RuntimeException("Erreur technique simulée")).when(mockBanqueManager).cloturerComptesDifferes();

        // Exécution de la méthode
        // Le test réussit si l'exception est bien attrapée par le bloc catch dans BatchClotureService
        batchClotureService.lancerClotureMensuelle();

        // Vérification : on s'assure que la méthode a bien été appelée, même si elle a planté
        verify(mockBanqueManager, times(1)).cloturerComptesDifferes();
    }
}