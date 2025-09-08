#!/bin/bash
set -e

ACTION=$1

if [ -z "$ACTION" ]; then
  echo "Usage: $0 [up|down|hard-reset|migrate|rollback|auth-reconcile|auth-backup|auth-restore]"
  exit 1
fi

load_env() {
  echo "Loading environment variables from .env..."
  export $(grep -v '^#' .env | xargs)

  if [ -z "${AUTH_BASE_URL:-}" ]; then
    export AUTH_BASE_URL="http://localhost:${AUTH_PORT:-8080}"
  fi
}

auth_backup_schema() {
  local schema="${KC_DB_SCHEMA:-auth}"
  local ts
  ts="$(date +%Y%m%d_%H%M%S)"
  mkdir -p backups

  echo "Backing up Keycloak schema '${schema}' from DB '${PG_NAME}'..."
  set +e
  docker compose exec -T -e PGPASSWORD="${PG_ROOT_PASS}" postgres \
    pg_dump -U "${PG_ROOT_USER}" -d "${PG_NAME}" -n "${schema}" -Fc -f "/tmp/auth_${ts}.dump"
  local rc=$?
  set -e

  if [ $rc -ne 0 ]; then
    echo "WARN: Backup failed or schema '${schema}' missing (continuing)."
    return 0
  fi
  docker compose cp postgres:/tmp/auth_${ts}.dump "backups/auth_${ts}.dump"
  echo "✔ Backup created: backups/auth_${ts}.dump"
}

auth_restore_schema() {
  local schema="${KC_DB_SCHEMA:-auth}"
  local latest
  latest="$(ls -1 backups/auth_*.dump 2>/dev/null | tail -n1 || true)"
  if [ -z "$latest" ]; then
    echo "No Keycloak backup found; starting clean."
    return 0
  fi

  echo "Restoring Keycloak schema '${schema}' from ${latest} ..."
  docker compose cp "${latest}" postgres:/tmp/auth_restore.dump
  docker compose exec -T -e PGPASSWORD="${PG_ROOT_PASS}" postgres bash -lc \
    "pg_restore -U '${PG_ROOT_USER}' -d '${PG_NAME}' --clean --if-exists -n '${schema}' /tmp/auth_restore.dump"
  echo "✔ Keycloak schema restored."

  # Restart KC to ensure it re-reads schema cleanly (ignore if service name differs)
  set +e
  docker compose restart auth >/dev/null 2>&1 || true
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

auth_apply_config() {
  echo "Applying Auth config-as-code (keycloak-config-cli)..."
  # Run the reconciler once and remove the container after it exits
  docker compose run --rm auth-config-cli
  echo "✔ Auth config applied."
}

case "$ACTION" in
  up)
    load_env

    export AUTH_ISSUER="${AUTH_ISSUER:-http://localhost:${AUTH_PORT:-8080}/realms/${AUTH_REALM:-luppol}}"
    export SERVER_PORT="${SERVER_PORT:-${LB_PORT:-3000}}"

    echo "Building images..."
    docker compose build

    echo "Starting DB (wait for health)..."
    docker compose up -d --wait postgres

    echo "Starting Auth (gated on DB health)..."
    docker compose up -d --wait auth

    echo "Auth Admin:  http://localhost:${AUTH_PORT:-8080}"
    echo "Auth Issuer: http://localhost:${AUTH_PORT:-8080}/realms/${AUTH_REALM:-luppol}"
    wait_for_http "http://localhost:${AUTH_PORT:-8080}/realms/${AUTH_REALM:-luppol}/.well-known/openid-configuration" || true

    auth_apply_config

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

    echo "Stopping and removing containers/volumes..."
    docker compose down -v --remove-orphans

    echo "Rebuilding from scratch..."
    docker compose build --no-cache

    echo "Starting DB and Auth..."
    docker compose up -d --wait postgres
    docker compose up -d --wait auth

    # No automatic restore here (clean reset). If you want to restore, run: ./dev.sh auth-restore
    echo "Reapplying Auth config..."
    auth_apply_config

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

   auth-reconcile)
      load_env
      # Ensure services are up; rely on reconciler's availability check afterward
      docker compose up -d --wait postgres auth
      auth_apply_config
      ;;

    auth-backup)
      load_env
      docker compose up -d --wait postgres
      auth_backup_schema
      ;;

    auth-restore)
      load_env
      docker compose up -d --wait postgres auth
      auth_restore_schema
      # After restore, reconcile to ensure config drift is fixed
      auth_apply_config
      ;;

  test)
    echo "Running unit & integration tests with coverage…"
    ./gradlew clean test jacocoTestReport
    REPORT="build/reports/jacoco/test/html/index.html"
    echo "✔ Coverage report → file://$PWD/$REPORT"
  ;;

  *)
    echo "Invalid option: $ACTION"
    echo "Usage: $0 [up|down|hard-reset|migrate|rollback|auth-reconcile|auth-backup|auth-restore|test]"
    exit 1
    ;;
esac
