<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="fr">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="<s:url value='/style/style.css'/>" />
    <title>Créer une Carte Bancaire</title>
</head>
<body>
<div class="btnLogout">
    <s:form action="logout" method="POST">
        <s:submit value="Déconnexion" />
    </s:form>
</div>

<h1>Création d'une Carte Bancaire</h1>

<div class="btnBack">
    <s:form action="editAccount" method="POST">
        <s:hidden name="compte" value="%{numeroCompte}" />
        <s:submit value="Retour" />
    </s:form>
</div>

<div>
    <s:if test="hasActionErrors()">
        <div class="failure">
            <s:actionerror />
        </div>
    </s:if>
    <s:if test="hasFieldErrors()">
        <div class="failure">
            <s:fielderror />
        </div>
    </s:if>

    <s:form action="creerCarte" method="post" theme="simple">
        <s:hidden name="numeroCompte" value="%{numeroCompte}" theme="simple" />

        <table class="form-table">
            <tr>
                <th scope="col"><label>Type de débit :</label></th>
                <th scope="col">
                    <s:select name="typeDebit"
                              list="#{'IMMEDIAT':'Débit Immédiat', 'DIFFERE':'Débit Différé'}"
                              theme="simple" />
                </th>
            </tr>
            <tr>
                <th scope="col"><label>Plafond (30 jours) :</label></th>
                <th scope="col">
                    <s:textfield name="plafond" placeholder="Ex: 500" theme="simple" />
                </th>
            </tr>
            <tr>
                <th scope="col" colspan="2" style="text-align: center; padding-top: 20px;">
                    <s:submit value="Créer la carte" class="btn-primary" theme="simple" />
                </th>
            </tr>
        </table>
    </s:form>
</div>

</body>
<jsp:include page="/JSP/Footer.jsp" />
</html>