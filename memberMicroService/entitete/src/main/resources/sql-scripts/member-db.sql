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


-- Example SQL Insert Statements for `members` table with Slovenian names

-- Members (Slovenian names)
INSERT INTO members (name, surname, dateOfBirth, homeAddress, city, zipCode, email, phoneNumber, status, pending) VALUES ('Matevž', 'Novak', '1985-03-22', 'Cesta v Gorice 12', 'Ljubljana', '1000', 'matevz.novak@example.com', '+38640123456', TRUE, FALSE);
INSERT INTO members (name, surname, dateOfBirth, homeAddress, city, zipCode, email, phoneNumber, status, pending) VALUES ('Ana', 'Kovač', '1992-07-18', 'Trg republike 4', 'Maribor', '2000', 'ana.kovac@example.com', '+38660234567', TRUE, FALSE);
INSERT INTO members (name, surname, dateOfBirth, homeAddress, city, zipCode, email, phoneNumber, status, pending) VALUES ('Luka', 'Jovanović', '1980-10-10', 'Rudarska ulica 5', 'Celje', '3000', 'luka.jovanovic@example.com', '+38641122334', TRUE, FALSE);
INSERT INTO members (name, surname, dateOfBirth, homeAddress, city, zipCode, email, phoneNumber, status, pending) VALUES ('Maja', 'Horvat', '1995-02-03', 'Zeleni trg 7', 'Kranj', '4000', 'maja.horvat@example.com', '+38660876543', TRUE, FALSE);
INSERT INTO members (name, surname, dateOfBirth, homeAddress, city, zipCode, email, phoneNumber, status, pending) VALUES ('Marko', 'Zupančič', '1988-05-25', 'Gorenjska cesta 21', 'Novo Mesto', '8000', 'marko.zupancic@example.com', '+38660987654', TRUE, FALSE);

-- Pending Members
INSERT INTO members (name, surname, dateOfBirth, homeAddress, city, zipCode, email, phoneNumber, status, pending) VALUES ('Ivana', 'Potočnik', '1990-04-10', 'Nazarje 2', 'Ljubljana', '1000', 'ivana.potocnik@example.com', '+38660222345', FALSE, TRUE);
INSERT INTO members (name, surname, dateOfBirth, homeAddress, city, zipCode, email, phoneNumber, status, pending) VALUES ('Tomaž', 'Jereb', '1983-08-15', 'Podgorska cesta 15', 'Maribor', '2000', 'tomaz.jereb@example.com', '+38640456789', FALSE, TRUE);
INSERT INTO members (name, surname, dateOfBirth, homeAddress, city, zipCode, email, phoneNumber, status, pending) VALUES ('Nina', 'Kranjc', '1997-12-01', 'Ajdovščina 34', 'Koper', '6000', 'nina.kranjc@example.com', '+38660123456', FALSE, TRUE);
INSERT INTO members (name, surname, dateOfBirth, homeAddress, city, zipCode, email, phoneNumber, status, pending) VALUES ('Filip', 'Knežević', '1986-11-27', 'Šempeter 9', 'Celje', '3000', 'filip.knezevic@example.com', '+38660345678', FALSE, TRUE);
INSERT INTO members (name, surname, dateOfBirth, homeAddress, city, zipCode, email, phoneNumber, status, pending) VALUES ('Katja', 'Pavli', '1994-03-14', 'Linhartova cesta 10', 'Novo Mesto', '8000', 'katja.pavli@example.com', '+38660654321', FALSE, TRUE);

-- Example Events (Slovenian-themed events)
INSERT INTO events (name, place, starts, ends) VALUES ('Festival vina', 'Maribor', '2024-05-10 10:00:00', '2024-05-12 18:00:00');
INSERT INTO events (name, place, starts, ends) VALUES ('Slovenski teden kulture', 'Ljubljana', '2024-06-20 09:00:00', '2024-06-22 21:00:00');
INSERT INTO events (name, place, starts, ends) VALUES ('Tekmovanje v plezanju', 'Celje', '2024-08-15 09:00:00', '2024-08-15 18:00:00');
INSERT INTO events (name, place, starts, ends) VALUES ('Muzikalni festival', 'Kranj', '2024-09-05 18:00:00', '2024-09-07 23:59:59');
INSERT INTO events (name, place, starts, ends) VALUES ('Konferenca o inovacijah', 'Ljubljana', '2024-11-10 08:00:00', '2024-11-10 18:00:00');

-- Associate Members with Events in the Join Table
-- Matevž Novak attends Festival vina
INSERT INTO event_member (event_id, member_id) VALUES (6, 10);
-- Ana Kovač attends Slovenski teden kulture
INSERT INTO event_member (event_id, member_id) VALUES (7, 11);
-- Luka Jovanović attends Tekmovanje v plezanju
INSERT INTO event_member (event_id, member_id) VALUES (8, 12);
-- Maja Horvat attends Muzikalni festival
INSERT INTO event_member (event_id, member_id) VALUES (9, 13);
-- Marko Zupančič attends Konferenca o inovacijah
INSERT INTO event_member (event_id, member_id) VALUES (10, 14);

