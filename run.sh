#!/bin/bash
echo "======================================"
echo "!!!!!!!! REQUIREMENTS !!!!!!!!!!!"
echo "Your system need to have Java 8, Maven 3, Docker and Docker compose installed before being able to run the mind-map-api."
echo "======================================"

echo "======================================"
echo "packaging mind-map-api ..."
echo "======================================"

mvn clean package || exit_with_error "error compiling mind-map-api"

echo "packaging completed with success"

echo "======================================"
echo "starting mind-map-api using docker-compose"
echo "======================================"
docker-compose -f docker-compose.yml up -d || exit_with_error "error starting mind-map-api"
counter=0
echo "waiting for mind-map-api to be up"

until curl --output /dev/null --silent --fail http://localhost:8888/actuator/health;
do
  sleep 1;
  if [ $counter -gt 60 ]; then
    echo "mind-map-api was never up..!"
    docker-compose -f docker-compose.yml down
    exit 1;
  fi
  counter=$((counter+1))
  echo "waiting for mind-map-api to be up, $counter..."
done
echo "mind-map-api is up ...!"
