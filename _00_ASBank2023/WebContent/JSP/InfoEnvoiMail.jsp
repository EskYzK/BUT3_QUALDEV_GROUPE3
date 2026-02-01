<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<!DOCTYPE html>
<html lang="fr" xml:lang="fr">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Email envoyé</title>
    <link rel="stylesheet" href="<s:url value='/style/style.css'/>" />
</head>

<body>
<h1>Email envoyé</h1>

<div style="color: green; margin: 20px 0; font-weight: bold;">
    <s:property value="message" default="Un lien a été envoyé." />
</div>

<p>Veuillez cliquer sur le lien reçu pour définir votre nouveau mot de passe.</p>

<s:form action="redirectionLogin" method="POST">
    <s:submit value="Retour à la connexion" />
</s:form>
</body>
<jsp:include page="/JSP/Footer.jsp" />
</html>