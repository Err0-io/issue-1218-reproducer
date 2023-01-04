#!/bin/sh

docker-compose stop || true
docker-compose rm -f || true

docker system prune --all --force
docker volume prune --force

rm -f .docker-build

