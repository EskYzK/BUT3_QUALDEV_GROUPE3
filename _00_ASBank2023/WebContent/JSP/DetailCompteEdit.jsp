<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="fr" xml:lang="fr">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="<s:url value='/style/style.css'/>" />
    <title>Détail du Compte <s:property value="compte" /></title>
</head>
<body>
	<div class="btnLogout">
		<s:form name="myForm" action="logout" method="POST">
			<s:submit name="Retour" value="Déconnexion" />
		</s:form>
	</div>
	<h1>
		Détail du Compte
		<s:property value="compte" />
	</h1>
	<br />
	<div class="btnBack">
		<s:form name="myForm" action="listeCompteManager" method="POST">
			<s:submit name="Retour" value="Retour" />
		</s:form>
	</div>
	<p>
		Type de compte :
		<s:if test="%{compte.className == \"CompteAvecDecouvert\"}">
			Découvert possible
		</s:if>
		<s:else>
			Simple
		</s:else>
		<br />
		<s:if test="%{compte.solde >= 0}">
			Solde : <s:property value="compte.solde" />
			<br />
		</s:if>
		<s:else>
			Solde : <span class="soldeNegatif"><s:property
					value="compte.solde" /></span>
			<br />
		</s:else>
		<s:if test="%{compte.className == \"CompteAvecDecouvert\"}">
			Découvert maximal autorisé : <s:property
				value="compte.decouvertAutorise" />
		</s:if>
		<br />
	</p>
	<s:form name="formOperation" action="creditActionEdit" method="post">
		<s:textfield label="Montant" name="montant" />
		<input type="hidden" name="compte"
			value="<s:property value='compte.numeroCompte' />">
		<s:submit value="Crediter" />
		<s:submit value="Debiter" onclick="this.form.action='debitActionEdit'; return true;" />
	</s:form>

	<s:if test="%{compte.className == \"CompteAvecDecouvert\"}">
		<s:form name="formChangeDecouvertAutorise"
			action="changerDecouvertAutoriseAction" method="post">
			<input type="hidden" name="compte"
				value="<s:property value='compte.numeroCompte' />">
			<s:textfield label="Découvert autorisé" name="decouvertAutorise"
				value="%{compte.decouvertAutorise}" />
			<s:submit value="Mettre à jour" />
		</s:form>
	</s:if>

    <hr />
    <h3>Cartes Bancaires associées</h3>

    <s:if test="%{compte.cartes.size() > 0}">
        <table class="tabDonnees" style="width: 100%; border-collapse: collapse; margin-bottom: 20px;">
            <thead>
            <tr style="background-color: #eee;">
                <th style="padding: 8px; border: 1px solid #ddd;">Numéro</th>
                <th style="padding: 8px; border: 1px solid #ddd;">Type</th>
                <th style="padding: 8px; border: 1px solid #ddd;">Plafond</th>
                <th style="padding: 8px; border: 1px solid #ddd;">Statut</th>
                <th style="padding: 8px; border: 1px solid #ddd;">Actions</th>
            </tr>
            </thead>
            <tbody>
            <s:iterator value="cartesTriees" var="carte">
                <tr>
                    <td style="padding: 8px; border: 1px solid #ddd;">
                        <s:property value="#carte.numeroCarte" />
                    </td>

                    <td style="padding: 8px; border: 1px solid #ddd;">
                        <s:property value="#carte.typeDeCarte" />
                    </td>

                    <td style="padding: 8px; border: 1px solid #ddd;">
                        <s:property value="#carte.plafond" /> &euro;
                    </td>

                    <td style="padding: 8px; border: 1px solid #ddd; text-align: center;">
                        <s:if test="#carte.supprimee">
                            <span style="color:red; font-weight:bold;">SUPPRIMÉE</span>
                        </s:if>
                        <s:elseif test="#carte.bloquee">
                            <span style="color:orange; font-weight:bold;">BLOQUÉE (Temp)</span>
                        </s:elseif>
                        <s:else>
                            <span style="color:green; font-weight:bold;">ACTIVE</span>
                        </s:else>
                    </td>

                    <td style="padding: 8px; border: 1px solid #ddd; text-align: center;">
                        <s:if test="!#carte.supprimee">
                            <s:url action="modifierCarte_input" var="urlModif">
                                <s:param name="numeroCarte" value="#carte.numeroCarte" />
                                <s:param name="numeroCompte" value="compte.numeroCompte" /> </s:url>
                            <a href="${urlModif}">Modifier</a>

                            <s:if test="#carte.bloquee">
                                &nbsp;|&nbsp;
                                <s:url action="debloquerCarte" var="urlDebloque">
                                    <s:param name="numeroCarte" value="#carte.numeroCarte" />
                                    <s:param name="numeroCompte" value="compte.numeroCompte" /> </s:url>
                                <a href="${urlDebloque}" style="color:green;">Activer</a>
                            </s:if>
                            <s:else>
                                &nbsp;|&nbsp;
                                <s:url action="bloquerCarte" var="urlBloque">
                                    <s:param name="numeroCarte" value="#carte.numeroCarte" />
                                    <s:param name="definitif" value="false" />
                                    <s:param name="numeroCompte" value="compte.numeroCompte" /> </s:url>
                                <a href="${urlBloque}" style="color:orange;">Bloquer</a>
                            </s:else>

                            &nbsp;|&nbsp;
                            <s:url action="bloquerCarte" var="urlSuppr">
                                <s:param name="numeroCarte" value="#carte.numeroCarte" />
                                <s:param name="definitif" value="true" />
                                <s:param name="numeroCompte" value="compte.numeroCompte" /> </s:url>
                            <a href="${urlSuppr}" style="color:red;" onclick="return confirm('Attention : Cette action est irréversible. Confirmer la suppression ?');">X</a>
                        </s:if>
                    </td>
                </tr>
            </s:iterator>
            </tbody>
        </table>
    </s:if>
    <s:else>
        <p><i>Aucune carte bancaire pour ce compte.</i></p>
    </s:else>

    <div style="margin-top: 10px;">
        <s:url action="creerCarte_input" var="urlCreer">
            <s:param name="numeroCompte" value="compte.numeroCompte" />
        </s:url>
        <a href="${urlCreer}" class="btn">
            [+] Ajouter une Carte Bancaire
        </a>
    </div>
    <br/>

	<s:url action="urlDetail" var="urlDetail">
		<s:param name="idCompte">
			<s:property value="key" />
		</s:param>
	</s:url>
	<s:if test="%{error != \"\"}">
		<div class="failure">
			Erreur :
			<s:property value="error" />
		</div>
	</s:if>
<br/>
<br/>
</body>
<jsp:include page="/JSP/Footer.jsp" />
</html>