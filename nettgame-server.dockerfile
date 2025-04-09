FROM openjdk:25
LABEL authors="Radovan Monček"
MAINTAINER Radovan Monček

COPY "target/server-1.0-jar-with-dependencies.jar" "server-1.0.jar"

EXPOSE 4321

#https://howtodoinjava.com/java/basics/java-system-properties/
#https://stackoverflow.com/questions/33408626/how-to-pass-system-property-to-docker-containers
ENTRYPOINT ["java", "-Dcontainerized=\"true\"", "-jar", "/server-1.0.jar", "--mode", "containerized"]
