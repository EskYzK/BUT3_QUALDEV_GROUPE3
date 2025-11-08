package com.iut.banque.controller;


import com.iut.banque.facade.LoginManager;
import com.iut.banque.modele.Utilisateur;
import com.opensymphony.xwork2.ActionSupport;

public class ChangePassword extends ActionSupport {
    private String oldPassword;
    private String newPassword;

    // Getters & setters

    @Override
    public String execute() {
        Utilisateur user = (Utilisateur)
                org.apache.struts2.ServletActionContext.getRequest()
                        .getSession().getAttribute("user");

        if (user == null) {
            addActionError("Vous devez être connecté.");
            return ERROR;
        }

        LoginManager manager = new LoginManager();
        boolean success = manager.changePassword(user, oldPassword, newPassword);

        if (success) {
            addActionMessage("Mot de passe modifié avec succès !");
            return SUCCESS;
        } else {
            addActionError("Ancien mot de passe incorrect.");
            return INPUT;
        }
    }
}
