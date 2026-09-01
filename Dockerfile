FROM maven:3.9-eclipse-temurin-23

COPY . /app
WORKDIR /app

RUN mvn clean package -DskipTests
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/target/projetoA-api-0.0.1-SNAPSHOT.jar"]
