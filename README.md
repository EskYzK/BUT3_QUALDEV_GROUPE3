# 💻 Projet Qualité de Développement – ASBank 2026
📚 **R5.A08 – IUT Informatique de Metz | Université de Lorraine**

Ce projet consiste en la maintenance évolutive et l'amélioration qualitative d'une application Java EE. L’accent est mis sur l’industrialisation du déploiement via Docker, l’intégration continue (CI/CD) et le respect d’une charte graphique institutionnelle stricte.

# 👥 Équipe — Groupe 3

| Membre | Rôle        |
|--------|-------------|
| **CHOLLET Thomas** | Développeur |
| **MORINON Lilian** | Développeur |
| **AIT BAHA Said** | Développeur |
| **KERBER Alexandre** | Développeur |

# 🚀 1. Installation et Déploiement

Le projet est entièrement conteneurisé. L’utilisation de Tomcat local est désormais obsolète : tout le cycle de vie (Build, Tests, Déploiement) est géré automatiquement par Docker.

## 📦 Pré-requis

Avant toute installation, assurez-vous d’avoir les outils suivants installés et configurés :

### 🔹 VPN
**Vous devez être connecté au réseau de l'IUT de Metz.**  
Pourquoi ? Le build Docker lance les tests unitaires qui se connectent à la base de données de développement de l'IUT. Sans VPN, la construction de l'image échouera.

### 🔹 Docker Desktop
Le moteur de conteneurisation doit être installé et lancé.

### 🔹 Un comte Google (pour les emails)
Un compte avec la validation en deux étapes, ainsi qu'un mot de passe d'application généré.

## 🛠 Procédure de lancement

> [!IMPORTANT]
> **Avant de commencer :**
> 1. Assurez-vous que l'application **Docker Desktop est lancée** sur votre machine.
> 2. Activez le **VPN** et connectez-vous au réseau de l'**IUT de Metz**.
> 2. Ouvrez votre terminal dans le répertoire racine du projet : `BUT3_QUALDEV_GROUPE3/_00_ASBank2023/`.

### 1️⃣ Configuration des secrets (.env)

Créez un fichier nommé `.env` dans le répertoire (`BUT3_QUALDEV_GROUPE3/_00_ASBank2023/`) et insérez vos identifiants :

```
# Configuration E-mail (Gmail)
MAIL_USER=votre.email@gmail.com
MAIL_PASSWORD=votre_mot_de_passe_application

# Configuration Base de données (Utilisateurs)
DB_URL=jdbc:mysql://devbdd.iutmetz.univ-lorraine.fr:3306/chollet14u_bankiuttest?useSSL=false&serverTimezone=UTC
DB_USER=chollet14u_appli
DB_PASS=32301542
```

Créez un second fichier nommé `.env` à la racine du projet (`BUT3_QUALDEV_GROUPE3/`) et copiez ce contenu :

```
# Configuration Base de données
DB_URL=jdbc:mysql://devbdd.iutmetz.univ-lorraine.fr:3306/chollet14u_bankiuttest?useSSL=false&serverTimezone=UTC
DB_USER=chollet14u_appli
DB_PASS=32301542

# Configuration Sonar
SONAR_PROJECT_KEY=BANK_IUT_2023
SONAR_HOST_URL=http://localhost:9000
SONAR_LOGIN=sqp_c55e85adf298ef5ffae40c13ec282dfc9482301b
```

### 2️⃣ Lancement automatisé

Lancez l'unique commande nécessaire dans votre terminal :

```bash
docker-compose up -d --build
```

**Ce que fait cette commande automatiquement :**
1. Compile le projet avec Maven (dans un conteneur isolé).
2. Exécute les tests unitaires (connectés à la BDD IUT).
3. Construit l'image Tomcat optimisée.
4. Lance une base de données MySQL locale et importe automatiquement les données (bankiut.sql).
5. Démarre l'application sur le port 8080.
6. Lance phpMyAdmin sur le port 8082 afin d'avoir un accès à la base de données locale via une interface utilisateur.

### 🌐 Accès application

Une fois le déploiement terminé (attendre ~30 secondes), l'application est accessible ici :

`http://localhost:8080/_00_ASBank2023/`

### 🗃️ Accès Base de Données (Interface Web)

Pour visualiser et administrer la base de données sans ligne de commande :

`http://localhost:8082`

(La connexion est automatique avec les identifiants du fichier .env)

# 🛠 Arrêt et Maintenance

Pour arrêter l'application et supprimer les conteneurs :

```
docker-compose down
```

Pour tout réinitialiser (supprimer aussi la base de données locale pour forcer une réimportation propre au prochain lancement) :

```bash
docker-compose down -v
```

# 💳 2. Fonctionnalités & Règles Métier

## 🔒 Gestion des Cartes Bancaires

L’implémentation suit des règles de gestion strictes validées avec le client :

- **Autorité du gestionnaire** : Seul le gestionnaire peut créer une carte, définir le compte lié et modifier les plafonds.
- **Types de débit** : Choix entre débit immédiat ou différé défini à la création (non modifiable par la suite).
- **Plafond 30 jours glissants** : La capacité de paiement est calculée sur une fenêtre mobile de 30 jours.
- **Sécurité** : Le client peut bloquer sa carte en cas d’urgence, mais seul le gestionnaire peut la débloquer.
- **Débit différé** : Les débits différés sont gérés par un Task Scheduler tous les 1er jours du mois, à 3h du matin.

## 🎨 Charte Graphique — Université de Lorraine

Conformément aux directives UL, l’interface a été modernisée :

- Intégration du logo officiel de l’Université de Lorraine sur la page de garde.
- Respect des codes couleurs et des polices de la charte graphique institutionnelle.
- Mise à jour des mentions temporelles pour l’année 2025-2026 sur le footer et l’accueil.

# 📈 3. Qualité du Code et Tests

## 🧪 Tests et Couverture

**Tests Unitaires (JUnit)**
```bash
mvn test
```

**Couverture JaCoCo**  
Rapport généré dans : `target/site/jacoco/index.html`

**Tests Selenium**  
Tests de recette automatisés pour valider les parcours critiques (en cours d’implémentation).

## 🔍 Analyse Statique & CI/CD

- **SonarCloud** : Suivi de la dette technique et des vulnérabilités, amélioration continue de la note globale du code legacy.
- **CI/CD GitHub Actions** : Pipeline automatisant la compilation et les tests à chaque push.

# 📂 Architecture du Projet

```plaintext
.
├── .github/workflows/
│   └── sonarcloud.yml          # Pipeline CI/CD GitHub Actions
├── _00_ASBank2023/
│   ├── script/                 # Scripts SQL (Initialisation et Tests)
│   ├── src/main/java/          # Code source Java (Spring, Hibernate, Struts 2)
│   ├── src/main/resources/     # Configuration Struts (struts.xml)
│   ├── WebContent/
│   │   ├── JSP/                # Vues (Index, ListeComptes, Footer...)
│   │   ├── WEB-INF/            # Configuration Spring (applicationContext.xml) et web.xml
│   │   └── style/              # Assets CSS et Images (Logo UL)
│   ├── Dockerfile              # Configuration de l'image application
│   ├── docker-compose.yml      # Orchestration (MySQL, phpMyAdmin)
│   ├── pom.xml                 # Dépendances Maven
│   └── RunSonar.bat            # Script de lancement SonarQube local (Windows)
├── Sprint2/                    # Documentation et preuves du Sprint 2
├── Diagramme_UML_ASBank...     # Documentation technique
├── Fonctionnalités et bugs...  # Documentation fonctionnelle
├── .gitignore                  # Fichiers ignorés par Git
└── README.md                   # Ce fichier
```

---

**ASBank 2026 — Université de Lorraine**  
Projet Qualité de Développement R5.A08
