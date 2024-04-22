FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
COPY . /home/app
WORKDIR /home/app
RUN mvn -B -DskipTests -f /home/app/pom.xml clean package -pl Acsp-Bot -am

FROM eclipse-temurin:21-alpine AS runtime
COPY --from=build /home/app/Acsp-Bot/target/Acsp-Bot-0.1.0.jar /usr/local/lib/acsp-bot.jar
EXPOSE 80
ENTRYPOINT ["java","-jar","/usr/local/lib/acsp-bot.jar","-Dreactor.schedulers.defaultBoundedElasticOnVirtualThreads=true", "-Xmx1G", "-Xms64M", "-Xss512k"]