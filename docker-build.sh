#!/bin/sh

docker build --no-cache docker/database --tag  err0_io:issue_1218 && \
echo "Build OK"
