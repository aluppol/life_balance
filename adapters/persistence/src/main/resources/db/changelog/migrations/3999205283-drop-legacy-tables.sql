--liquibase formatted sql
--changeset aluppol:db6f55f0-4226-436d-872e-5bdf67e46803

DROP TABLE core.quotenote;
DROP TABLE core.missionnote;
DROP TABLE core.valuenote;
DROP TABLE core.personvalue;
DROP TABLE core.enterprisenote;
DROP TABLE core.enterprisevalue;
DROP TABLE core.personenterprise;
DROP TABLE core.note;
DROP TABLE core.quote;
DROP TABLE core.value;
DROP TABLE core.enterprise;
DROP TABLE core.person;
DROP TABLE core.mission;

--rollback CREATE TABLE core.mission (id SERIAL PRIMARY KEY, text VARCHAR(2048) NOT NULL);
--rollback CREATE TABLE core.person (id SERIAL PRIMARY KEY, first_name VARCHAR(128) NOT NULL, last_name VARCHAR(128) NOT NULL, middle_name VARCHAR(256), phone_number VARCHAR(20) UNIQUE, address VARCHAR(256), mission_id INTEGER UNIQUE, CONSTRAINT fk_person_mission FOREIGN KEY (mission_id) REFERENCES core.mission (id) ON UPDATE CASCADE ON DELETE SET NULL);
--rollback CREATE TABLE core.enterprise (id SERIAL PRIMARY KEY, name VARCHAR(128) NOT NULL, description VARCHAR(1024), mission_id INTEGER UNIQUE, CONSTRAINT fk_enterprise_mission FOREIGN KEY (mission_id) REFERENCES core.mission (id) ON UPDATE CASCADE ON DELETE SET NULL);
--rollback CREATE TABLE core.value (id SERIAL PRIMARY KEY, name VARCHAR(128) NOT NULL, description VARCHAR(1024));
--rollback CREATE TABLE core.quote (id SERIAL PRIMARY KEY, person_id INTEGER NOT NULL, text VARCHAR(2048) NOT NULL, author VARCHAR(256), CONSTRAINT fk_quote_person FOREIGN KEY (person_id) REFERENCES core.person (id) ON UPDATE CASCADE ON DELETE CASCADE);
--rollback CREATE TABLE core.note (id SERIAL PRIMARY KEY, person_id INTEGER NOT NULL, title VARCHAR(256) NOT NULL, text VARCHAR(2048), CONSTRAINT fk_note_person FOREIGN KEY (person_id) REFERENCES core.person (id) ON UPDATE CASCADE ON DELETE CASCADE);
--rollback CREATE TABLE core.personenterprise (person_id INTEGER NOT NULL, enterprise_id INTEGER NOT NULL, CONSTRAINT fk_personenterprise_person FOREIGN KEY (person_id) REFERENCES core.person (id) ON UPDATE CASCADE ON DELETE CASCADE, CONSTRAINT fk_personenterprise_enterprise FOREIGN KEY (enterprise_id) REFERENCES core.enterprise (id) ON UPDATE CASCADE ON DELETE CASCADE);
--rollback CREATE TABLE core.enterprisevalue (value_id INTEGER NOT NULL, enterprise_id INTEGER NOT NULL, CONSTRAINT fk_enterprisevalue_value FOREIGN KEY (value_id) REFERENCES core.value (id) ON UPDATE CASCADE ON DELETE CASCADE, CONSTRAINT fk_enterprisevalue_enterprise FOREIGN KEY (enterprise_id) REFERENCES core.enterprise (id) ON UPDATE CASCADE ON DELETE CASCADE);
--rollback CREATE TABLE core.enterprisenote (enterprise_id INTEGER NOT NULL, note_id INTEGER NOT NULL, CONSTRAINT fk_enterprisenote_note FOREIGN KEY (note_id) REFERENCES core.note (id) ON UPDATE CASCADE ON DELETE CASCADE, CONSTRAINT fk_enterprisenote_enterprise FOREIGN KEY (enterprise_id) REFERENCES core.enterprise (id) ON UPDATE CASCADE ON DELETE CASCADE);
--rollback CREATE TABLE core.personvalue (person_id INTEGER NOT NULL, value_id INTEGER NOT NULL, CONSTRAINT fk_personvalue_person FOREIGN KEY (person_id) REFERENCES core.person (id) ON UPDATE CASCADE ON DELETE CASCADE, CONSTRAINT fk_personvalue_value FOREIGN KEY (value_id) REFERENCES core.value (id) ON UPDATE CASCADE ON DELETE CASCADE);
--rollback CREATE TABLE core.valuenote (value_id INTEGER NOT NULL, note_id INTEGER NOT NULL, CONSTRAINT fk_valuenote_note FOREIGN KEY (note_id) REFERENCES core.note (id) ON UPDATE CASCADE ON DELETE CASCADE, CONSTRAINT fk_valuenote_value FOREIGN KEY (value_id) REFERENCES core.value (id) ON UPDATE CASCADE ON DELETE CASCADE);
--rollback CREATE TABLE core.missionnote (mission_id INTEGER NOT NULL, note_id INTEGER NOT NULL, CONSTRAINT fk_missionnote_note FOREIGN KEY (note_id) REFERENCES core.note (id) ON UPDATE CASCADE ON DELETE CASCADE, CONSTRAINT fk_missionnote_mission FOREIGN KEY (mission_id) REFERENCES core.mission (id) ON UPDATE CASCADE ON DELETE CASCADE);
--rollback CREATE TABLE core.quotenote (quote_id INTEGER NOT NULL, note_id INTEGER NOT NULL, CONSTRAINT fk_quotenote_note FOREIGN KEY (note_id) REFERENCES core.note (id) ON UPDATE CASCADE ON DELETE CASCADE, CONSTRAINT fk_quotenote_quote FOREIGN KEY (quote_id) REFERENCES core.quote (id) ON UPDATE CASCADE ON DELETE CASCADE);
