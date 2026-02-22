<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<!DOCTYPE html>
<html lang="fr" xml:lang="fr">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Modifier mon mot de passe</title>
    <link rel="stylesheet" href="<s:url value='/style/style.css'/>" />
</head>

<body>
    <div class="btnLogout">
	    <s:form name="myForm" action="logout" method="POST">
			<s:submit name="Retour" value="Déconnexion" />
		</s:form>
	</div>
	<h1>Modifier mon mot de passe</h1>

	<div style="color: red">
		<s:actionerror />
	</div>

	<p>Veuillez entrer votre ancien mot de passe et votre nouveau mot de passe.</p>

	<s:form action="changePasswordManager" method="POST">
		<s:password name="oldPassword" label="Ancien mot de passe" required="true"/>
		<s:password name="newPassword" label="Nouveau mot de passe" required="true"/>
		<s:password name="confirmPassword" label="Confirmer le mot de passe" required="true"/>
		<s:submit value="Modifier"/>
	</s:form>

	<s:form action="retourTableauDeBordManager" method="POST">
		<s:submit value="Annuler" />
	</s:form>

	<s:if test="result">
		<s:if test="!error">
			<div class="success">
				<s:actionmessage />
			</div>
		</s:if>
		<s:else>
			<div class="failure">
				<s:actionmessage />
			</div>
		</s:else>
	</s:if>
</body>
<jsp:include page="/JSP/Footer.jsp" />
</html>