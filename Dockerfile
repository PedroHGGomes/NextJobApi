# ============================================
# Multi-stage build para NextJob API
# ============================================

# Stage 1: Build da aplicação
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copiar arquivos do Gradle
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Copiar código fonte
COPY src src

# Dar permissão de execução ao gradlew
RUN chmod +x ./gradlew

# Build da aplicação (sem testes)
RUN ./gradlew clean build -x test --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiar JAR do stage anterior
COPY --from=builder /app/build/libs/*.jar app.jar

# Criar usuário não-root para segurança
RUN addgroup -S spring && adduser -S spring -G spring && \
    chown -R spring:spring /app
USER spring:spring

# Expor porta da aplicação
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Configurar JVM para containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0"

# Executar aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]
