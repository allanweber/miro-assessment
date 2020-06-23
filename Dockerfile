FROM adoptopenjdk/openjdk11:alpine-jre

EXPOSE 8080

ARG ENV_ARG=dev

ENV ENV_PROFILE=$ENV_ARG

ENV JAVA_OPTS  "\
    -XX:+UnlockExperimentalVMOptions \
    -Xmx512M \
    -Dspring.profiles.active=\${ENV_PROFILE}"

ADD target/miro-widgets*.jar miro-widgets.jar

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar miro-widgets.jar"]