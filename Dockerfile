# Etapa 1: Build con Gradle + JDK
FROM gradle:8.7-jdk21 AS builder
WORKDIR /app

# Copiar todo el proyecto
COPY . .

# Compilar el proyecto generando el jar
RUN gradle clean bootJar --no-daemon

# Etapa 2: Imagen ligera con solo JRE
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copiar el jar generado
COPY --from=builder /app/build/libs/*.jar app.jar

# Exponer el puerto (opcional)
EXPOSE 8080

# Correr la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
