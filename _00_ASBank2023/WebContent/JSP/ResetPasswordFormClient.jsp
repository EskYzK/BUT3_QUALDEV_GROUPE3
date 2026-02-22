<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<!DOCTYPE html>
<html lang="fr" xml:lang="fr">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Nouveau mot de passe</title>
    <link rel="stylesheet" href="<s:url value='/style/style.css'/>" />
</head>

<body>
<s:form action="retourTableauDeBordClient" method="POST">
    <s:submit value="Annuler" />
</s:form>
<h1>Définir un nouveau mot de passe</h1>

<div style="color: red">
    <s:actionerror />
</div>

<s:form action="doResetPassword" method="POST">
    <s:hidden name="token" value="%{token}" />

    <s:password label="Nouveau mot de passe" name="newPassword" required="true" />
    <s:submit value="Valider le changement"/>
</s:form>

</body>
<jsp:include page="/JSP/Footer.jsp" />
</html>