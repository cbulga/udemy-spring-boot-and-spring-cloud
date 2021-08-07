
FROM openjdk:8-jdk-alpine
LABEL maintainer="Nicola La Rocca <info@xantrix.it>"
LABEL description="Dockerfile servizio Gestuser"
VOLUME /tmp
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
ADD target/gestuser-0.0.1-SNAPSHOT.jar gestuser.jar
EXPOSE 8019
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar gestuser.jar
# For Spring-Boot project, use the entrypoint below to reduce Tomcat startup time.
#ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar gestuser.jar
