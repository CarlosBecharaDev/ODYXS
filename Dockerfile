# ─────────────────────────────────────────────────────────────
# Etapa 1 — Compilar el jar con Maven (cache de dependencias)
# ─────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

# Copiamos primero el pom para aprovechar la cache de capas de Docker:
# las dependencias solo se re-descargan si cambia el pom.xml.
COPY pom.xml .
RUN mvn -q -B dependency:go-offline

COPY src ./src
RUN mvn -q -B clean package -DskipTests

# ─────────────────────────────────────────────────────────────
# Etapa 2 — Imagen de ejecución ligera (solo JRE)
# ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /build/target/*.jar app.jar

# Carpeta persistente de imágenes subidas.
# user.home se fija a /app para que StorageService (${user.home}/odyxs-uploads)
# y WebConfig usen la misma ruta dentro del contenedor.
RUN mkdir -p /app/odyxs-uploads

EXPOSE 8080
ENTRYPOINT ["java", "-Duser.home=/app", "-jar", "app.jar"]
