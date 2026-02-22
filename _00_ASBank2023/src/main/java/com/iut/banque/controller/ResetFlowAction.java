package com.iut.banque.controller;

import com.iut.banque.facade.BanqueFacade;
import com.opensymphony.xwork2.ActionSupport;

public class ResetFlowAction extends ActionSupport {

    private String email;
    private String token;
    private String newPassword;
    private String message;
    private transient BanqueFacade banque;

    // --- Getters & Setters ---

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public void setBanque(BanqueFacade banque) { this.banque = banque; }

    // Un Setter pour permettre aux tests d'injecter un Mock
    public void setBanqueFacade(BanqueFacade banqueFacade) {
        this.banque = banqueFacade;
    }

    // --- Actions ---

    /**
     * Appelé depuis le formulaire "Mot de passe oublié"
     */
    public String sendLink() {
        // 1. Vérification de surface (Input Validation)
        // Si l'utilisateur n'a rien écrit, on le lui dit (c'est une erreur de saisie, pas de sécurité)
        if (email == null || email.trim().isEmpty()) {
            message = "Veuillez renseigner une adresse email.";
            return "error";
        }

        // 2. Appel du métier
        // On appelle la façade, mais on IGNORE le résultat (true/false) volontairement
        banque.initiatePasswordReset(email);

        // 3. Réponse générique (Security)
        // Qu'il existe ou non, on affiche le même message de succès
        message = "Lien envoyé à l'adresse mail renseignée si elle existe.";
        return "link_sent";
    }

    /**
     * Appelé quand l'utilisateur clique sur le lien dans le mail
     */
    public String showForm() {
        if (token == null || token.isEmpty()) {
            addActionError("Lien invalide.");
            return ERROR;
        }
        // Ici pas besoin de la façade, on affiche juste la vue
        return "show_form";
    }

    /**
     * Appelé quand l'utilisateur soumet son nouveau mot de passe
     */
    public String processReset() {
        if (banque.usePasswordResetToken(token, newPassword)) {
            return "reset_success";
        } else {
            addActionError("Lien invalide ou expiré.");
            return "show_form";
        }
    }
}