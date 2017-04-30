FROM openjdk:8-jre-alpine
MAINTAINER rainu <rainu@raysha.de>

COPY ./docker/start.sh /application/start.sh
COPY ./server/target/*.jar /application/lib/server.jar
COPY ./server/target/lib/*.jar /application/lib/
COPY ./hello/target/*.jar /application/lib/hello.jar

EXPOSE 8080

CMD ["/application/start.sh"]