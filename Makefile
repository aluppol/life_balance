.PHONY: dev stop generate-changelog

# Shell fragment to load .env
ENV_LOAD := set -a && source .env && set +a

dev:
	@bash -c '$(ENV_LOAD) && docker compose up -d && ./gradlew bootRun'

stop:
	docker compose down
	pkill -f 'bootRun' || true

generate-changelog:
	@bash -c '$(ENV_LOAD) && \
	liquibase \
	  --url="jdbc:postgresql://$${PG_HOST}:$${PG_PORT}/$${PG_NAME}" \
	  --username="$${PG_USER}" \
	  --password="$${PG_PASS}" \
	  --driver=org.postgresql.Driver \
	  --classpath=./build/libs/life_balance-0.0.1-SNAPSHOT.jar \
	  --changeLogFile=src/main/resources/db/changelog/db.changelog-master.xml \
	  generateChangeLog'
