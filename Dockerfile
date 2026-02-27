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
# Usamos Debian Bookworm (não Ubuntu Jammy) porque:
# - Debian tem o pacote 'chromium' real no apt (Ubuntu só tem snap)
# - Playwright precisa do Chromium para renderizar os gráficos do relatório comparativo
FROM eclipse-temurin:21-jre-bookworm

# Instala Chromium e dependências de sistema necessárias para headless
RUN apt-get update && apt-get install -y --no-install-recommends \
    chromium \
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
    libasound2 \
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
    libxtst6 \
 && rm -rf /var/lib/apt/lists/*

# Informa ao Playwright qual binário usar (evita download de ~300MB)
ENV CHROMIUM_PATH=/usr/bin/chromium

# Diretório da aplicação no container final
WORKDIR /app

# Copiamos APENAS o jar gerado no stage de build
# Nada de código-fonte, Maven ou cache vai para produção
COPY --from=build /app/target/*.jar app.jar

# Porta padrão do Spring Boot
EXPOSE 8080

# Comando de inicialização do container
# O Java 21 já está dentro da imagem, independente do Coolify
ENTRYPOINT ["java", "-jar", "app.jar"]