#!/bin/bash
set -e

docker stop stores_testing_db || true
docker rm stores_testing_db || true
docker run --name stores_testing_db \
  -e POSTGRES_USER=kotlin_app_test \
  -e POSTGRES_PASSWORD=abcde123_test \
  -e POSTGRES_DB=stores_db_test \
  -p 5430:5432 \
  -d postgres

./gradlew clean test
