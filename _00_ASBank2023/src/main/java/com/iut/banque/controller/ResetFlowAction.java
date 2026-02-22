package com.iut.banque.controller;

import com.iut.banque.facade.BanqueFacade;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ResetFlowAction extends ActionSupport {

    private String email;
    private String token;
    private String newPassword;
    private String message;
    private BanqueFacade banque;

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

    // --- Méthode helper pour récupérer la banque ---
    private BanqueFacade getBanque() {
        if (this.banque == null) {
            ApplicationContext context = WebApplicationContextUtils
                    .getRequiredWebApplicationContext(ServletActionContext.getServletContext());
            this.banque = (BanqueFacade) context.getBean("banqueFacade");
        }
        return this.banque;
    }

    // --- Actions ---

    /**
     * Appelé depuis le formulaire "Mot de passe oublié"
     */
    public String sendLink() {
        if (getBanque().initiatePasswordReset(email)) {
            message = "Lien envoyé à l'adresse mail renseignée si elle existe.";
            return "link_sent";
        } else {
            addActionError("Erreur.");
            return ERROR;
        }
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
        if (getBanque().usePasswordResetToken(token, newPassword)) {
            return "reset_success";
        } else {
            addActionError("Lien invalide ou expiré.");
            return "show_form";
        }
    }
}