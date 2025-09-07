FROM openjdk:20-jdk
COPY build/libs/*.jar app.jar
EXPOSE 8088
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "/app.jar"]