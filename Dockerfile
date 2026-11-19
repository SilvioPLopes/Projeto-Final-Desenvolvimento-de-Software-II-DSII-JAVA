FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
COPY src src
RUN mvn -B verify

FROM eclipse-temurin:17-jre
WORKDIR /app
RUN mkdir /dados && chown 10001:10001 /dados
COPY --from=build /build/target/oficina-os-1.0.0.jar /app/oficina.jar
ENV JDBC_URL=jdbc:h2:file:/dados/oficina
EXPOSE 8080
USER 10001:10001
ENTRYPOINT ["java", "-jar", "/app/oficina.jar"]
