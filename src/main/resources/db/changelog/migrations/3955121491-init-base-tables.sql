--liquibase formatted sql
--changeset aluppol:22383f48-56f9-4f9d-815a-6a8656e60baa

CREATE TABLE Person (
    id INTEGER UNSIGNED  PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(128) NOT NULL,
    last_name VARCHAR (128) NOT NULL,
    middle_name VARCHAR(256),
    phone_number BIGINT UNSIGNED,
    address VARCHAR(256)
);

CREATE TABLE Enterprise (
    id INTEGER UNSIGNED  PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR (128) NOT NULL,
    description VARCHAR(1024)
);

CREATE TABLE Value (
    id INTEGER UNSIGNED  PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR (128) NOT NULL,
    description VARCHAR(1024)
);

CREATE TABLE Mission (
    id INTEGER UNSIGNED  PRIMARY KEY AUTO_INCREMENT,
    text VARCHAR(2048)
);

CREATE TABLE Quote (
    id INTEGER UNSIGNED  PRIMARY KEY AUTO_INCREMENT,
    person_id  INTEGER UNSIGNED NOT NULL,
    text VARCHAR (2048)  NOT NULL,
    author VARCHAR (256),
    CONSTRAINT fk_quote_person FOREIGN KEY (person_id) REFERENCES Person(id)
);

CREATE TABLE Note (
    id INTEGER UNSIGNED  PRIMARY KEY AUTO_INCREMENT,
    person_id  INTEGER UNSIGNED NOT NULL,
    title VARCHAR (256) NOT NULL,
    text VARCHAR(2048),
    CONSTRAINT fk_note_person FOREIGN KEY (person_id) REFERENCES Person(id)
);

CREATE TABLE PersonEnterprise (
    person_id INTEGER UNSIGNED NOT NULL,
    enterprise_id INTEGER UNSIGNED NOT NULL,
    CONSTRAINT fk_personenterprise_person FOREIGN KEY (person_id) REFERENCES Person(id),
    CONSTRAINT fk_personenterprise_enterprise FOREIGN KEY (enterprise_id) REFERENCES Enterprise(id)
);

CREATE TABLE EnterpriseValue (
    value_id INTEGER UNSIGNED NOT NULL,
    enterprise_id INTEGER UNSIGNED NOT NULL,
    CONSTRAINT fk_enterprisevalue_value FOREIGN KEY (value_id ) REFERENCES Value(id),
    CONSTRAINT  fk_enterprisevalue_enterprise FOREIGN KEY (enterprise_id) REFERENCES Enterprise(id)
);

CREATE TABLE EnterpriseMission (
    mission_id INTEGER UNSIGNED NOT NULL,
    enterprise_id INTEGER UNSIGNED NOT NULL,
    CONSTRAINT fk_enterprisemission_mission FOREIGN KEY (mission_id) REFERENCES Mission(id),
    CONSTRAINT  fk_enterprisemission_enterprise FOREIGN KEY (enterprise_id ) REFERENCES Enterprise(id)
);

CREATE TABLE EnterpriseNote (
    enterprise_id INTEGER UNSIGNED NOT NULL,
    note_id INTEGER UNSIGNED NOT NULL,
    CONSTRAINT fk_enterprisenote_note FOREIGN KEY (note_id ) REFERENCES Note(id),
    CONSTRAINT  fk_enterprisenote_enterprise FOREIGN KEY (enterprise_id ) REFERENCES Enterprise(id)
);

CREATE TABLE PersonMission (
    person_id INTEGER UNSIGNED NOT NULL,
    mission_id INTEGER UNSIGNED NOT NULL,
    CONSTRAINT fk_personmission_person FOREIGN KEY (person_id  ) REFERENCES Person(id),
    CONSTRAINT fk_personmission_mission FOREIGN KEY (mission_id ) REFERENCES Mission(id)
);

CREATE TABLE PersonValue (
    person_id INTEGER UNSIGNED NOT NULL,
    value_id INTEGER UNSIGNED NOT NULL,
    CONSTRAINT  fk_personvalue_person FOREIGN KEY (person_id) REFERENCES Person(id),
    CONSTRAINT fk_personvalue_value FOREIGN KEY (value_id ) REFERENCES Value(id)
);

CREATE TABLE ValueNote (
    value_id INTEGER UNSIGNED NOT NULL,
    note_id INTEGER UNSIGNED NOT NULL,
    CONSTRAINT fk_valuenote_note FOREIGN KEY (note_id ) REFERENCES Note(id),
    CONSTRAINT  fk_valuenote_value FOREIGN KEY (value_id ) REFERENCES Value(id)
);

CREATE TABLE MissionNote (
    mission_id INTEGER UNSIGNED NOT NULL,
    note_id INTEGER UNSIGNED NOT NULL,
    CONSTRAINT fk_missionnote_note FOREIGN KEY (note_id ) REFERENCES Note(id),
    CONSTRAINT  fk_missionnote_mission FOREIGN KEY (mission_id ) REFERENCES Mission(id)
);

CREATE TABLE QuoteNote (
    quote_id INTEGER UNSIGNED NOT NULL,
    note_id INTEGER UNSIGNED NOT NULL,
    CONSTRAINT fk_quotenote_note FOREIGN KEY (note_id ) REFERENCES Note(id),
    CONSTRAINT  fk_quotenote_quote FOREIGN KEY (quote_id ) REFERENCES Mission(id)
);

--rollback
DROP TABLE IF EXISTS
    QuoteNote,
    MissionNote,
    ValueNote,
    PersonValue,
    PersonMission,
    EnterpriseNote,
    EnterpriseMission,
    EnterpriseValue,
    PersonEnterprise,
    Note,
    Quote,
    Mission,
    `Value`,
    Enterprise,
    Person;
