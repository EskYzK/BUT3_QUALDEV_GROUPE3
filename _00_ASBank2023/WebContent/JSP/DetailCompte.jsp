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
        <s:submit name="Retour" value="Logout" />
    </s:form>
</div>
<h1>
    Détail du Compte
    <s:property value="compte" />
</h1>
<br />
<div class="btnBack">
    <s:form name="myForm" action="retourTableauDeBordClient" method="POST">
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
<s:form name="formOperation" action="creditAction" method="post">
    <s:textfield label="Montant" name="montant" />
    <input type="hidden" name="compte"
           value="<s:property value='compte' />">
    <s:submit value="Crediter" />
    <s:submit value="Debiter" onclick="this.form.action='debitAction'; return true;" />
</s:form>

<hr />
<h3>Mes Cartes Bancaires</h3>

<s:if test="%{compte.cartes.size() > 0}">
    <table class="tabDonnees" style="   width: 100%; border-collapse: collapse; margin-bottom: 20px;">
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
                    <s:if test="!#carte.supprimee && !#carte.bloquee">
                        <s:url action="bloquerCarteClient" var="urlBloque">
                            <s:param name="numeroCarte" value="#carte.numeroCarte" />
                            <s:param name="definitif" value="false" />
                            <s:param name="numeroCompte" value="compte.numeroCompte" />
                        </s:url>
                        <a href="${urlBloque}" style="color:orange;">Bloquer (Temp)</a>

                        &nbsp;|&nbsp;
                        <s:url action="bloquerCarteClient" var="urlSuppr">
                            <s:param name="numeroCarte" value="#carte.numeroCarte" />
                            <s:param name="definitif" value="true" />
                            <s:param name="numeroCompte" value="compte.numeroCompte" />
                        </s:url>
                        <a href="${urlSuppr}" style="color:red;" onclick="return confirm('Attention : Cette action est irréversible. Votre carte sera définitivement inutilisable. Confirmer ?');">Opposer (Def)</a>
                    </s:if>
                    <s:elseif test="#carte.bloquee && !#carte.supprimee">
                        <i>Contactez votre conseiller pour débloquer</i>
                    </s:elseif>
                </td>
            </tr>
        </s:iterator>
        </tbody>
    </table>

    <h4>Simuler un paiement par Carte</h4>

    <div style="display: flex; flex-wrap: wrap; gap: 20px; align-items: flex-start; justify-content: center;">

        <s:iterator value="cartesTriees " var="carte">
            <s:if test="!#carte.supprimee && !#carte.bloquee">

                <div style="border: 1px solid #ccc; background-color: #f9f9f9; border-radius: 8px; width: 280px; box-shadow: 2px 2px 5px rgba(0,0,0,0.1); overflow: hidden;">

                    <div style="background-color: #eee; padding: 10px; border-bottom: 1px solid #ccc;">
                        <strong>Carte : <s:property value="#carte.numeroCarte" /></strong>
                        <br/>
                        <span style="font-size: 0.8em; color: #555;">
                                (<s:if test='#carte.typeDeCarte == "Débit Immédiat"'>Immédiat</s:if><s:else>Différé</s:else>)
                            </span>
                    </div>

                    <div style="padding: 15px;">
                        <s:form action="payerParCarte" method="post" theme="simple">
                            <s:hidden name="numeroCarte" value="%{#carte.numeroCarte}" theme="simple" />
                            <s:hidden name="numeroCompte" value="%{compte.numeroCompte}" theme="simple" />

                            <table style="width: 100%; border: none;">
                                <tr>
                                    <td style="padding-bottom: 5px;">Libellé :</td>
                                    <td style="padding-bottom: 5px;">
                                        <s:textfield name="libellePaiement" placeholder="Ex: Amazon" theme="simple" style="width: 95%;" />
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding-bottom: 5px; min-width: 70px">Montant :</td>
                                    <td style="padding-bottom: 5px;">
                                        <s:textfield name="montantPaiement" placeholder="0.00" theme="simple" style="width: 80px; text-align: right;" /> &euro;
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="2" style="text-align: right; padding-top: 10px;">
                                        <s:submit value="Payer" class="btn-primary" theme="simple" style="cursor:pointer; padding: 5px 15px;" />
                                    </td>
                                </tr>
                            </table>
                        </s:form>
                    </div>
                </div>
            </s:if>
        </s:iterator>
    </div>
    <br/>
</s:if>
<s:else>
    <p><i>Aucune carte bancaire associée à ce compte.</i></p>
</s:else>
<br/>
<s:url action="urlDetail" var="urlDetail">
    <s:param name="compte">
        <s:property value="key" />
    </s:param>
</s:url>
<s:if test="%{error != \"\"}">
    <div class="failure">
        Erreur :
        <s:property value="error" />
    </div>
</s:if>
</body>
<jsp:include page="/JSP/Footer.jsp" />
</html>