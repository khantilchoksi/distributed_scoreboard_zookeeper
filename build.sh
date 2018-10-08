#!/bin/sh
echo "Unity ID: khchoksi"

echo "Updating apt-get for latest version of apt-get"
sudo apt-get update

echo "Installing default jdk, maven"
sudo apt-get install default-jdk -y

sudo apt-get install maven -y

echo "Starting up maven spring boot dependecies and starting project"
mvn clean install
mvn spring-boot:run