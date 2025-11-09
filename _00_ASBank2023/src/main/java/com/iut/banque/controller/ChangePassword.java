package com.iut.banque.controller;


import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.Utilisateur;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.context.ApplicationContext;

public class ChangePassword extends ActionSupport {
    private String oldPassword;
    private String newPassword;

    // Getters & setters

    public String getOldPassword() { return oldPassword; }
    public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

    @Override
    public String execute() {
        // Récupérer la BanqueFacade depuis Spring
        ApplicationContext context = WebApplicationContextUtils
                .getRequiredWebApplicationContext(ServletActionContext.getServletContext());
        BanqueFacade banque = (BanqueFacade) context.getBean("banqueFacade");

        Utilisateur user = banque.getConnectedUser(); // tu n'as plus besoin du DAO direct

        if (user == null) {
            addActionError("Vous devez être connecté.");
            return ERROR;
        }

        // Utiliser directement le LoginManager via la facade
        boolean success = banque.changePassword(user, oldPassword, newPassword);

        if (success) {
            return SUCCESS;
        } else {
            return INPUT;
        }
    }
}
