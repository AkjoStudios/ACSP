FROM maven:3-eclipse-temurin-21-alpine
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -B -DskipTests -f /home/app/pom.xml clean package

FROM eclipse-temurin:21-alpine
COPY --from=build /home/app/target/AcspBackend-0.1.0.jar /usr/local/lib/acsp-backend.jar
EXPOSE 80
ENTRYPOINT ["java","-jar","/usr/local/lib/acsp-backend.jar","-Dreactor.schedulers.defaultBoundedElasticOnVirtualThreads=true"]