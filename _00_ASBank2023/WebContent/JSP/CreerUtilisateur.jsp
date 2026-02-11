<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<!DOCTYPE html>
<html lang="fr" xml:lang="fr">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Formulaire de création d'utilisateur</title>
    <link rel="stylesheet" href="<s:url value='/style/style.css'/>" />
    <script src="<s:url value='/js/jquery.js'/>"></script>
    <script src="<s:url value='/js/jsCreerUtilisateur.js'/>"></script>

    <style>
        .info-format {
            background-color: #e8f4f8;
            border: 1px solid #2980b9;
            color: #2980b9;
            padding: 10px;
            margin-bottom: 15px;
            border-radius: 5px;
            font-size: 0.9em;
        }
    </style>
</head>

<body>

    <div class="btnLogout">
        <s:form name="myForm" action="logout" method="POST">
            <s:submit name="Retour" value="Logout" />
        </s:form>
    </div>
    <h1>Créer un nouvel utilisateur</h1>

    <div class="info-format">
        <strong>Formats obligatoires pour les Clients :</strong><br/>
        <ul>
            <li><strong>Code utilisateur :</strong> doit être au format <em>lettre.lettresNombre ou Nombre doit obligatoirement commencer par un chiffre de 1 à 9</em> (ex: <code>j.dupont107</code>).</li>
            <li><strong>Numéro de client :</strong> doit comporter exactement <strong>10 chiffres</strong>.</li>
        </ul>
    </div>

    <s:form id="myForm" name="myForm" action="ajoutUtilisateur"
            method="POST">

        <s:textfield label="Code utilisateur" name="userId" placeholder="ex: j.dupont1" />

        <s:textfield label="Nom" name="nom" />
        <s:textfield label="Prenom" name="prenom" />
        <s:textfield label="Adresse" name="adresse" />
        <s:password label="Password" name="userPwd" />
        <s:radio label="Sexe" name="male" list="#{true:'Homme',false:'Femme'}"
                value="true" />
        <s:radio label="Type" name="client"
                list="#{true:'Client',false:'Manager'}" value="true" />

        <s:textfield label="Numéro de client" name="numClient" placeholder="0123456789 (10 chiffres)" />

        <s:submit name="submit" value="Créer l'utilisateur" />
    </s:form>

    <s:form name="myForm" action="retourTableauDeBordManager" method="POST">
        <s:submit name="Retour" value="Retour" />
    </s:form>

    <s:if test="(result == \"SUCCESS\")">
        <div class="success">
            <s:property value="message" />
        </div>
    </s:if>
    <s:else>
        <div class="failure">
            <s:property value="message" />
        </div>
    </s:else>
</body>
<jsp:include page="/JSP/Footer.jsp" />
</html>