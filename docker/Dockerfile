FROM openjdk:8-jre-alpine
MAINTAINER rainu <rainu@raysha.de>

COPY ./docker/start.sh /application/start.sh

# base module
COPY ./server/target/*.jar /application/lib/server.jar
COPY ./server/target/lib/*.jar /application/lib/

# hello module
COPY ./hello/target/*.jar /application/lib/hello.jar

# cloud module
COPY ./cloud/target/*.jar /application/lib/coud.jar
COPY ./cloud/target/lib/*.jar /application/lib/

RUN apk add --update git python2 python-dev py-pip build-base libxml2-dev libxslt-dev &&\
    git clone https://github.com/tobixen/calendar-cli.git &&\
    cd calendar-cli && chmod +x ./setup.py && ./setup.py install &&\
    cd / && rm -rf calendar-cli &&\
    apk del git python-dev build-base &&\
    rm -rf /var/cache/apk/*

EXPOSE 8080

CMD ["/application/start.sh"]