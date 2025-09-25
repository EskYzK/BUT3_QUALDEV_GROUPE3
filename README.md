# 💻 Projet Qualité de Développement – **ASBank**

📚 Projet réalisé dans le cadre du cours **R5.A08 – Qualité de Développement** (IUT Informatique de Metz).  
Ce projet déploie une application Java EE avec **Spring**, **Hibernate**, **Tomcat 9** et une base **MySQL**.

---

## 🛠️ Prérequis

- macOS
- [IntelliJ IDEA Ultimate](https://www.jetbrains.com/idea/download) (licence étudiante JetBrains)
- Java **JDK 8**
- Maven
- Apache **Tomcat 9**
- MySQL (version 8+)

---

## ⚙️ Installation

### 1. IntelliJ IDEA Ultimate
- Télécharger et installer IntelliJ.
- Activer la licence avec l’email étudiant.

### 2. JDK 8
```bash
java -version
```
➡️ Doit afficher `1.8.x`.  
Si ce n’est pas le cas → installer [Temurin 8](https://adoptium.net/temurin/releases/?version=8).

### 3. Tomcat 9
- Télécharger [Tomcat 9 (tar.gz)](https://tomcat.apache.org/download-90.cgi).
- Extraire dans :
  ```
  ~/Documents/ANNEE 3/QualiteDev/apache-tomcat-9.0.109
  ```

### 4. Import du projet
- Ouvrir `_00_ASBank2023/` dans IntelliJ (projet Maven).
- Vérifier **Project SDK** → Java 1.8.
- Compiler une première fois :
```bash
mvn clean install -DskipTests
```

---

## 🚀 Configuration Tomcat dans IntelliJ

1. **Ajouter Tomcat**  
   `Preferences → Build, Execution, Deployment → Application Servers`  
   → pointer vers `apache-tomcat-9.0.109`.

2. **Créer une configuration Run**
    - `Run → Edit Configurations` → **Tomcat Local**
    - HTTP Port : `8080`
    - Deployment → ajouter **artifact** `war exploded`
    - **Context path** :
      ```
      /_00_ASBank2023
      ```

3. **Artifact**
    - `File → Project Structure → Artifacts`
    - Ajouter `Web Application: Exploded → From Modules`.

4. **Lancer**  
   ▶️ Run → accéder à :  
   👉 [http://localhost:8080/_00_ASBank2023](http://localhost:8080/_00_ASBank2023)

---

## 🗄️ Base de données MySQL

1. Créer les bases :
   ```sql
   CREATE DATABASE bankiuttest;
   CREATE DATABASE bankiut;
   ```

2. Importer le `.sql` fourni.

3. Exemple de config `applicationContext.xml` :

```xml
<bean id="dataSource" scope="singleton"
      class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
    <property name="driverClassName" value="com.mysql.cj.jdbc.Driver" />
    <property name="url" value="jdbc:mysql://localhost:3306/bankiuttest?useSSL=false&amp;serverTimezone=UTC" />
    <property name="username" value="root" />
    <property name="password" value="" /> <!-- changer si besoin -->
    <property name="defaultAutoCommit" value="false" />
</bean>
```

⚠️ Attention : en XML, utiliser `&amp;` (pas `&`).

---

## 🎨 CSS et ressources

- Placer CSS dans `WebContent/css/` ou `src/main/webapp/css/`.
- Vérifier qu’ils sont inclus dans l’artifact.
- Dans vos JSP :

```html
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" />
```

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

- [x] Installation IntelliJ Ultimate
- [x] Installation JDK 8
- [x] Installation Tomcat 9
- [x] Import projet `_00_ASBank2023`
- [x] Création artifact `war exploded`
- [x] Déploiement sur Tomcat avec `/ _00_ASBank2023`
- [x] Connexion MySQL via Spring/Hibernate
- [x] Correction `&amp;serverTimezone=UTC`
- [x] Vérification CSS
- [x] Dépôt GitHub fonctionnel  
