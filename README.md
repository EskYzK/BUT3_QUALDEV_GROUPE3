🏦 ASBank 2026 - Système de Gestion Bancaire
📚 Projet de Qualité de Développement (R5.A08) IUT Informatique de Metz – Université de Lorraine

Ce projet consiste en la maintenance évolutive et l'amélioration qualitative d'une application Java EE. L'accent est mis sur l'industrialisation du déploiement via Docker, l'intégration continue (CI/CD) et le respect d'une charte graphique institutionnelle stricte.

👥 L'Équipe (Groupe 3)
Membre	Rôle
CHOLLET Thomas	Scrum Master & Développeur
MORINON Lilian	Développeur & Responsable Documentation
AIT BAHA Said	Développeur
KERBER Alexandre	Développeur
🚀 1. Installation et Déploiement
Le projet est entièrement conteneurisé. L'utilisation de Tomcat local est désormais obsolète au profit d'une infrastructure Docker.

📦 Prérequis

Docker Desktop (moteur de conteneurisation).

Java JDK 11 & Maven 3.9+ (pour le build initial).

Configuration BDD : Vérifiez que applicationContext.xml pointe sur db:3306.

🛠 Procédure de lancement

Build Maven : Générez l'artifact en ignorant les tests (nécessaire pour le build Docker).

Bash
mvn clean install -DskipTests
Build de l'image : Préparez l'image Docker de l'application.

Bash
docker build -t mon-projet-java .
Infrastructure & Réseau : Créez le réseau et lancez la base de données.

Bash
docker network create asbank_network
docker-compose up -d
Import SQL : Accédez à http://localhost:8082, sélectionnez la base testdb et importez le fichier script/chollet14u_bankiut.sql.

Démarrage App : Lancez le conteneur avec vos variables d'environnement pour les mails.

Bash
docker run -d --name mon-projet-java --network asbank_network -p 8080:8080 -e MAIL_USER="votre_mail" -e MAIL_PASSWORD="votre_mot_de_passe_app" mon-projet-java
Accès application : http://localhost:8080/_00_ASBank2023/

💳 2. Fonctionnalités & Règles Métier
🔒 Gestion des Cartes Bancaires (Phase 2)

L'implémentation suit des règles de gestion strictes validées avec le client :

Autorité du Gestionnaire : Seul le gestionnaire peut créer une carte, définir le compte lié et modifier les plafonds.

Types de Débit : Choix entre débit immédiat ou différé défini à la création (non modifiable par la suite).

Plafond 30 jours glissants : La capacité de paiement est calculée sur une fenêtre mobile de 30 jours.

Sécurité : Le client peut bloquer sa carte en cas d'urgence, mais seul le gestionnaire peut la débloquer.

🎨 Charte Graphique "Université de Lorraine"

Conformément aux directives de l'UL, l'interface a été modernisée :

Intégration du logo officiel de l'Université de Lorraine sur la page de garde.

Respect des codes couleurs et des polices de la charte graphique institutionnelle.

Mise à jour des mentions temporelles pour l'année 2025-2026 sur le footer et l'accueil.

📈 3. Qualité du Code et Tests
🧪 Tests et Couverture

Tests Unitaires (JUnit) : Exécutables via mvn test.

Couverture JaCoCo : Les rapports de couverture sont générés dans target/site/jacoco/index.html.

Tests Sélénium : Tests de recette automatisés pour valider les parcours critiques (en cours d'implémentation).

🔍 Analyse Statique

SonarCloud : Suivi de la dette technique et des vulnérabilités. L'objectif est l'amélioration continue de la note globale sur le code legacy.

CI/CD : Pipeline GitHub Actions automatisant la compilation et les tests à chaque push.

📂 Architecture du Projet
Plaintext
_00_ASBank2023/
├── script/                 # Scripts SQL (Initialisation et Tests)
├── src/main/java/          # Code source Java (Spring, Hibernate, Struts 2)
├── src/main/resources/     # Configuration (applicationContext.xml)
├── WebContent/
│   ├── JSP/                # Vues (Index, ListeComptes, Footer...)
│   └── style/              # Assets CSS et Images (Logo UL)
├── Dockerfile              # Configuration de l'image application
├── docker-compose.yml      # Orchestration (MySQL, Adminer)
└── README.md               # Ce fichier
🛠 Maintenance
Pour réinitialiser complètement l'environnement (nettoyage des volumes et réseaux) :

Bash
docker-compose down -v