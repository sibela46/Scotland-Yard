FROM ubuntu:16.04

RUN mkdir java && mkdir java/ScotlandYard

WORKDIR /java/ScotlandYard

COPY config/* ./
COPY src/ ./src/

RUN apt-get -y update && apt-get -y install && apt-get -y upgrade \
    && apt-get install -y software-properties-common && add-apt-repository ppa:webupd8team/java \
    && apt-get -y update && echo "oracle-java8-installer shared/accepted-oracle-license-v1-1 select true" | debconf-set-selections \
    && apt-get install -y oracle-java8-installer

RUN export uid=1000 gid=1000 \
    && echo "JAVA_HOME=$(which java)" | tee -a /etc/environment \
    && /bin/bash -c "source /etc/environment"

RUN apt-get install -y openjdk-8-jdk maven openjfx \
    && mvn clean package

CMD mvn exec:java -Dexec.mainClass="uk.ac.bris.cs.scotlandyard.Main" -s /java/ScotlandYard/settings.xml
