<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="fr">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="<s:url value='/style/style.css'/>" />
    <title>Modifier Carte Bancaire</title>
</head>
<body>
<div class="btnLogout">
    <s:form action="logout" method="POST">
        <s:submit value="Déconnexion" />
    </s:form>
</div>

<h1>Modification de la Carte</h1>

<div class="btnBack">
    <s:form action="editAccount" method="POST">
        <s:hidden name="compte" value="%{numeroCompte}" />
        <s:submit value="Retour" />
    </s:form>
</div>

<div>
    <s:if test="hasActionErrors()">
        <div class="failure"><s:actionerror /></div>
    </s:if>
    <s:if test="message != null">
        <div class="success"><s:property value="message"/></div>
    </s:if>

    <s:form action="modifierCarte" method="post" theme="simple">
        <s:hidden name="numeroCarte" value="%{carte.numeroCarte}" theme="simple" />
        <table class="form-table">
            <tr>
                <th scope="col"><strong>Numéro de Carte :</strong></th>
                <th scope="col"><s:property value="carte.numeroCarte" /></th>
            </tr>
            <tr>
                <th scope="col"><strong>Type :</strong></th>
                <th scope="col"><s:property value="carte.typeDeCarte" /></th>
            </tr>
            <tr>
                <th scope="col"><strong>Statut :</strong></th>
                <th scope="col">
                    <s:if test="carte.bloquee"><span style="color:orange">Bloquée</span></s:if>
                    <s:else><span style="color:green">Active</span></s:else>
                </th>
            </tr>
            <tr>
                <th scope="col"><label><input type="text" />Compte lié :</label></th>
                <th scope="col">
                    <s:if test='%{carte.typeDeCarte == "Débit Immédiat"}'>
                        <s:select
                                list="comptesClient"
                                listKey="key"
                                listValue="value.numeroCompte"
                                name="nouveauNumeroCompte"
                                value="%{carte.compte.numeroCompte}"
                                theme="simple"
                        />
                    </s:if>
                    <s:else>
                        <s:property value="carte.compte.numeroCompte" />
                        <s:hidden name="nouveauNumeroCompte" value="%{carte.compte.numeroCompte}" theme="simple" />
                    </s:else>
                </th>
            </tr>
            <tr>
                <th scope="col"><input type="text" />Nouveau Plafond :</th>
                <th scope="col">
                    <s:textfield name="plafond" value="%{carte.plafond}" theme="simple" />
                </th>
            </tr>
            <tr>
                <th scope="col" colspan="2" style="text-align: center; padding-top: 20px;">
                    <s:submit value="Enregistrer les modifications" class="btn-primary" theme="simple" />
                </th>
            </tr>
        </table>
    </s:form>
</div>

</body>
<jsp:include page="/JSP/Footer.jsp" />
</html>