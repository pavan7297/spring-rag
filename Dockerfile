FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/springboot-rag-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
