# Etapa 1: Build da aplicação
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Expõe a porta do serviço de autenticação
EXPOSE 8079

# Health check para verificar se o serviço está saudável
HEALTHCHECK --interval=30s --timeout=3s --start-period=20s --retries=3 \
  CMD curl -f http://localhost:8079/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]