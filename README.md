# alexa-skill
A modular spring-boot application for alexa (amazon) skill.

# how to build
```
  mvn clean install
```
After building the application a docker image was built. After that you can run
a container like this:
```
  docker run -it --rm -p 8080:8080 rainu/alexa
```

By default this application has no https support by its own. A alexa skill needs
a https endpoint. For example you can use a nginx-proxy to enable the ssl support.
In the following you can see a example docker-compose setup:
```
version: '2'
services:
  nginx-proxy:
    image: jwilder/nginx-proxy
    container_name: nginx-proxy
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - /etc/letsencrypt:/etc/letsencrypt
      - /var/run/docker.sock:/tmp/docker.sock:ro
    restart: always

  alexa:
    image: rainu/alexa
    container_name: alexa
    environment:
      - VIRTUAL_HOST=alexa.yourdomain.com
      - VIRTUAL_PORT=8080
    restart: always
```
For more information about the nginx-proxy see: https://hub.docker.com/r/jwilder/nginx-proxy/
