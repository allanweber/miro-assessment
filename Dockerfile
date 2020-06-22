FROM maven:3.6.0-jdk-11-slim AS build
COPY src /home/app/src
COPY pmd /home/app/pmd
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

FROM adoptopenjdk/openjdk11:alpine-jre
COPY --from=build /home/app/target/miro-widgets*.jar /usr/local/lib/miro-widgets.jar
EXPOSE 8080
ENV JAVA_OPTS  "\
    -Xmx512M"
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /usr/local/lib/miro-widgets.jar"]