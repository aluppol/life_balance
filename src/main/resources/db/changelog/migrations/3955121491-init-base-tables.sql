--liquibase formatted sql
--changeset aluppol:22383f48-56f9-4f9d-815a-6a8656e60baa

CREATE TABLE core.Person (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(128) NOT NULL,
    last_name VARCHAR (128) NOT NULL,
    middle_name VARCHAR(256),
    phone_number VARCHAR(20),
    address VARCHAR(256)
);

CREATE TABLE core.Enterprise (
    id SERIAL PRIMARY KEY,
    name VARCHAR (128) NOT NULL,
    description VARCHAR(1024)
);

CREATE TABLE core.Value (
    id SERIAL PRIMARY KEY,
    name VARCHAR (128) NOT NULL,
    description VARCHAR(1024)
);

CREATE TABLE core.Mission (
    id SERIAL PRIMARY KEY,
    text VARCHAR(2048)
);

CREATE TABLE core.Quote (
    id SERIAL PRIMARY KEY,
    person_id  INTEGER NOT NULL,
    text VARCHAR (2048)  NOT NULL,
    author VARCHAR (256),
    CONSTRAINT fk_quote_person FOREIGN KEY (person_id) REFERENCES core.Person(id)
);

CREATE TABLE core.Note (
    id SERIAL PRIMARY KEY,
    person_id  INTEGER NOT NULL,
    title VARCHAR (256) NOT NULL,
    text VARCHAR(2048),
    CONSTRAINT fk_note_person FOREIGN KEY (person_id) REFERENCES core.Person(id)
);

CREATE TABLE core.PersonEnterprise (
    person_id INTEGER NOT NULL,
    enterprise_id INTEGER NOT NULL,
    CONSTRAINT fk_personenterprise_person FOREIGN KEY (person_id) REFERENCES core.Person(id),
    CONSTRAINT fk_personenterprise_enterprise FOREIGN KEY (enterprise_id) REFERENCES core.Enterprise(id)
);

CREATE TABLE core.EnterpriseValue (
    value_id INTEGER NOT NULL,
    enterprise_id INTEGER NOT NULL,
    CONSTRAINT fk_enterprisevalue_value FOREIGN KEY (value_id ) REFERENCES core.Value(id),
    CONSTRAINT  fk_enterprisevalue_enterprise FOREIGN KEY (enterprise_id) REFERENCES core.Enterprise(id)
);

CREATE TABLE core.EnterpriseMission (
    mission_id INTEGER NOT NULL,
    enterprise_id INTEGER NOT NULL,
    CONSTRAINT fk_enterprisemission_mission FOREIGN KEY (mission_id) REFERENCES core.Mission(id),
    CONSTRAINT  fk_enterprisemission_enterprise FOREIGN KEY (enterprise_id ) REFERENCES core.Enterprise(id)
);

CREATE TABLE core.EnterpriseNote (
    enterprise_id INTEGER NOT NULL,
    note_id INTEGER NOT NULL,
    CONSTRAINT fk_enterprisenote_note FOREIGN KEY (note_id ) REFERENCES core.Note(id),
    CONSTRAINT  fk_enterprisenote_enterprise FOREIGN KEY (enterprise_id ) REFERENCES core.Enterprise(id)
);

CREATE TABLE core.PersonMission (
    person_id INTEGER NOT NULL,
    mission_id INTEGER NOT NULL,
    CONSTRAINT fk_personmission_person FOREIGN KEY (person_id  ) REFERENCES core.Person(id),
    CONSTRAINT fk_personmission_mission FOREIGN KEY (mission_id ) REFERENCES core.Mission(id)
);

CREATE TABLE core.PersonValue (
    person_id INTEGER NOT NULL,
    value_id INTEGER NOT NULL,
    CONSTRAINT  fk_personvalue_person FOREIGN KEY (person_id) REFERENCES core.Person(id),
    CONSTRAINT fk_personvalue_value FOREIGN KEY (value_id ) REFERENCES core.Value(id)
);

CREATE TABLE core.ValueNote (
    value_id INTEGER NOT NULL,
    note_id INTEGER NOT NULL,
    CONSTRAINT fk_valuenote_note FOREIGN KEY (note_id ) REFERENCES core.Note(id),
    CONSTRAINT  fk_valuenote_value FOREIGN KEY (value_id ) REFERENCES core.Value(id)
);

CREATE TABLE core.MissionNote (
    mission_id INTEGER NOT NULL,
    note_id INTEGER NOT NULL,
    CONSTRAINT fk_missionnote_note FOREIGN KEY (note_id ) REFERENCES core.Note(id),
    CONSTRAINT  fk_missionnote_mission FOREIGN KEY (mission_id ) REFERENCES core.Mission(id)
);

CREATE TABLE core.QuoteNote (
    quote_id INTEGER NOT NULL,
    note_id INTEGER NOT NULL,
    CONSTRAINT fk_quotenote_note FOREIGN KEY (note_id ) REFERENCES core.Note(id),
    CONSTRAINT  fk_quotenote_quote FOREIGN KEY (quote_id ) REFERENCES core.Quote(id)
);

--rollback DROP TABLE IF EXISTS core.QuoteNote;
--rollback DROP TABLE IF EXISTS core.MissionNote;
--rollback DROP TABLE IF EXISTS core.ValueNote;
--rollback DROP TABLE IF EXISTS core.PersonValue;
--rollback DROP TABLE IF EXISTS core.PersonMission;
--rollback DROP TABLE IF EXISTS core.EnterpriseNote;
--rollback DROP TABLE IF EXISTS core.EnterpriseMission;
--rollback DROP TABLE IF EXISTS core.EnterpriseValue;
--rollback DROP TABLE IF EXISTS core.PersonEnterprise;
--rollback DROP TABLE IF EXISTS core.Note;
--rollback DROP TABLE IF EXISTS core.Quote;
--rollback DROP TABLE IF EXISTS core.Mission;
--rollback DROP TABLE IF EXISTS core.Value;
--rollback DROP TABLE IF EXISTS core.Enterprise;
--rollback DROP TABLE IF EXISTS core.Person;