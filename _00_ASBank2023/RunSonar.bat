@echo off
REM Chargement des variables depuis le fichier .env
if exist .env (
    for /f "tokens=1,* delims==" %%a in ('type .env') do (
        set %%a=%%b
    )
) else (
    echo Le fichier .env est introuvable.
    exit /b 1
)

REM Lancement de Sonar avec les variables chargées
mvn clean verify sonar:sonar -Dsonar.projectKey=%SONAR_PROJECT_KEY% -Dsonar.host.url=%SONAR_HOST_URL% -Dsonar.login=%SONAR_LOGIN%