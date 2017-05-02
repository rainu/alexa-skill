FROM openjdk:8-jre-alpine
MAINTAINER rainu <rainu@raysha.de>

# needed by coloud
RUN apk add --update git python2 python-dev py-pip build-base libxml2-dev libxslt-dev &&\
    git clone https://github.com/tobixen/calendar-cli.git &&\
    cd calendar-cli && chmod +x ./setup.py && ./setup.py install &&\
    cd / && rm -rf calendar-cli &&\
    apk del git python-dev build-base &&\
    rm -rf /var/cache/apk/*

COPY ./docker/start.sh /application/start.sh

#libs
COPY ./server/target/lib/*.jar /application/lib/
COPY ./cloud/target/lib/*.jar /application/lib/

#modules
COPY ./server/target/*.jar /application/lib/server.jar
COPY ./hello/target/*.jar /application/lib/hello.jar
COPY ./cloud/target/*.jar /application/lib/coud.jar


EXPOSE 8080

ENTRYPOINT ["/application/start.sh"]