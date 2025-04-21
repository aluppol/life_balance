#!/bin/bash
set -e

# Required environment variables
required_vars=(
  PG_NAME
  PG_USER
  PG_PASS
)

for v in "${required_vars[@]}"; do
  if [ -z "${!v}" ]; then
    echo "Missing env var: $v"
    exit 1
  fi
done

# Create the application user and grant privileges
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
  CREATE USER $PG_USER WITH PASSWORD '$PG_PASS';
  GRANT CONNECT ON DATABASE $PG_NAME TO $PG_USER;
EOSQL

# Connect to the target database and set up extensions/permissions
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$PG_NAME" <<-EOSQL
  CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

  GRANT USAGE ON SCHEMA public TO $PG_USER;
  GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO $PG_USER;

  ALTER DEFAULT PRIVILEGES IN SCHEMA public
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO $PG_USER;
EOSQL
