# (빌드 단계)
FROM gradle:8.7.0-jdk17 AS builder
WORKDIR /workspace/app
COPY . .
RUN ./gradlew clean build -x test

# (런타임 단계)
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=builder /workspace/app/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
EXPOSE 8080