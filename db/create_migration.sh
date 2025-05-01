#!/bin/bash

# Usage: ./create_migration.sh add-person-table
NAME=$1
UUID=$(uuidgen)
NOW_UTC=$(date -u +%s)
SECONDS_SINCE_1900=$((NOW_UTC + 2208988800))
FILE_REL="db/changelog/migrations/${SECONDS_SINCE_1900}-${NAME}.sql"
FILE_PATH="src/main/resources/${FILE_REL}"
MASTER_FILE="src/main/resources/db/changelog/db.changelog-master.xml"
INCLUDE_LINE="    <include file=\"$FILE_REL\"/>"

mkdir -p src/main/resources/db/changelog/migrations

cat <<EOF > "$FILE_PATH"
--liquibase formatted sql
--changeset aluppol:${UUID}

-- SQL goes here

--rollback
-- rollback goes here
EOF

echo "Created migration: $FILE_PATH"
echo "Changeset UUID: $UUID"

# Add <include> line to master changelog (cross-platform)
if ! grep -Fxq "$INCLUDE_LINE" "$MASTER_FILE"; then
    if [[ "$OSTYPE" == "darwin"* ]]; then
        # macOS (BSD sed)
        sed -i '' "/<\/databaseChangeLog>/i\\
$INCLUDE_LINE
" "$MASTER_FILE"
    else
        # Linux (GNU sed)
        sed -i "/<\/databaseChangeLog>/i $INCLUDE_LINE" "$MASTER_FILE"
    fi
    echo "Appended to master changelog: $MASTER_FILE"
else
    echo "Include already exists in master changelog."
fi
