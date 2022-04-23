FROM maven:3.8.4-jdk-11-slim as build
COPY . /usr/src/app/
RUN mvn -f /usr/src/app/pom.xml clean install -DskipTests

FROM gcr.io/distroless/java
EXPOSE 8080
COPY --from=build /usr/src/app/target/*.jar /usr/app/*.jar
ENTRYPOINT ["java", "-jar", "/usr/app/*.jar"]