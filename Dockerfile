# Use a imagem oficial do Maven para compilar o projeto
FROM maven:3.8.3-openjdk-17 AS build

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copia o código-fonte para o diretório de trabalho
COPY . .

# Compila o projeto e gera o arquivo JAR
RUN mvn clean package -DskipTests

# Usa a imagem oficial do OpenJDK para rodar a aplicação
FROM openjdk:17-jdk-slim

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copia o arquivo JAR do estágio anterior para o contêiner final
COPY --from=build /app/target/*.jar app.jar

# Define o comando para rodar a aplicação
CMD ["java", "-jar", "app.jar"]