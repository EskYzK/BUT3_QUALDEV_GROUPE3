📘 README – Projet Qualité de Développement ASBank
Projet réalisé dans le cadre du cours R5.A08 – Qualité de Développement à l’IUT Informatique de Metz.
Ce projet met en place une application Java EE avec Spring, Hibernate, Tomcat 9 et une base MySQL.
📦 Prérequis
macOS
IntelliJ IDEA Ultimate (licence étudiante JetBrains, nécessaire pour Tomcat)
Java JDK 8 (obligatoire pour compiler le projet)
Maven
Apache Tomcat 9
MySQL 8 ou équivalent
⚙️ Installation et configuration
1. Installation d’IntelliJ IDEA Ultimate
Télécharger depuis JetBrains.
Installer sur Mac (glisser dans Applications).
Activer la licence avec l’adresse email étudiante.
2. Installation du JDK 8
Télécharger depuis Adoptium Temurin 8.
Vérifier l’installation :
java -version
Résultat attendu → 1.8.x.
3. Installation de Tomcat 9
Télécharger depuis Tomcat 9 → archive tar.gz.
Décompresser dans un dossier, ex. :
~/Documents/ANNEE 3/QualiteDev/apache-tomcat-9.0.109
4. Import du projet dans IntelliJ
Ouvrir _00_ASBank2023/ comme projet Maven.
Vérifier que le SDK est bien sur JDK 1.8.
Compiler une première fois :
mvn clean install -DskipTests
🚀 Configuration Tomcat dans IntelliJ
Ajouter Tomcat
IntelliJ → Preferences (⌘,) → Build, Execution, Deployment → Application Servers.
Ajouter Tomcat 9 et pointer vers apache-tomcat-9.0.109.
Configurer une Run Configuration
Run → Edit Configurations…
Ajouter Tomcat Server → Local.
Dans l’onglet Server : HTTP port = 8080.
Dans l’onglet Deployment :
Ajouter l’artifact war exploded.
Définir le Application context :
/_00_ASBank2023
Créer l’Artifact
File → Project Structure → Artifacts
Ajouter Web Application → Exploded → From Modules.
Vérifier que WEB-INF et WebContent sont bien inclus.
Lancer Tomcat
Sélectionner la configuration Tomcat9_ASBank.
Lancer ▶️.
Accéder à l’appli via :
👉 http://localhost:8080/_00_ASBank2023
🗄️ Configuration de la base MySQL
Importer la base de données
Lancer MySQL.
Créer la base :
CREATE DATABASE bankiuttest;
CREATE DATABASE bankiut;
Importer le .sql fourni.
Configurer applicationContext.xml (test et prod)
Exemple pour bankiuttest :
<bean id="dataSource" scope="singleton"
      class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
    <property name="driverClassName" value="com.mysql.cj.jdbc.Driver" />
    <property name="url" value="jdbc:mysql://localhost:3306/bankiuttest?useSSL=false&amp;serverTimezone=UTC" />
    <property name="username" value="root" />
    <property name="password" value="" /> <!-- ou root selon ton MySQL -->
    <property name="defaultAutoCommit" value="false" />
</bean>
⚠️ Remarques :
Utiliser com.mysql.cj.jdbc.Driver (nouveau driver).
Ne pas oublier &amp;serverTimezone=UTC (car en XML & doit être échappé).
Adapter username et password selon ta config MySQL.
🎨 Gestion du CSS
Les fichiers CSS doivent être placés dans WebContent/css/ (ou src/main/webapp/css/).
Vérifier qu’ils sont inclus dans l’artifact.
Dans les JSP/HTML, utiliser :
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css" />
Accessible via :
👉 http://localhost:8080/_00_ASBank2023/css/style.css
🐙 Dépôt GitHub
1. Initialisation
cd "/Users/lilianmorinon/Documents/ANNEE 3/QualiteDev"
git init
git remote add origin https://github.com/EskYzK/BUT3_QUALDEV_GROUPE3.git
2. .gitignore (pour ne pas pousser Tomcat ni IntelliJ)
apache-tomcat-9.0.109/
.idea/
out/
target/
*.iml
3. Préparation du dépôt
Supprimer .git/ dans _00_ASBank2023/ (pas de sous-repo Git).
Ajouter et commit :
git add .
git commit -m "Ajout projet ASBank (code source + config Tomcat/MySQL)"
4. Gestion des conflits avec le dépôt distant
Si le dépôt contient déjà un README ou du code → faire :
git pull --rebase origin main
# résoudre conflits si besoin
git push origin main
✅ État d’avancement
 Installation IntelliJ Ultimate
 Installation JDK 8
 Installation Tomcat 9
 Import du projet _00_ASBank2023 dans IntelliJ
 Création et configuration de l’artifact war exploded
 Déploiement sur Tomcat avec URL / _00_ASBank2023
 Connexion MySQL (Spring + Hibernate)
 Correction &amp;serverTimezone=UTC
 Vérification CSS dans WebContent
 Dépôt GitHub de groupe fonctionnel avec .gitignore
