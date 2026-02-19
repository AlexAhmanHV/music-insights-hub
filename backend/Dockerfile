FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml ./
COPY .mvn ./.mvn
COPY mvnw ./
COPY mvnw.cmd ./
COPY src ./src

RUN chmod +x ./mvnw && ./mvnw -DskipTests clean package
RUN JAR_FILE=$(ls target/*.jar | grep -v 'original' | head -n 1) && cp "$JAR_FILE" target/app.jar

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/app.jar ./app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
