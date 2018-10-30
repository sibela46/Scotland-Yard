FROM ubuntu:14.04

RUN mkdir java && mkdir java/ScotlandYard && mkdir java/ScotlandYard/build

WORKDIR /java/ScotlandYard

COPY config/* ./
COPY src/ ./src/
ADD init/run.sh ./
ADD init/build.sh ./

RUN chmod a+x run.sh
RUN chmod a+x build.sh

RUN apt-get -y update && apt-get -y upgrade \
    && apt-get install -y openjdk-7-jdk maven

RUN export uid=1000 gid=1000 \
    && echo "JAVA_HOME=$(which java)" | tee -a /etc/environment \
    && /bin/bash -c "source /etc/environment"

RUN mvn clean package

RUN cp ./target/*.jar /java/ScotlandYard/build
