FROM maven:3-amazoncorretto-21 AS build

WORKDIR /app

COPY pom.xml ./
RUN mvn dependency:go-offline -q

COPY src ./src
RUN mvn clean package -DskipTests

FROM amazoncorretto:21-alpine3.20

WORKDIR /app

COPY --from=build /app/target/Lata-Velha-0.0.1.jar ./app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]