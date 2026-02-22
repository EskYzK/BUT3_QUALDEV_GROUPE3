<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="s" uri="/struts-tags"%>

<html lang="fr" xml:lang="fr">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Tableau de bord</title>
    <link rel="stylesheet" href="<s:url value='/style/style.css'/>" />
</head>
<body>
	<div class="btnLogout">
		<s:form name="myForm" action="logout" method="POST">
			<s:submit name="Retour" value="Déconnexion" />
		</s:form>
	</div>
	<h1>Tableau de bord</h1>
	<p>
		Bienvenue <b><s:property value="connectedUser.prenom" /> <s:property
				value="connectedUser.nom" /></b> !
	</p>
    <p>
        <s:url action="changePasswordClient" var="urlChangePassword"></s:url>
        <s:a href="%{urlChangePassword}">Changer mon mot de passe</s:a>
    </p>
    <p>Voici l'état de vos comptes :</p>
	<table>
		<tr>
			<th>Numéro de compte</th>
			<th>Type de compte</th>
			<th>Solde actuel</th>
		</tr>
		<s:iterator value="accounts">
			<tr>
				<td><s:url action="urlDetail" var="urlDetail">
						<s:param name="compte"><s:property value="key" /></s:param> 
						<%-- <s:param name="idCompte"><s:property value="key" /></s:param> --%>
					</s:url> <s:a href="%{urlDetail}">
						<s:property value="key" />
					</s:a></td>
				<s:if test="%{value.className == \"CompteAvecDecouvert\"}">
					<td>Découvert possible</td>
				</s:if>
				<s:else>
					<td>Simple</td>
				</s:else>
				<s:if test="%{value.solde >= 0}">
					<td><s:property value="value.solde" /></td>
				</s:if>
				<s:else>
					<td class="soldeNegatif"><s:property value="value.solde" /></td>
				</s:else>
			</tr>
		</s:iterator>
	</table>
</body>
<jsp:include page="/JSP/Footer.jsp" />
</html>