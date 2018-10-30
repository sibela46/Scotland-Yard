#!/bin/sh
mvn clean package
cp ./target/*.jar ./build/build-$(date +"%Y-%m-%d_%H-%M-%S").jar
