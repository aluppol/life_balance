#!/bin/bash
set -e

# Required environment variables
required_vars=(
  POSTGRES_USER
  POSTGRES_DB
  PG_USER
  PG_PASS
)

for v in "${required_vars[@]}"; do
  if [ -z "${!v}" ]; then
    echo "Missing env var: $v"
    exit 1
  fi
done

# Create application user
psql -h localhost -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
  DO \$\$
  BEGIN
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = '$PG_USER') THEN
      CREATE USER $PG_USER WITH PASSWORD '$PG_PASS';
    END IF;
  END
  \$\$;
  GRANT CONNECT ON DATABASE $POSTGRES_DB TO $PG_USER;
EOSQL

# Grant schema and object privileges
psql -h localhost -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
  CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

  GRANT USAGE, CREATE ON SCHEMA public TO $PG_USER;
  GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO $PG_USER;

  ALTER DEFAULT PRIVILEGES IN SCHEMA public
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO $PG_USER;

  CREATE SCHEMA IF NOT EXISTS core AUTHORIZATION $PG_USER;

  GRANT USAGE, CREATE ON SCHEMA core TO $PG_USER;
  GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA core TO $PG_USER;

  ALTER DEFAULT PRIVILEGES IN SCHEMA core
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO $PG_USER;

EOSQL
