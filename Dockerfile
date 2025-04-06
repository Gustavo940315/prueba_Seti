FROM eclipse-temurin:17-jdk

ARG JAR_FILE=build/libs/ms-franchises-0.0.1-SNAPSHOT.jar

WORKDIR /app
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]