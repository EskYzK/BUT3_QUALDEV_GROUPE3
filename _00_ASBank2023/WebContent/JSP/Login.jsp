<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<!DOCTYPE html>
<html lang="fr" xml:lang="fr">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Page de connexion</title>
    <link rel="stylesheet" href="<s:url value='/style/style.css'/>" />
</head>

<body>
	<h1>Login :</h1>
	<s:form name="myForm" action="controller.Connect.login.action"
		method="POST">
		<s:textfield label="Code user" name="userCde" />
		<s:password label="Password" name="userPwd" />
        <s:submit value="Se Connecter"/>
    </s:form>

    <s:form action="forgotPasswordInput" method="POST">
        <s:submit value="Mot de passe oublié ?" />
    </s:form>

    <s:form name="myFormRetour" action="retourAccueil" method="POST">
		<s:submit name="Retour" value="Retour à l'accueil" />
	</s:form>
</body>
<jsp:include page="/JSP/Footer.jsp" />
</html>