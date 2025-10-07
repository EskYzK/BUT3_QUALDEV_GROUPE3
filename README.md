# 💻 Projet Qualité de Développement – **ASBank**

📚 Projet réalisé dans le cadre du cours **R5.A08 – Qualité de Développement** (IUT Informatique de Metz).  
Ce projet déploie une application Java EE avec **Spring**, **Hibernate**, **Tomcat 9** et une base **MySQL**.

---

## 1. Pré-requis logiciels

Avant toute installation, assurez-vous d’avoir les outils suivants installés et configurés :

### 🔹 Java JDK 11

- Le projet fonctionne avec **Java 11**.  
- Dans IntelliJ IDEA, allez dans :  
  **File > Project Structure > Project**  
  et téléchargez/configurez directement un **JDK 11** (exemple : *Microsoft OpenJDK 11*).  
- Vérifiez que le projet utilise bien **Java 11** comme SDK.  

### 🔹 Apache Maven (≥ 3.9)

* Téléchargez Maven [ici](https://maven.apache.org/download.cgi).
* Décompressez-le dans un dossier stable (ex. `C:\apache-maven-3.9.x`).
* Ajoutez le chemin `bin/` de Maven dans la variable d’environnement `PATH`.
* Vérifiez l’installation avec :

  ```bash
  mvn -v
  ```

  Résultat attendu : la version de Maven + Java 11.

### 🔹 Apache Tomcat 9.x

* Téléchargez la version ZIP depuis [Tomcat 9 Downloads](https://tomcat.apache.org/download-90.cgi).
* Décompressez-le dans un dossier (ex. `C:\apache-tomcat-9.0.xx`).
* Vérifiez l’installation avec :

  ```bash
  catalina.bat version
  ```

### 🔹 MySQL / MariaDB

* Téléchargez et installez [MySQL Community Server](https://dev.mysql.com/downloads/mysql/) ou utilisez XAMPP/WAMP.
* Vérifiez que le service MySQL est démarré.

### 🔹 IntelliJ IDEA Ultimate

* Utilisez la version **Ultimate** (car elle gère Tomcat et les projets Java EE nativement).

#### 🔸 Connexion VPN obligatoire
Important : pour compiler et exécuter le projet ASBank, vous devez être connecté au VPN Cisco Secure Client (réseau de l’IUT). 
Sans cette connexion, Maven ne parviendra pas à récupérer certaines dépendances ni à se connecter à la base MySQL distante, et la commande mvn clean install échouera.


---

## 2. Installation des bases de données

1. Créez deux bases vides dans MySQL :

   * `nomuser_bankiut` (base principale de l’application)
   * `nomuser_bankiut_test` (base utilisée pour les tests)

2. Importez les fichiers SQL fournis dans le dossier `script/` :

   * `dumpSQL.sql` → pour la base principale
   * `dumpSQL_JUnitTest.sql` → pour la base de test

---

## 3. Configuration du projet

### a) Récupération des dépendances

Dans IntelliJ ou via un terminal, exécutez :

```bash
mvn clean install -DskipTests
```

Cette commande permet de :

* Nettoyer le projet,
* Télécharger toutes les dépendances nécessaires,
* Compiler et générer le WAR,
* **Ignorer les tests unitaires** (qui nécessitent la configuration complète de la base de test).

Résultat attendu : **BUILD SUCCESS**.

### b) Connexion à la base de données

Le projet utilise **Spring** et **Hibernate** pour accéder à la base MySQL.

* Le fichier `applicationContext.xml` contient la configuration pour la base principale.
* Les fichiers `TestsBanqueManager-context.xml` et `TestsDaoHibernate-context.xml` définissent la configuration pour la base de test.

Vérifiez dans ces fichiers que :

* Le nom de la base correspond bien à `nomuser_bankiut` (ou `nomuser_bankiut_test` pour les tests).
* L’utilisateur et le mot de passe MySQL sont corrects (par défaut `root` / `root`, sauf si vous avez modifié votre configuration).

En résumé :

* **applicationContext.xml** → doit pointer sur la base principale.
* **fichiers de test** → doivent pointer sur la base de test.

---

## 4. Déploiement avec Tomcat

1. Dans IntelliJ, ouvrez **Run > Edit Configurations**.
2. Ajoutez une configuration **Tomcat Server > Local**.
3. Dans l’onglet **Deployment**, ajoutez l’artifact généré par Maven :

   ```
   _00_ASBank2023:war exploded
   ```
4. Conservez le **contexte par défaut** généré par IntelliJ (ex. `/_00_ASBank2023_war_exploded`).
5. Cliquez sur **Apply**, puis **OK**.

---

## 5. Lancement de l’application

1. Lancez Tomcat depuis IntelliJ avec ▶️ **Run**.
2. Surveillez la console et attendez le message :

   ```
   Artifact _00_ASBank2023:war exploded: Artifact is deployed successfully
   ```
3. Ouvrez un navigateur et accédez à l’URL :

   ```
   http://localhost:8080/_00_ASBank2023_war_exploded
   ```

Vous devriez voir la page d’accueil de l’application :
**« Bienvenue sur l'application IUT Bank 2023 »** avec le lien vers la page de connexion.

---

## 🐙 Git & GitHub

### Initialisation
```bash
cd "Documents/ANNEE 3/QualiteDev"
git init
git remote add origin https://github.com/EskYzK/BUT3_QUALDEV_GROUPE3.git
```

### `.gitignore`
```gitignore
apache-tomcat-9.0.109/
.idea/
out/
target/
*.iml
```

### Ajout & Push
```bash
git add .
git commit -m "Ajout projet ASBank"
git pull --rebase origin main   # récupérer le travail du groupe
git push -u origin main
```

---

## ✅ État d’avancement

* [x] Installation **IntelliJ IDEA Ultimate**
* [x] Installation **JDK 11** 
* [x] Installation **Apache Tomcat 9**
* [x] Importation du projet **_00_ASBank2023** dans IntelliJ
* [x] Création de l’**artifact WAR exploded**
* [x] Déploiement sur **Tomcat** avec le contexte `/_00_ASBank2023_war_exploded`
* [x] Connexion **MySQL** via **Spring / Hibernate**
* [x] Vérification du rendu **CSS** dans l’application
* [x] Dépôt **GitHub** initialisé et fonctionnel (`git add / commit / push`)
