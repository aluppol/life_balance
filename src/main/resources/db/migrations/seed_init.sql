-- up

-- Insert data into Person (10 rows)
INSERT INTO Person (uuid, first_name, last_name, middle_name, phone_number, address) VALUES
('uuid_placeholder', 'John', 'Doe', 'Michael', 1234567890, '123 Main St'),
('uuid_placeholder', 'Jane', 'Smith', 'Elizabeth', 2345678901, '456 Oak St'),
('uuid_placeholder', 'Alice', 'Johnson', 'Marie', 3456789012, '789 Pine St'),
('uuid_placeholder', 'Bob', 'Brown', 'Alexander', 4567890123, '101 Maple St'),
('uuid_placeholder', 'Charlie', 'Davis', 'Nathan', 5678901234, '202 Birch St'),
('uuid_placeholder', 'Diana', 'Wilson', 'Claire', 6789012345, '303 Cedar St'),
('uuid_placeholder', 'Ethan', 'Moore', 'James', 7890123456, '404 Walnut St'),
('uuid_placeholder', 'Fiona', 'Taylor', 'Rose', 8901234567, '505 Cherry St'),
('uuid_placeholder', 'George', 'Anderson', 'Robert', 9012345678, '606 Spruce St'),
('uuid_placeholder', 'Hannah', 'Martinez', 'Grace', 1230984567, '707 Elm St');

-- Insert data into Enterprise (5 rows)
INSERT INTO Enterprise (name, description) VALUES
('Tech Corp', 'A leading technology company'),
('Health Solutions', 'Providing innovative healthcare services'),
('Eco Energy', 'Developing renewable energy solutions'),
('Global Finance', 'International financial services provider'),
('EduWorld', 'Advancing education through technology');

-- Insert data into Value (5 rows)
INSERT INTO Value (name, description) VALUES
('Integrity', 'Commitment to ethical principles'),
('Innovation', 'Pushing the boundaries of technology'),
('Sustainability', 'Ensuring long-term environmental health'),
('Collaboration', 'Working together for success'),
('Customer Focus', 'Putting customers first');

-- Insert data into Mission (5 rows)
INSERT INTO Mission (text) VALUES
('To revolutionize technology for a better world.'),
('To provide quality healthcare for all.'),
('To create sustainable energy solutions.'),
('To empower individuals through financial literacy.'),
('To make education accessible worldwide.');

-- Insert data into Quote (5 rows)
INSERT INTO Quote (person_id, text, author) VALUES
(1, 'Innovation distinguishes between a leader and a follower.', 'Steve Jobs'),
(2, 'The best way to predict the future is to create it.', 'Peter Drucker'),
(3, 'Success is not the key to happiness. Happiness is the key to success.', 'Albert Schweitzer'),
(4, 'Do what you can, with what you have, where you are.', 'Theodore Roosevelt'),
(5, 'Believe you can and you’re halfway there.', 'Theodore Roosevelt');

-- Insert data into Note (5 rows)
INSERT INTO Note (person_id, title, text) VALUES
(1, 'Meeting Notes', 'Discussed project milestones and deadlines.'),
(2, 'Research Insights', 'Found key trends in the industry.'),
(3, 'Personal Goals', 'Focus on continuous learning and improvement.'),
(4, 'Strategy Plan', 'Outlining key initiatives for growth.'),
(5, 'Customer Feedback', 'Summarizing key pain points and solutions.');

-- Insert data into PersonEnterprise (5 rows)
INSERT INTO PersonEnterprise (person_id, enterprise_id) VALUES
(1, 1), (2, 2), (3, 3), (4, 4), (5, 5);

-- Insert data into EnterpriseValue (5 rows)
INSERT INTO EnterpriseValue (value_id, enterprise_id) VALUES
(1, 1), (2, 2), (3, 3), (4, 4), (5, 5);

-- Insert data into EnterpriseMission (5 rows)
INSERT INTO EnterpriseMission (mission_id, enterprise_id) VALUES
(1, 1), (2, 2), (3, 3), (4, 4), (5, 5);

-- Insert data into EnterpriseNote (5 rows)
INSERT INTO EnterpriseNote (enterprise_id, note_id) VALUES
(1, 1), (2, 2), (3, 3), (4, 4), (5, 5);

-- Insert data into PersonMission (5 rows)
INSERT INTO PersonMission (person_id, mission_id) VALUES
(1, 1), (2, 2), (3, 3), (4, 4), (5, 5);

-- Insert data into PersonValue (5 rows)
INSERT INTO PersonValue (person_id, value_id) VALUES
(1, 1), (2, 2), (3, 3), (4, 4), (5, 5);

-- Insert data into ValueNote (5 rows)
INSERT INTO ValueNote (value_id, note_id) VALUES
(1, 1), (2, 2), (3, 3), (4, 4), (5, 5);

-- Insert data into MissionNote (5 rows)
INSERT INTO MissionNote (mission_id, note_id) VALUES
(1, 1), (2, 2), (3, 3), (4, 4), (5, 5);

-- Insert data into QuoteNote (5 rows)
INSERT INTO QuoteNote (quote_id, note_id) VALUES
(1, 1), (2, 2), (3, 3), (4, 4), (5, 5);


-- down

SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM QuoteNote;
DELETE FROM MissionNote;
DELETE FROM ValueNote;
DELETE FROM PersonValue;
DELETE FROM PersonMission;
DELETE FROM EnterpriseNote;
DELETE FROM EnterpriseMission;
DELETE FROM EnterpriseValue;
DELETE FROM PersonEnterprise;
DELETE FROM Note;
DELETE FROM Quote;
DELETE FROM Mission;
DELETE FROM `Value`;
DELETE FROM Enterprise;
DELETE FROM Person;

SET FOREIGN_KEY_CHECKS = 1;
