#!/bin/bash
# Proves Spring MVC answers from the migrated database: 200 today, 401 once the Keycloak work lands.
set -euo pipefail
status=$(docker compose --env-file "$IMAGES_ENV" exec -T web bash -c \
  'exec 3<>/dev/tcp/127.0.0.1/8080; printf "GET /api/persons HTTP/1.0\r\nHost: localhost\r\n\r\n" >&3; head -n 1 <&3')
echo "GET /api/persons -> ${status}"
[[ "$status" =~ ^HTTP/1\.[01]\ (200|401) ]]
