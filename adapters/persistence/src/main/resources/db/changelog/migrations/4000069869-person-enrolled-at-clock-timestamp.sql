--liquibase formatted sql
--changeset aluppol:79a6439f-82c6-4199-ba63-40103eb52cc5

ALTER TABLE core.person ALTER COLUMN enrolled_at SET DEFAULT clock_timestamp();

--rollback ALTER TABLE core.person ALTER COLUMN enrolled_at SET DEFAULT now();
