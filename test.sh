#!/bin/bash
set -e

docker run --name stores_testing_db \
   -d postgres \
   -e POSTGRES_USER=kotlin_app_test \
   -e POSTGRES_PASSWORD=abcde123_test \
   -e POSTGRES_DB=stores_db_test \
   -p 5430:5432 \

./gradlew clean test
