# ===============================
# STAGE 1 — BUILD
# ===============================
# Usamos Amazon Corretto 21 Alpine:
# - Mais leve que a imagem padrão, reduzindo chance de timeout na rede
# - Totalmente compatível com Java 21
FROM amazoncorretto:21-alpine AS build

# Define o diretório de trabalho
WORKDIR /app

# Instala o bash no Alpine (necessário para o mvnw rodar)
RUN apk add --no-cache bash

# Copiamos todo o código-fonte
COPY . .

# Executa o build
# - B: Modo batch para logs mais limpos
# - DskipTests: Pula testes para agilizar o deploy
RUN chmod +x ./mvnw \
 && ./mvnw -B -DskipTests clean package


# ===============================
# STAGE 2 — RUNTIME
# ===============================
# Usamos a imagem mínima do JRE para produção
FROM amazoncorretto:21-alpine

WORKDIR /app

# Copiamos o jar gerado no estágio anterior
COPY --from=build /app/target/*.jar app.jar

# Porta padrão do Spring Boot
EXPOSE 8080

# Variáveis de ambiente para RabbitMQ (conforme o erro anterior)
ARG RABBITMQ_QUEUE=queue_auditory
ARG RABBITMQ_HOST=rabbitmq-q4s08kc8cwogwgkkwg0ckwkk.45.187.224.228.sslip.io

# Comando de inicialização
ENTRYPOINT ["java", "-jar", "app.jar"]