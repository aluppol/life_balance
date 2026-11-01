--liquibase formatted sql
--changeset aluppol:5d92d75f-6d6d-4251-935c-35d25e9ff22a

ALTER TABLE core.person ADD COLUMN last_active_at TIMESTAMPTZ;
UPDATE core.person SET last_active_at = enrolled_at;
ALTER TABLE core.person ALTER COLUMN last_active_at SET NOT NULL;
ALTER TABLE core.person ALTER COLUMN last_active_at SET DEFAULT clock_timestamp();

--rollback ALTER TABLE core.person DROP COLUMN last_active_at;
