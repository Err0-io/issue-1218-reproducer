#!/bin/sh

# does nothing; your services go here.

service postgresql start

su - postgres -c psql <<PSQL
ALTER ROLE postgres WITH SUPERUSER LOGIN PASSWORD 'password';

CREATE DATABASE db;
PSQL

su - postgres -c 'psql -v ON_ERROR_STOP=1 < /tmp/schema.sql'

service postgresql stop
