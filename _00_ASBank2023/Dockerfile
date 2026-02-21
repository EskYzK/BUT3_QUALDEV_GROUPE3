# Étape 1 : Build avec Maven
FROM maven:3.9-eclipse-temurin-11 AS build
WORKDIR /app

# On copie d'abord le pom.xml pour profiter du cache Docker des dépendances
COPY pom.xml .
# On télécharge les dépendances (ceci ne sera refait que si le pom.xml change)
RUN mvn dependency:go-offline

# On copie le code source et les ressources
COPY src ./src
COPY WebContent ./WebContent

# IMPORTANT : On copie le .env pour que les tests puissent lire les accès à la BDD en ligne
COPY .env .

# Lancement du build AVEC les tests et la mise à jour des snapshots (-U)
# Si les tests échouent ici (connexion BDD impossible ou erreur code), le build s'arrête.
RUN mvn -U clean package

# Étape 2 : Création de l'image d'exécution
FROM tomcat:9.0-jdk11

# Nettoyage du Tomcat par défaut
RUN rm -rf /usr/local/tomcat/webapps/*

# Récupération du WAR compilé à l'étape précédente
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/_00_ASBank2023.war

EXPOSE 8080
CMD ["catalina.sh", "run"]