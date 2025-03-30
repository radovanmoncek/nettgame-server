FROM openjdk:25
LABEL authors="Radovan Monček"
MAINTAINER Radovan Monček

COPY target/gameserver.jar gameserver.jar

EXPOSE 4321

ENTRYPOINT ["java", "-jar", "/gameserver.jar", "--mode", "containerized"]
