CREATE DATABASE nba_db;

USE nba_db;

-- Venue Table
CREATE TABLE Venue (
    venue_id INT PRIMARY KEY AUTO_INCREMENT,
    venue_name VARCHAR(100) NOT NULL,
    location VARCHAR(100) NOT NULL,
    capacity INT NOT NULL CHECK (capacity > 0)  -- Ensure positive capacity
);

-- Team Table
CREATE TABLE Team (
    team_id INT PRIMARY KEY AUTO_INCREMENT,
    team_name VARCHAR(50) NOT NULL,
    city VARCHAR(50) NOT NULL,
    owner VARCHAR(50) NOT NULL,
    home_venue_id INT UNIQUE,  -- Home venue foreign key
    FOREIGN KEY (home_venue_id) REFERENCES Venue(venue_id) ON DELETE SET NULL
);

-- Player Table
CREATE TABLE Player (
    player_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(25) NOT NULL,
    last_name VARCHAR(25) NOT NULL,
    dob DATE NOT NULL,  -- Date of Birth
    position VARCHAR(25) NOT NULL,
    height INT,  -- Height in cm
    weight INT,   -- Weight in kg
    team_id INT,  -- Team of the player (can be NULL if the player is not in a team)
    mentor_id INT,  -- Self-referential foreign key (NULL if no mentor)
    FOREIGN KEY (team_id) REFERENCES Team(team_id) ON DELETE SET NULL,
    FOREIGN KEY (mentor_id) REFERENCES Player(player_id) ON DELETE SET NULL
);

-- Ensure mentor is from the same team
DELIMITER //

CREATE TRIGGER check_mentor_team
BEFORE INSERT ON Player
FOR EACH ROW
BEGIN
    DECLARE mentor_team INT;

    -- Only check if a mentor is provided
    IF NEW.mentor_id IS NOT NULL THEN
        -- Get the mentor's team
        SELECT team_id INTO mentor_team FROM Player WHERE player_id = NEW.mentor_id;

        -- Check if the mentor's team matches the player's team
        IF NEW.team_id IS NOT NULL AND mentor_team IS NOT NULL AND NEW.team_id != mentor_team THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Mentor must be from the same team.';
        END IF;
    END IF;
END //

DELIMITER ;

-- Phone Table (to store player phone numbers)
CREATE TABLE Phone (
    phone_id INT PRIMARY KEY AUTO_INCREMENT,
    player_id INT NOT NULL,
    phone_number VARCHAR(15) NOT NULL,  -- Assuming a max length for phone numbers
    FOREIGN KEY (player_id) REFERENCES Player(player_id) ON DELETE CASCADE,
    UNIQUE (phone_number)  -- Prevent duplicate phone numbers
);

-- Referee Table
CREATE TABLE Referee (
    referee_id INT PRIMARY KEY AUTO_INCREMENT,
    referee_name VARCHAR(50) NOT NULL,
    experience_years INT NOT NULL CHECK (experience_years >= 0)  -- Ensure non-negative experience
);

-- Coach Table (Each team can have only one coach)
CREATE TABLE Coach (
    coach_id INT PRIMARY KEY AUTO_INCREMENT,
    coach_name VARCHAR(50) NOT NULL,
    experience_years INT NOT NULL CHECK (experience_years >= 0),  -- Ensure non-negative experience
    team_id INT,
    FOREIGN KEY (team_id) REFERENCES Team(team_id) ON DELETE SET NULL,
    UNIQUE (team_id)  -- Ensures each team has only one coach
);

-- Sponsor Table (Each team can have only one sponsor)
CREATE TABLE Sponsor (
    sponsor_id INT PRIMARY KEY AUTO_INCREMENT,
    sponsor_name VARCHAR(100) NOT NULL,
    sponsor_value DECIMAL(10, 2) NOT NULL CHECK (sponsor_value >= 0),  -- Ensure non-negative value
    team_id INT,
    FOREIGN KEY (team_id) REFERENCES Team(team_id) ON DELETE CASCADE,
    UNIQUE (team_id)  -- Ensures each team can have only one sponsor
);

-- Match Table (Stores details of each match, ensuring home and away teams are different and the winner is valid)
CREATE TABLE `Match` (
    match_id INT PRIMARY KEY AUTO_INCREMENT,
    home_team_id INT NOT NULL,
    away_team_id INT NOT NULL,
    match_date DATE NOT NULL,
    venue_id INT NOT NULL,
    referee_id INT NOT NULL,
    winner_team_id INT,  -- NULL if no winner is determined yet
    FOREIGN KEY (winner_team_id) REFERENCES Team(team_id) ON DELETE SET NULL,
    FOREIGN KEY (home_team_id) REFERENCES Team(team_id) ON DELETE CASCADE,
    FOREIGN KEY (away_team_id) REFERENCES Team(team_id) ON DELETE CASCADE,
    FOREIGN KEY (venue_id) REFERENCES Venue(venue_id) ON DELETE CASCADE,
    FOREIGN KEY (referee_id) REFERENCES Referee(referee_id) ON DELETE CASCADE,
    UNIQUE (venue_id, match_date),
    UNIQUE (home_team_id, match_date),
    UNIQUE (away_team_id, match_date),
    UNIQUE (referee_id, match_date),
    CHECK (home_team_id <> away_team_id)  -- Ensure home and away teams are different
);

DELIMITER $$

CREATE TRIGGER validate_winner_team_insert
BEFORE INSERT ON `Match`
FOR EACH ROW
BEGIN
    IF NEW.winner_team_id IS NOT NULL AND NEW.winner_team_id NOT IN (NEW.home_team_id, NEW.away_team_id) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Winner team must be either the home team or away team';
    END IF;
END $$

DELIMITER ;


DELIMITER $$

CREATE TRIGGER check_team_venue_ids
BEFORE UPDATE ON `Match`
FOR EACH ROW
BEGIN
    -- Check if there exists home_team_id in Team Table
    IF NOT EXISTS (SELECT 1 FROM Team WHERE team_id = NEW.home_team_id) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Invalid home_team_id! This team does not exist.';
    END IF;

    -- Check if there exists away_team_id in Team Table
    IF NOT EXISTS (SELECT 1 FROM Team WHERE team_id = NEW.away_team_id) THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Invalid away_team_id! This team does not exist.';
    END IF;

    -- Check if the winner_team_id is either home_team_id or away_team_id
    IF NEW.winner_team_id IS NOT NULL AND NEW.winner_team_id != NEW.home_team_id AND NEW.winner_team_id != NEW.away_team_id THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Invalid winner_team_id! It must be either home_team_id or away_team_id.';
    END IF;

    -- Check if there exist venue in the Venue Table
    IF NOT EXISTS ( SELECT 1 FROM Venue WHERE venue_id = NEW.venue_id) THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Invalid venue_id! This venue does not exist.';
    END IF;

END $$

DELIMITER ;

DELIMITER $$

-- Trigger for updating team constraints
CREATE TRIGGER update_team
BEFORE UPDATE ON Team
FOR EACH ROW
BEGIN
    IF EXISTS (SELECT 1 FROM Team WHERE team_name = NEW.team_name AND team_id != NEW.team_id) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Another team with the same name already exists.';
    END IF;

    IF EXISTS (SELECT 1 FROM Team WHERE city = NEW.city AND team_id != NEW.team_id) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Another team with the same city already exists.';
    END IF;

    IF EXISTS (SELECT 1 FROM Team WHERE owner = NEW.owner AND team_id != NEW.team_id) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Another team with the same owner already exists.';
    END IF;

    IF NEW.home_venue_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM Venue WHERE venue_id = NEW.home_venue_id) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Specified venue does not exist.';
    END IF;
END $$

DELIMITER ;



-- Stats Table (Stores performance statistics for players in each match)
CREATE TABLE Stats (
    player_id INT NOT NULL,
    match_id INT NOT NULL,
    points INT DEFAULT 0,
    assists INT DEFAULT 0,
    rebounds INT DEFAULT 0,
    PRIMARY KEY (player_id, match_id),
    FOREIGN KEY (player_id) REFERENCES Player(player_id) ON DELETE CASCADE,
    FOREIGN KEY (match_id) REFERENCES `Match`(match_id) ON DELETE CASCADE
);
