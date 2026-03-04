# ===============================
# STAGE 1 — BUILD
# ===============================
# Usamos Eclipse Temurin 21 JDK porque:
# - O projeto compila com Java 21
# - O Coolify NÃO tem JDK 21 instalado
# - A imagem já vem com javac, Maven wrapper funciona sem gambiarra
FROM eclipse-temurin:21-jdk AS build

# Define o diretório de trabalho dentro do container
# Evita espalhar arquivos pela raiz do sistema
WORKDIR /app

# Copiamos todo o código-fonte para dentro da imagem
# Isso inclui pom.xml, src/, mvnw etc.
COPY . .

# Executa o build dentro da imagem
# Motivo:
# - O ambiente fica 100% controlado
# - Não depende do Java do host (Coolify)
# - Garante que o jar final é Java 21
RUN chmod +x ./mvnw \
 && ./mvnw -B -DskipTests clean package


 # ===============================
 # STAGE 2 — RUNTIME
 # ===============================
 FROM eclipse-temurin:21-jre

 # Remove snap chromium e instala a versão real
 RUN apt-get update && \
     apt-get remove -y chromium chromium-browser || true && \
     apt-get install -y --no-install-recommends \
     chromium-common \
     fonts-liberation \
     libnss3 \
     libnspr4 \
     libatk1.0-0 \
     libatk-bridge2.0-0 \
     libcups2 \
     libdrm2 \
     libxkbcommon0 \
     libxcomposite1 \
     libxrandr2 \
     libgbm1 \
     libpango-1.0-0 \
     libcairo2 \
     libx11-6 \
     libx11-xcb1 \
     libxcb1 \
     libxext6 \
     libxfixes3 \
     libxi6 \
     libxrender1 \
     libxss1 \
     libxtst6 && \
     rm -rf /var/lib/apt/lists/*

 # Deixa Playwright encontrar o chromium automaticamente
 ENV PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=false

 WORKDIR /app

 COPY --from=build /app/target/*.jar app.jar

 EXPOSE 8080

 ENTRYPOINT ["java", "-jar", "app.jar"]