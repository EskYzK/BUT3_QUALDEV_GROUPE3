<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="fr" xml:lang="fr">
<script type="text/javascript">
	function DisplayMessage() {
		alert('Ce TD a été donné pour les RA dans le cadre du cours de Maintenance Applicative (Promotion 2025-2026)');
	}
</script>
<link href="<s:url value='/style/favicon.ico'/>" rel="icon" type="image/x-icon" />
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="<s:url value='/style/style.css'/>" />
    <title>Application IUT Bank</title>
</head>
<body>
	<h1>Bienvenue sur l'application IUT Bank 2026</h1>
	<p>
		<img
			src="<s:url value='/style/UL_IUT_MOSELLE_EST.png'/>"
			alt="logo" />
	</p>
	<input type="button" value="Information" name="info"
		onClick="DisplayMessage()" />
	<p style="font-size: 2em">
		<s:url action="redirectionLogin" var="redirectionLogin" ></s:url>
		<s:a href="%{redirectionLogin}">Page de Login</s:a>
	</p>
</body>
<jsp:include page="/JSP/Footer.jsp" />
</html>