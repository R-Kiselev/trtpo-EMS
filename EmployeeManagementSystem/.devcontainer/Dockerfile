FROM gradle:8.10.2-jdk23 AS builder

WORKDIR /app

COPY ./build.gradle.kts settings.gradle.kts ./
COPY ./gradlew ./
COPY ./gradle ./gradle

RUN chmod +x gradlew

RUN ./gradlew dependencies --no-daemon

COPY ./src ./src

RUN ./gradlew bootJar --no-daemon

FROM openjdk:23-jdk-slim

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]