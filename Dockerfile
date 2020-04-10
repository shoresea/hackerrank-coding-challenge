FROM openjdk:8-jre-alpine

MAINTAINER mav3n

RUN mkdir -p /apps
RUN mkdir -p /apps/conf

ENV DIRPATH /apps
WORKDIR /apps

COPY target/coding-challenge-1.0.2.jar /apps/coding-challenge-1.0.2.jar
#COPY target/coding-challenge-1.0.2-jar-with-dependencies.jar /apps/

#EXPOSE 3000

ENTRYPOINT ["sh","-c","java -jar coding-challenge-1.0.2.jar"]

VOLUME /apps/conf
