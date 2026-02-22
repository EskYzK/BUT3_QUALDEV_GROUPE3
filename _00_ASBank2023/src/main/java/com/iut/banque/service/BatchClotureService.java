package com.iut.banque.service;

import com.iut.banque.facade.BanqueManager;
import org.slf4j.Logger;         // Import SLF4J
import org.slf4j.LoggerFactory;  // Import SLF4J

/**
 * Cette classe sert uniquement à déclencher les tâches planifiées.
 */
public class BatchClotureService {

    private static final Logger logger = LoggerFactory.getLogger(BatchClotureService.class);
    private BanqueManager banqueManager;

    // Spring va injecter le manager ici
    public void setBanqueManager(BanqueManager banqueManager) {
        this.banqueManager = banqueManager;
    }

    /**
     * Méthode appelée automatiquement par le planificateur.
     */
    public void lancerClotureMensuelle() {
        logger.info("[SCHEDULER] Lancement automatique de la clôture mensuelle...");
        try {
            banqueManager.cloturerComptesDifferes();
            logger.info("[SCHEDULER] Clôture terminée avec succès.");
        } catch (Exception e) {
            logger.error("[SCHEDULER] Erreur critique lors de la clôture !", e);
        }
    }
}