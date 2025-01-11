-- Example SQL Insert Statements for `members` table

-- Members
INSERT INTO members (name, surname, dateOfBirth, homeAddress, city, zipCode, email, phoneNumber, status, pending) VALUES ('John', 'Doe', '1985-06-15', '123 Main St', 'Springfield', '1234', 'john.doe@example.com', '+1234567890', TRUE, FALSE);
INSERT INTO members (name, surname, dateOfBirth, homeAddress, city, zipCode, email, phoneNumber, status, pending) VALUES ('Jane', 'Smith', '1990-02-20', '456 Oak St', 'Shelbyville', '6789', 'jane.smith@example.com', '+0987654321', TRUE, FALSE);
INSERT INTO members (name, surname, dateOfBirth, homeAddress, city, zipCode, email, phoneNumber, status, pending) VALUES ('Alice', 'Johnson', '1978-11-03', '789 Maple Ave', 'Ogdenville', '5432', 'alice.johnson@example.com', '+1122334455', TRUE, FALSE);
INSERT INTO members (name, surname, dateOfBirth, homeAddress, city, zipCode, email, phoneNumber, status, pending) VALUES ('Bob', 'Brown', '1982-08-12', '321 Pine St', 'North Haverbrook', '9876', 'bob.brown@example.com', '+6677889900', TRUE, FALSE);

-- Pending
INSERT INTO members (name, surname, dateOfBirth, homeAddress, city, zipCode, email, phoneNumber, status, pending) VALUES ('Alice', 'Johnson', '1990-01-15', '456 Elm St', 'Springfield', '5432', 'alice.johnson@example.com', '+1122334455', FALSE, TRUE);
INSERT INTO members (name, surname, dateOfBirth, homeAddress, city, zipCode, email, phoneNumber, status, pending) VALUES ('Charlie', 'Smith', '1975-11-20', '789 Oak St', 'Shelbyville', '8765', 'charlie.smith@example.com', '+9988776655', FALSE, TRUE);
INSERT INTO members (name, surname, dateOfBirth, homeAddress, city, zipCode, email, phoneNumber, status, pending) VALUES ('Diana', 'Lee', '1988-03-10', '123 Maple St', 'Capital City', '6543', 'diana.lee@example.com', '+5566778899', FALSE, TRUE);
INSERT INTO members (name, surname, dateOfBirth, homeAddress, city, zipCode, email, phoneNumber, status, pending) VALUES ('Evan', 'Taylor', '1995-05-05', '321 Cedar St', 'Ogdenville', '5678', 'evan.taylor@example.com', '+3344556677', FALSE, TRUE);
INSERT INTO members (name, surname, dateOfBirth, homeAddress, city, zipCode, email, phoneNumber, status, pending) VALUES ('Fiona', 'Davis', '1980-12-25', '654 Birch St', 'North Haverbrook', '4321', 'fiona.davis@example.com', '+7788990011', FALSE, TRUE);

-- Events
INSERT INTO events (name, place, starts, ends) VALUES ('Tech Conference 2024', 'New York', '2024-12-10 09:00:00', '2024-12-10 18:00:00');
INSERT INTO events (name, place, starts, ends) VALUES ('Music Festival', 'Los Angeles', '2024-12-15 12:00:00', '2024-12-15 23:59:59');
INSERT INTO events (name, place, starts, ends) VALUES ('Art Exhibition', 'Paris', '2025-01-20 10:00:00', '2025-01-22 19:00:00');
INSERT INTO events (name, place, starts, ends) VALUES ('Business Workshop', 'London', '2025-02-10 08:30:00', '2025-02-10 17:00:00');
INSERT INTO events (name, place, starts, ends) VALUES ('Coding Hackathon', 'Berlin', '2025-03-05 09:00:00', '2025-03-06 20:00:00');

-- Associate Members with Events in the Join Table
INSERT INTO event_member (event_id, member_id) VALUES (1, 1); -- John attends Tech Conference
INSERT INTO event_member (event_id, member_id) VALUES (2, 1); -- John attends Music Festival
INSERT INTO event_member (event_id, member_id) VALUES (3, 2); -- Jane attends Art Exhibition
INSERT INTO event_member (event_id, member_id) VALUES (1, 2); -- Jane also attends Tech Conference
