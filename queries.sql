-- Insert into Venue Table
INSERT INTO Venue (venue_name, location, capacity) VALUES
('Staples Center', 'Los Angeles, CA', 19068),
('Madison Square Garden', 'New York, NY', 19918),
('TD Garden', 'Boston, MA', 19056),
('United Center', 'Chicago, IL', 20344),
('Chase Center', 'San Francisco, CA', 18064),
('American Airlines Arena', 'Miami, FL', 19916),
('Ball Arena', 'Denver, CO', 19000),
('Wells Fargo Center', 'Philadelphia, PA', 20852);

-- Insert into Team Table
INSERT INTO Team (team_name, city, owner, home_venue_id) VALUES
('Lakers', 'Los Angeles', 'Jeanie Buss', 1),
('Knicks', 'New York', 'James Dolan', 2),
('Celtics', 'Boston', 'Wyc Grousbeck', 3),
('Bulls', 'Chicago', 'Jerry Reinsdorf', 4),
('Warriors', 'San Francisco', 'Joe Lacob', 5),
('Heat', 'Miami', 'Micky Arison', 6),
('Nuggets', 'Denver', 'Stan Kroenke', 7),
('76ers', 'Philadelphia', 'Josh Harris', 8);

-- Insert into Player Table
INSERT INTO Player (first_name, last_name, dob, position, height, weight, team_id, mentor_id) VALUES
('LeBron', 'James', '1984-12-30', 'SF', 206, 113, 1, NULL),
('Kevin', 'Durant', '1988-09-29', 'SF', 206, 109, 5, NULL),
('Kyrie', 'Irving', '1992-03-23', 'PG', 185, 88, 2, NULL),
('Giannis', 'Antetokounmpo', '1994-12-06', 'PF', 211, 110, 8, NULL),
('Stephen', 'Curry', '1988-03-14', 'PG', 191, 86, 5, NULL),
('Jimmy', 'Butler', '1989-09-14', 'SF', 198, 104, 6, NULL),
('Nikola', 'Jokic', '1995-02-19', 'C', 211, 113, 7, NULL),
('Joel', 'Embiid', '1994-03-16', 'C', 213, 127, 8, NULL);

-- Insert into Phone Table
INSERT INTO Phone (player_id, phone_number) VALUES
(1, '555-0100'),
(2, '555-0101'),
(3, '555-0102'),
(4, '555-0103'),
(5, '555-0104'),
(6, '555-0105'),
(7, '555-0106'),
(8, '555-0107');

-- Insert into Referee Table
INSERT INTO Referee (referee_name, experience_years) VALUES
('John Doe', 10),
('Jane Smith', 8),
('Mike Johnson', 12),
('Sara Lee', 5);

-- Insert into Coach Table
INSERT INTO Coach (coach_name, experience_years, team_id) VALUES
('Frank Vogel', 10, 1),
('Tom Thibodeau', 8, 2),
('Brad Stevens', 12, 3),
('Billy Donovan', 6, 4),
('Steve Kerr', 7, 5),
('Erik Spoelstra', 15, 6),
('Michael Malone', 9, 7),
('Doc Rivers', 20, 8);

-- Insert into Sponsor Table
INSERT INTO Sponsor (sponsor_name, sponsor_value, team_id) VALUES
('Nike', 1000000.00, 1),
('Adidas', 800000.00, 2),
('Under Armour', 600000.00, 3),
('Gatorade', 700000.00, 4),
('Rakuten', 500000.00, 5),
('Tissot', 300000.00, 6),
('Pepsi', 400000.00, 7),
('StubHub', 350000.00, 8);

-- Insert into Match Table
INSERT INTO `Match` (home_team_id, away_team_id, match_date, venue_id, referee_id , winner_team_id ) VALUES
(1, 2, '2024-10-01', 1, 1 , 1),
(3, 4, '2024-10-02', 3, 2 , 3),
(5, 6, '2024-10-03', 5, 3 , 5),
(7, 8, '2024-10-04', 7, 4 , 8);

-- Insert into Stats Table
INSERT INTO Stats (player_id, match_id, points, assists, rebounds) VALUES
(1, 1, 30, 8, 5),
(2, 1, 22, 5, 4),
(3, 2, 28, 7, 3),
(4, 2, 26, 6, 9),
(5, 3, 34, 11, 5),
(6, 3, 21, 4, 10),
(7, 4, 15, 5, 8),
(8, 4, 18, 3, 6);
