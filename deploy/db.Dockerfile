FROM postgres:16@sha256:a3b7f434b2dc57ce85a67e171163eb8ab1a1ebcb39d27484661f26b1dfbe30d6
# The server allows no bind mounts, so the first-start script that creates the app role and the core schema is baked in.
COPY db/postgres-init.sh /docker-entrypoint-initdb.d/10-init.sh
