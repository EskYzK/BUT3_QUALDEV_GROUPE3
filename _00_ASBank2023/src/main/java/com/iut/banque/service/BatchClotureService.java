package com.iut.banque.service;

import com.iut.banque.facade.BanqueManager;

/**
 * Cette classe sert uniquement à déclencher les tâches planifiées.
 */
public class BatchClotureService {

    private BanqueManager banqueManager;

    // Spring va injecter le manager ici
    public void setBanqueManager(BanqueManager banqueManager) {
        this.banqueManager = banqueManager;
    }

    /**
     * Méthode appelée automatiquement par le planificateur.
     */
    public void lancerClotureMensuelle() {
        System.out.println("[SCHEDULER] Lancement automatique de la clôture mensuelle...");
        try {
            banqueManager.cloturerComptesDifferes();
            System.out.println("[SCHEDULER] Clôture terminée avec succès.");
        } catch (Exception e) {
            System.err.println("[SCHEDULER] Erreur critique lors de la clôture !");
            e.printStackTrace();
        }
    }
}