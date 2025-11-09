<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Mot de passe modifié</title>
    <link rel="stylesheet" href="<s:url value='/style/style.css'/>" />
</head>
<body>
<h2>Votre mot de passe a été modifié avec succès !</h2>
<p>
    <s:url action="redirectionLogin" var="redirectionLogin" ></s:url>
    <s:a href="%{redirectionLogin}">Cliquez ici</s:a> pour revenir à l'écran de login</p>
</body>
<jsp:include page="/JSP/Footer.jsp" />
</html>