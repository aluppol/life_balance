--liquibase formatted sql
--changeset aluppol:743d32c2-bb0f-4908-aedf-b0a36d6aeece

CREATE TABLE core.person (
    id          VARCHAR(255) PRIMARY KEY,
    kind        VARCHAR(16)  NOT NULL CHECK (kind IN ('MEMBER', 'GUEST')),
    enrolled_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE core.mission_statement (
    person_id VARCHAR(255)  PRIMARY KEY REFERENCES core.person (id) ON DELETE CASCADE,
    text      VARCHAR(4000) NOT NULL
);

CREATE TABLE core.core_value (
    id          UUID          PRIMARY KEY,
    person_id   VARCHAR(255)  NOT NULL REFERENCES core.person (id) ON DELETE CASCADE,
    name        VARCHAR(100)  NOT NULL,
    description VARCHAR(1000) NOT NULL,
    position    INTEGER       NOT NULL CHECK (position >= 0),
    CONSTRAINT core_value_of_person UNIQUE (person_id, id)
);
CREATE UNIQUE INDEX core_value_name_per_person ON core.core_value (person_id, lower(name));

CREATE TABLE core.life_role (
    id          UUID          PRIMARY KEY,
    person_id   VARCHAR(255)  NOT NULL REFERENCES core.person (id) ON DELETE CASCADE,
    name        VARCHAR(100)  NOT NULL,
    description VARCHAR(1000) NOT NULL,
    kind        VARCHAR(20)   NOT NULL CHECK (kind IN ('PERSONAL', 'SHARPEN_THE_SAW')),
    position    INTEGER       NOT NULL CHECK (position >= 0),
    CONSTRAINT life_role_of_person UNIQUE (person_id, id)
);
CREATE UNIQUE INDEX life_role_name_per_person ON core.life_role (person_id, lower(name));
CREATE UNIQUE INDEX life_role_one_sharpen_the_saw ON core.life_role (person_id) WHERE kind = 'SHARPEN_THE_SAW';

CREATE TABLE core.goal (
    id           UUID          PRIMARY KEY,
    person_id    VARCHAR(255)  NOT NULL REFERENCES core.person (id) ON DELETE CASCADE,
    life_role_id UUID          NOT NULL,
    title        VARCHAR(200)  NOT NULL,
    description  VARCHAR(2000) NOT NULL,
    due_on       DATE,
    status       VARCHAR(16)   NOT NULL CHECK (status IN ('ACTIVE', 'ACHIEVED', 'DROPPED')),
    created_at   TIMESTAMPTZ   NOT NULL DEFAULT clock_timestamp(),
    CONSTRAINT goal_of_person UNIQUE (person_id, id),
    CONSTRAINT goal_life_role FOREIGN KEY (person_id, life_role_id) REFERENCES core.life_role (person_id, id)
);

CREATE TABLE core.goal_core_value (
    person_id     VARCHAR(255) NOT NULL,
    goal_id       UUID         NOT NULL,
    core_value_id UUID         NOT NULL,
    PRIMARY KEY (goal_id, core_value_id),
    CONSTRAINT goal_core_value_goal FOREIGN KEY (person_id, goal_id)
        REFERENCES core.goal (person_id, id) ON DELETE CASCADE,
    CONSTRAINT goal_core_value_core_value FOREIGN KEY (person_id, core_value_id)
        REFERENCES core.core_value (person_id, id) ON DELETE CASCADE
);

CREATE TABLE core.planned_activity (
    id           UUID         PRIMARY KEY,
    person_id    VARCHAR(255) NOT NULL REFERENCES core.person (id) ON DELETE CASCADE,
    week_start   DATE         NOT NULL CHECK (EXTRACT(ISODOW FROM week_start) = 1),
    life_role_id UUID         NOT NULL,
    goal_id      UUID,
    title        VARCHAR(200) NOT NULL,
    quadrant     VARCHAR(32)  NOT NULL CHECK (quadrant IN
                     ('IMPORTANT_URGENT', 'IMPORTANT_NOT_URGENT', 'NOT_IMPORTANT_URGENT', 'NOT_IMPORTANT_NOT_URGENT')),
    scheduled_on DATE         CHECK (scheduled_on BETWEEN week_start AND week_start + 6),
    is_completed BOOLEAN      NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT clock_timestamp(),
    CONSTRAINT planned_activity_life_role FOREIGN KEY (person_id, life_role_id)
        REFERENCES core.life_role (person_id, id),
    CONSTRAINT planned_activity_goal FOREIGN KEY (person_id, goal_id)
        REFERENCES core.goal (person_id, id) ON DELETE SET NULL (goal_id)
);
CREATE INDEX planned_activity_of_week ON core.planned_activity (person_id, week_start);

CREATE TABLE core.weekly_review (
    person_id       VARCHAR(255)  NOT NULL REFERENCES core.person (id) ON DELETE CASCADE,
    week_start      DATE          NOT NULL CHECK (EXTRACT(ISODOW FROM week_start) = 1),
    accomplishments VARCHAR(4000) NOT NULL,
    lessons         VARCHAR(4000) NOT NULL,
    PRIMARY KEY (person_id, week_start)
);

CREATE TABLE core.weekly_review_renewal (
    person_id  VARCHAR(255) NOT NULL,
    week_start DATE         NOT NULL,
    dimension  VARCHAR(20)  NOT NULL CHECK (dimension IN ('PHYSICAL', 'MENTAL', 'SOCIAL_EMOTIONAL', 'SPIRITUAL')),
    PRIMARY KEY (person_id, week_start, dimension),
    CONSTRAINT weekly_review_renewal_review FOREIGN KEY (person_id, week_start)
        REFERENCES core.weekly_review (person_id, week_start) ON DELETE CASCADE
);

--rollback DROP TABLE core.weekly_review_renewal;
--rollback DROP TABLE core.weekly_review;
--rollback DROP TABLE core.planned_activity;
--rollback DROP TABLE core.goal_core_value;
--rollback DROP TABLE core.goal;
--rollback DROP TABLE core.life_role;
--rollback DROP TABLE core.core_value;
--rollback DROP TABLE core.mission_statement;
--rollback DROP TABLE core.person;
