FROM ubuntu:22.04

ENV DEBIAN_FRONTEND=noninteractive

FROM eclipse-temurin:11-jdk-jammy AS build
RUN apt-get clean && rm -rf /var/lib/apt/lists/* \
 && apt-get update \
 && apt-get install -y maven
WORKDIR /app
COPY pom.xml .
RUN mvn -B -f pom.xml dependency:resolve

COPY src ./src
COPY WebContent ./WebContent

RUN mvn -U clean package -DskipTests

FROM tomcat:9.0-jdk11

COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8082
CMD ["catalina.sh", "run"]