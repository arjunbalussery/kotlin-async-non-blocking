#!/bin/bash
source ~/.bash_profile
#running postgres
#docker pull postgres:16.1
#docker run -d -p 5432:5432 -e POSTGRES_PASSWORD=pass123 postgres:16.1
#cleaning the build folder
sleep 5.0s
rm -r build
#migrate scripts to db using flyway plugin
./gradlew flywayMigrate
#generating jooq files and creating jar
./gradlew clean build
 #kill all docker process
docker kill $(docker ps -q)
#build multi-container application
docker-compose up --build