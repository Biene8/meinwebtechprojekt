# 🌱 STAGE 1 – Robuster Build mit besserer Fehlerbehandlung
FROM gradle:8.5.0-jdk21 AS build

# Arbeitsverzeichnis setzen
WORKDIR /home/gradle/project

# Gradle Wrapper und Build-Dateien zuerst kopieren (für besseres Caching)
COPY --chown=gradle:gradle gradlew .
COPY --chown=gradle:gradle gradle gradle
COPY --chown=gradle:gradle build.gradle .
COPY --chown=gradle:gradle settings.gradle .

# Gradle Wrapper ausführbar machen
RUN chmod +x gradlew

# Dependencies downloaden (separater Layer für besseres Caching)
RUN ./gradlew dependencies --no-daemon || true

# Quellcode kopieren
COPY --chown=gradle:gradle src src

# Projekt bauen mit verbose Logging
RUN ./gradlew clean build --no-daemon --info --stacktrace

# 🚀 STAGE 2 – Minimales Runtime-Image
FROM eclipse-temurin:21-jre-alpine

# Arbeitsverzeichnis
WORKDIR /app

# JAR-Datei kopieren
COPY --from=build /home/gradle/project/build/libs/*.jar app.jar

# Port freigeben
EXPOSE 8080

# Anwendung starten mit optimierten JVM-Parametern
ENTRYPOINT ["java", \
  "-Dserver.port=${PORT:-8080}", \
  "-Xmx512m", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-jar", "app.jar"]
