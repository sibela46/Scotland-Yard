FROM ubuntu:16.04

RUN mkdir java && mkdir java/ScotlandYard && mkdir java/ScotlandYard/build

RUN (mv /etc/localtime /etc/localtime.org \
    && ln -s /usr/share/zoneinfo/Europe/Berlin /etc/localtime)

WORKDIR /java/ScotlandYard

COPY config/* ./
COPY src/ ./src/
ADD init/run.sh ./
ADD init/build.sh ./

RUN chmod a+x run.sh
RUN chmod a+x build.sh

RUN apt-get -y update && apt-get -y upgrade \
    && apt-get install -y openjdk-8-jdk maven openjfx

RUN export uid=1000 gid=1000 \
    && echo "JAVA_HOME=$(which java)" | tee -a /etc/environment \
    && /bin/bash -c "source /etc/environment"

RUN mvn clean package
