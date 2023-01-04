#!/bin/sh

docker-compose stop || true

test -e .docker-build || ( ./docker-build.sh && touch .docker-build) && \

docker-compose up

