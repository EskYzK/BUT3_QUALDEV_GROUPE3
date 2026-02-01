<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<!DOCTYPE html>
<html lang="fr" xml:lang="fr">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Mot de passe oublié</title>
    <link rel="stylesheet" href="<s:url value='/style/style.css'/>" />
</head>

<body>
<h1>Réinitialisation du mot de passe</h1>

<div style="color: red">
    <s:actionerror />
</div>

<p>Veuillez entrer votre adresse email. Si elle correspond à un compte, vous recevrez un lien de réinitialisation.</p>

<s:form action="askResetLink" method="POST">
    <s:textfield label="Votre Email" name="email" required="true" />
    <s:submit value="Envoyer le lien"/>
</s:form>

<s:form action="redirectionLogin" method="POST">
    <s:submit value="Retour à la connexion" />
</s:form>
</body>
<jsp:include page="/JSP/Footer.jsp" />
</html>