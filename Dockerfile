FROM gradle:8.4.0-jdk17-jammy as builder

WORKDIR /app

COPY . ./

RUN gradle bootJar

FROM eclipse-temurin:17-jdk-jammy
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]

