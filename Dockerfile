# Usa una imagen de Java oficial
FROM openjdk:17-jdk-slim

# Establece el directorio de trabajo
WORKDIR /app

# Copia el archivo JAR de tu aplicación
COPY quips-0.0.1-SNAPSHOT.jar app.jar

# Expone el puerto en el que corre tu aplicación
EXPOSE 8080

# Define el comando para ejecutar la aplicación
ENTRYPOINT ["java", "-XX:+EnableDynamicAgentLoading", "-jar", "app.jar"]

