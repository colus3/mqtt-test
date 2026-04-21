FROM gradle:8.14-jdk21 AS builder
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY src src
RUN gradle bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
