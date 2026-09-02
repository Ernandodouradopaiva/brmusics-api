# Imagem: brmusics-api (Spring Boot)
FROM maven:3.9-eclipse-temurin-23 AS build
WORKDIR /src
COPY . .
RUN mvn -B -DskipTests clean package \
 && mkdir -p /out \
 && find /src/target -maxdepth 1 -type f -name '*.jar' ! -name '*.jar.original' -exec cp {} /out/app.jar \;

FROM eclipse-temurin:23-jre
WORKDIR /app

LABEL org.opencontainers.image.title="brmusics-api"
LABEL org.opencontainers.image.description="API REST do BRMusics"

COPY --from=build /out/app.jar /app/app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
