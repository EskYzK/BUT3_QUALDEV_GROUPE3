<%@ taglib prefix="s" uri="/struts-tags" %>
<h2>Modifier mon mot de passe</h2>
<s:form action="changePassword">
    <s:password name="oldPassword" label="Ancien mot de passe" required="true"/>
    <s:password name="newPassword" label="Nouveau mot de passe" required="true"/>
    <s:password name="confirmPassword" label="Confirmer le mot de passe" required="true"/>
    <s:submit value="Modifier"/>
</s:form>

<s:actionerror/>
<s:actionmessage/>