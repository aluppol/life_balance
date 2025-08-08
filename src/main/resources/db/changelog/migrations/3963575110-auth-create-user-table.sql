--liquibase formatted sql
--changeset aluppol:0848dddd-8425-4c8e-998c-123122a3ed9a

CREATE TABLE auth."User" (
    id        BIGSERIAL PRIMARY KEY,
    username     VARCHAR(256) NOT NULL UNIQUE,
    password  VARCHAR(256) NOT NULL,
    email     VARCHAR(256) NOT NULL UNIQUE
);

CREATE INDEX user_username ON auth."User" (username);


--rollback DROP INDEX IF EXISTS auth.user_username;
--rollback DROP TABLE IF EXISTS auth."User";

