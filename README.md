# 💻 Projet Qualité de Développement – ASBank 2026
📚 **R5.A08 – IUT Informatique de Metz | Université de Lorraine**

Ce projet consiste en la maintenance évolutive et l'amélioration qualitative d'une application Java EE. L’accent est mis sur l’industrialisation du déploiement via Docker, l’intégration continue (CI/CD) et le respect d’une charte graphique institutionnelle stricte.

---

# 👥 Équipe — Groupe 3

| Membre | Rôle |
|--------|------|
| **CHOLLET Thomas** | Scrum Master & Développeur |
| **MORINON Lilian** | Développeur & Responsable Documentation |
| **AIT BAHA Said** | Développeur |
| **KERBER Alexandre** | Développeur |

---

# 🚀 1. Installation et Déploiement

Le projet est entièrement conteneurisé. L’utilisation de Tomcat local est désormais obsolète au profit d’une infrastructure Docker.

## 📦 Pré-requis logiciels

Avant toute installation, assurez-vous d’avoir les outils suivants installés et configurés :

### 🔹 Docker Desktop
Moteur de conteneurisation.

### 🔹 Java JDK 11 & Maven ≥ 3.9
Nécessaires pour le build initial.

### 🔹 Configuration BDD
Vérifiez que `applicationContext.xml` pointe sur :

`db:3306`

---

## 🛠 Procédure de lancement

### 1️⃣ Build Maven — Génération de l’artifact (tests ignorés)

```bash
mvn clean install -DskipTests
```

### 2️⃣ Build de l’image Docker

```bash
docker build -t mon-projet-java .
```

### 3️⃣ Infrastructure & Réseau — Création du réseau et lancement BDD

```bash
docker network create asbank_network
docker-compose up -d
```

### 4️⃣ Import SQL

Accédez à :

`http://localhost:8082`

- Sélectionnez la base **testdb**
- Importez : `script/chollet14u_bankiut.sql`

### 5️⃣ Démarrage de l’application

```bash
docker run -d --name mon-projet-java --network asbank_network -p 8080:8080 -e MAIL_USER="votre_mail" -e MAIL_PASSWORD="votre_mot_de_passe_app" mon-projet-java
```

### 🌐 Accès application

`http://localhost:8080/_00_ASBank2023/`

---

# 💳 2. Fonctionnalités & Règles Métier

## 🔒 Gestion des Cartes Bancaires — Phase 2

L’implémentation suit des règles de gestion strictes validées avec le client :

- **Autorité du gestionnaire** : seul le gestionnaire peut créer une carte, définir le compte lié et modifier les plafonds.
- **Types de débit** : choix entre débit immédiat ou différé défini à la création (non modifiable par la suite).
- **Plafond 30 jours glissants** : la capacité de paiement est calculée sur une fenêtre mobile de 30 jours.
- **Sécurité** : le client peut bloquer sa carte en cas d’urgence, mais seul le gestionnaire peut la débloquer.

---

## 🎨 Charte Graphique — Université de Lorraine

Conformément aux directives UL, l’interface a été modernisée :

- Intégration du logo officiel de l’Université de Lorraine sur la page de garde.
- Respect des codes couleurs et des polices de la charte graphique institutionnelle.
- Mise à jour des mentions temporelles pour l’année 2025-2026 sur le footer et l’accueil.

---

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

---

## 🔍 Analyse Statique & CI/CD

- **SonarCloud** : suivi de la dette technique et des vulnérabilités, amélioration continue de la note globale du code legacy.
- **CI/CD GitHub Actions** : pipeline automatisant la compilation et les tests à chaque push.

---

# 📂 Architecture du Projet

```plaintext
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
```

---

# 🛠 Maintenance

Pour réinitialiser complètement l’environnement (nettoyage des volumes et réseaux) :

```bash
docker-compose down -v
```

---

**ASBank 2026 — Université de Lorraine**  
Projet Qualité de Développement R5.A08
