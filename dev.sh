#!/bin/bash
set -e

ACTION=$1

if [ -z "$ACTION" ]; then
  echo "Usage: $0 [up|down|hard-reset|migrate|rollback]"
  exit 1
fi

load_env() {
  echo "Loading environment variables from .env..."
  export $(grep -v '^#' .env | xargs)
}

case "$ACTION" in
  up)
    load_env

    echo "Starting PostgreSQL via Docker Compose..."
    docker compose up -d

    echo "Swagger UI: http://localhost:8080/swagger-ui.html"
    echo "Starting Spring Boot app with Gradle..."
    ./gradlew bootRun
    ;;

  down)
    echo "Stopping Docker Compose services..."
    docker compose down

    echo "Spring Boot app is stopped (if run via Gradle, stop manually with Ctrl+C)."
    ;;

  hard-reset)
    load_env

    echo "Stopping and removing containers and volumes..."
    docker compose down -v --remove-orphans

    echo "Rebuilding containers from scratch..."
    docker compose up -d --build

    echo "Starting Spring Boot app with Gradle..."
    ./gradlew bootRun
    ;;

  migrate)
    load_env

    echo "Running Liquibase migration (update)..."
    ./gradlew update
    ;;

  rollback)
    load_env

    echo "Rolling back last Liquibase changeset (rollbackCount = 1)..."
    ./gradlew rollbackCount -PliquibaseCommandValue=1
    ;;

  test)
    echo "Running unit & integration tests with coverage…"
    ./gradlew clean test jacocoTestReport
    REPORT="build/reports/jacoco/test/html/index.html"
    echo "✔ Coverage report → file://$PWD/$REPORT"
  ;;

  *)
    echo "Invalid option: $ACTION"
    echo "Usage: $0 [up|down|hard-reset|migrate|rollback]"
    exit 1
    ;;
esac
