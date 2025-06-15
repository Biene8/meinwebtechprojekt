# 🌱 STAGE 1 – Build mit Gradle und JDK 21
FROM gradle:8.5.0-jdk21 AS build

# Gradle Wrapper und Projekt kopieren
COPY --chown=gradle:gradle . /home/gradle/project
WORKDIR /home/gradle/project

# Gradle Wrapper ausführbar machen
RUN chmod +x gradlew

# Mit Gradle Wrapper bauen
RUN ./gradlew build --no-daemon

# 🚀 STAGE 2 – Run mit minimalem Java 21 JRE
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# JAR-Datei kopieren
COPY --from=build /home/gradle/project/build/libs/*.jar app.jar

# Port für Render.com
EXPOSE 8080

# Anwendung starten mit Port-Konfiguration
ENTRYPOINT ["java", "-Dserver.port=${PORT:-8080}", "-jar", "app.jar"]
