# ===============================
# STAGE 1 — BUILD
# ===============================
# Versão Alpine: muito menor e mais rápida de baixar
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

# O Alpine precisa do bash para rodar o ./mvnw
RUN apk add --no-cache bash

COPY . .

# Build controlado
RUN chmod +x ./mvnw \
 && ./mvnw -B -DskipTests clean package

# ===============================
# STAGE 2 — RUNTIME
# ===============================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copia apenas o necessário
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]