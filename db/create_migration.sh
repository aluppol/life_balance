#!/usr/bin/env bash
set -euo pipefail

readonly NAME="${1:?Usage: $0 <migration-name>, e.g. $0 add-journal-table}"
readonly RESOURCES="adapters/persistence/src/main/resources"
readonly MASTER_FILE="$RESOURCES/db/changelog/db.changelog-master.xml"

changeset_id=$(uuidgen | tr '[:upper:]' '[:lower:]')
seconds_since_1900=$(( $(date -u +%s) + 2208988800 ))
relative_path="db/changelog/migrations/${seconds_since_1900}-${NAME}.sql"
include_line="    <include file=\"$relative_path\"/>"

cat > "$RESOURCES/$relative_path" <<SQL
--liquibase formatted sql
--changeset aluppol:${changeset_id}

--rollback
SQL

if ! grep -Fxq "$include_line" "$MASTER_FILE"; then
  awk -v line="$include_line" '/<\/databaseChangeLog>/ { print line } { print }' "$MASTER_FILE" > "$MASTER_FILE.tmp"
  mv "$MASTER_FILE.tmp" "$MASTER_FILE"
fi

echo "Created $RESOURCES/$relative_path (changeset aluppol:${changeset_id})"
