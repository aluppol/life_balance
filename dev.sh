#!/usr/bin/env bash
set -euo pipefail

readonly REQUIRED_KEYS=(PG_HOST PG_PORT PG_NAME PG_ROOT_USER PG_ROOT_PASS PG_USER PG_PASS)
readonly LOCAL_ISSUER="http://localhost:8090/realms/dev"

usage() {
  echo "Usage: $0 {db|backend|frontend|demo-reset|test|down|hard-reset}"
  echo "  db          start the local Postgres"
  echo "  backend     start Postgres and the API on :8080, trusting the local dev identity server"
  echo "  frontend    start the dev identity server on :8090 and the Vite dev server on :5173"
  echo "  demo-reset  wipe and refill every guest workspace in the local database"
  echo "  test        full build: tests, coverage and mutation gates, frontend checks"
  echo "  down        stop the local containers (data kept)"
  echo "  hard-reset  stop the local containers and DELETE the local database"
}

load_env() {
  if [[ ! -f .env ]]; then
    echo "Missing .env: copy .env.example to .env and fill it in"
    exit 1
  fi
  set -a
  source .env
  set +a
  require_keys
}

require_keys() {
  local missing=()
  for key in "${REQUIRED_KEYS[@]}"; do
    [[ -n "${!key:-}" ]] || missing+=("$key")
  done
  if (( ${#missing[@]} > 0 )); then
    echo "Missing keys in .env: ${missing[*]}"
    exit 1
  fi
}

start_database() {
  docker compose up --detach --wait postgres
}

run_backend() {
  AUTH_ISSUER="$LOCAL_ISSUER" \
  AUTH_JWK_SET_URI="$LOCAL_ISSUER/protocol/openid-connect/certs" \
    ./gradlew :bootstrap:bootRun "$@"
}

run_frontend() {
  cd frontend
  source "$HOME/.nvm/nvm.sh"
  nvm use
  npm ci
  trap 'kill 0' EXIT
  npm run dev:identity &
  npm run dev
}

case "${1:-}" in
  db) load_env; start_database ;;
  backend) load_env; start_database; run_backend ;;
  frontend) run_frontend ;;
  demo-reset) load_env; start_database; run_backend --args=demo-reset ;;
  test) ./gradlew clean build ;;
  down) docker compose down ;;
  hard-reset) docker compose down --volumes --remove-orphans ;;
  *) usage; exit 1 ;;
esac
