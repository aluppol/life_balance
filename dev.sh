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

auth_backup_schema() {
  local schema="${AUTH_DB_SCHEMA:-kc}"
  local ts
  ts="$(date +%Y%m%d_%H%M%S)"
  mkdir -p backups
  echo "Backing up Auth schema '${schema}' from DB '${PG_NAME}'..."
  set +e
  docker exec -e PGPASSWORD="${PG_ROOT_PASS}" postgres \
    pg_dump -U "${PG_ROOT_USER}" -d "${PG_NAME}" -n "${schema}" -Fc -f "/tmp/kc_${ts}.dump"
  local rc=$?
  set -e
  if [ $rc -ne 0 ]; then
    echo "WARN: Auth schema backup failed or schema missing (continuing with reset)."
    return 0
  fi
  docker cp postgres:/tmp/kc_${ts}.dump "backups/kc_${ts}.dump"
  echo "Backup created: backups/kc_${ts}.dump"
}

kc_restore_schema() {
  local latest
  latest="$(ls -1 backups/kc_*.dump 2>/dev/null | tail -n1 || true)"
  if [ -z "$latest" ]; then
    echo "No Keycloak backup found; starting clean."
    return 0
  fi
  echo "Restoring Keycloak schema from ${latest} ..."
  docker cp "${latest}" postgres:/tmp/kc_restore.dump
  docker exec -e PGPASSWORD="${PG_ROOT_PASS}" postgres bash -lc \
    "pg_restore -U '${PG_ROOT_USER}' -d '${PG_NAME}' --clean --if-exists -n '${AUTH_DB_SCHEMA:-kc}' /tmp/kc_restore.dump"
  echo "Keycloak schema restored."
  # Restart keycloak to ensure it re-reads schema cleanly
  set +e
  docker compose restart keycloak >/dev/null 2>&1
  set -e
}

wait_for_http() {
  local url="$1" ; local max="${2:-40}"
  for i in $(seq 1 "$max"); do
    if curl -fsS "$url" >/dev/null 2>&1; then return 0; fi
    sleep 1
  done
  return 1
}

case "$ACTION" in
  up)
    load_env

    export AUTH_ISSUER="${AUTH_ISSUER:-http://localhost:${AUTH_PORT:-8080}/realms/${AUTH_REALM:-luppol}}"
    export SERVER_PORT="${SERVER_PORT:-${LB_PORT:-3000}}"

    echo "Building images..."
    docker compose build

    echo "Starting PostgreSQL via Docker Compose..."
    docker compose up -d

    echo "Auth Admin:  http://localhost:${AUTH_PORT:-8080}"
    echo "Auth Issuer:       http://localhost:${AUTH_PORT:-8080}/realms/${AUTH_REALM:-luppol}"
    wait_for_http "http://localhost:${AUTH_PORT:-8080}/realms/${AUTH_REALM:-luppol}/.well-known/openid-configuration" || true

    echo "Swagger UI: http://localhost:${APP_PORT}}}/swagger-ui.html"
    echo "Starting Spring Boot app with Gradle..."
    ./gradlew bootRun
    ;;

  down)
    load_env

    echo "Stopping Docker Compose services..."
    docker compose down

    echo "Spring Boot app is stopped (if run via Gradle, stop manually with Ctrl+C)."
    ;;

  hard-reset)
    load_env

    echo "Preparing backup of Auth schema (if present) before reset..."
    auth_backup_schema

    echo "Stopping and removing containers and volumes..."
    docker compose down -v --remove-orphans

    echo "Rebuilding containers from scratch..."
    docker compose build --no-cache
    docker compose up -d

    echo "Attempting to restore Auth schema (if a backup exists)..."
    kc_restore_schema

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
