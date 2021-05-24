ARG openJdkVersion=8

FROM openjdk:${openJdkVersion}-jre-alpine as base

ARG USER="mindmap"

RUN adduser --disabled-password --uid 1000 --gecos '' ${USER}
RUN apk update

WORKDIR /home/${USER}/

COPY ./target/mind-map-*.jar ./service.jar

EXPOSE 8080

USER ${USER}

CMD ["java", "-jar", "service.jar"]