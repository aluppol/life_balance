#!/bin/bash
set -euo pipefail

for name in POSTGRES_USER POSTGRES_DB PG_USER PG_PASS; do
  if [[ -z "${!name:-}" ]]; then
    echo "Missing environment variable: $name" >&2
    exit 1
  fi
done

psql --set ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" \
  --set app_user="$PG_USER" --set app_password="$PG_PASS" --set database="$POSTGRES_DB" <<'SQL'
SELECT format('CREATE ROLE %I LOGIN PASSWORD %L', :'app_user', :'app_password')
WHERE NOT EXISTS (SELECT FROM pg_roles WHERE rolname = :'app_user') \gexec
SELECT format('GRANT CONNECT ON DATABASE %I TO %I', :'database', :'app_user') \gexec
SELECT format('GRANT USAGE, CREATE ON SCHEMA public TO %I', :'app_user') \gexec
SELECT format('CREATE SCHEMA IF NOT EXISTS core AUTHORIZATION %I', :'app_user') \gexec
SQL
