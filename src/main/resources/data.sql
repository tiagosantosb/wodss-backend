-- Admin
INSERT INTO user (id, admin, email, name, password) VALUES (0, 1, 'wodss.fhnw@gmail.com', 'Administrator', '$argon2d$v=19$m=65536,t=3,p=4$cr3v3QLF9LDYbt5UzmdJ8g$Uve1nkPvQD8y0WUMwxs3pw');

-- Teams
INSERT INTO team (name, code, group_stage) VALUES ('Ägypten', 'EGY', 'A');
INSERT INTO team (name, code, group_stage) VALUES ('Argentinien', 'ARG', 'D');
INSERT INTO team (name, code, group_stage) VALUES ('Australien', 'AUS', 'C');
INSERT INTO team (name, code, group_stage) VALUES ('Belgien', 'BEL', 'G');
INSERT INTO team (name, code, group_stage) VALUES ('Brasilien', 'BRA', 'E');
INSERT INTO team (name, code, group_stage) VALUES ('Costa Rica', 'CRC', 'E');
INSERT INTO team (name, code, group_stage) VALUES ('Dänemark', 'DNK', 'C');
INSERT INTO team (name, code, group_stage) VALUES ('Deutschland', 'GER', 'F');
INSERT INTO team (name, code, group_stage) VALUES ('England', 'ENG', 'G');
INSERT INTO team (name, code, group_stage) VALUES ('Frankreich', 'FRA', 'C');
INSERT INTO team (name, code, group_stage) VALUES ('Iran', 'IRN', 'B');
INSERT INTO team (name, code, group_stage) VALUES ('Island', 'ISL', 'D');
INSERT INTO team (name, code, group_stage) VALUES ('Japan', 'JPN', 'H');
INSERT INTO team (name, code, group_stage) VALUES ('Kolumbien', 'COL', 'H');
INSERT INTO team (name, code, group_stage) VALUES ('Südkorea', 'KOR', 'F');
INSERT INTO team (name, code, group_stage) VALUES ('Kroatien', 'CRO', 'D');
INSERT INTO team (name, code, group_stage) VALUES ('Marokko', 'MAR', 'B');
INSERT INTO team (name, code, group_stage) VALUES ('Mexiko', 'MEX', 'F');
INSERT INTO team (name, code, group_stage) VALUES ('Nigeria', 'NIG', 'D');
INSERT INTO team (name, code, group_stage) VALUES ('Panama', 'PAN', 'G');
INSERT INTO team (name, code, group_stage) VALUES ('Peru', 'PER', 'C');
INSERT INTO team (name, code, group_stage) VALUES ('Polen', 'POL', 'H');
INSERT INTO team (name, code, group_stage) VALUES ('Portugal', 'POR', 'B');
INSERT INTO team (name, code, group_stage) VALUES ('Russland', 'RUS', 'A');
INSERT INTO team (name, code, group_stage) VALUES ('Saudiarabien', 'KSA', 'A');
INSERT INTO team (name, code, group_stage) VALUES ('Schweden', 'SWE', 'F');
INSERT INTO team (name, code, group_stage) VALUES ('Schweiz', 'SUI', 'E');
INSERT INTO team (name, code, group_stage) VALUES ('Senegal', 'SEN', 'H');
INSERT INTO team (name, code, group_stage) VALUES ('Serbien', 'SRB', 'E');
INSERT INTO team (name, code, group_stage) VALUES ('Spanien', 'ESP', 'B');
INSERT INTO team (name, code, group_stage) VALUES ('Tunesien', 'TUN', 'G');
INSERT INTO team (name, code, group_stage) VALUES ('Uruguay', 'URU', 'A');

-- Stadiums
INSERT INTO stadium (id, name, city) VALUES (1, 'Olympiastadion Luschniki', 'Moskau');
INSERT INTO stadium (id, name, city) VALUES (2, 'Otkrytije Arena', 'Moskau');
INSERT INTO stadium (id, name, city) VALUES (3, 'Sankt-Petersburg-Stadion', 'Sankt Petersburg');
INSERT INTO stadium (id, name, city) VALUES (4, 'Kaliningrad-Stadion', 'Kaliningrad');
INSERT INTO stadium (id, name, city) VALUES (5, 'Kasan-Arena', 'Kazan');
INSERT INTO stadium (id, name, city) VALUES (6, 'Stadion Nischni Nowgorod', 'Nischni Nowgorod');
INSERT INTO stadium (id, name, city) VALUES (7, 'Kosmos-Arena', 'Samara');
INSERT INTO stadium (id, name, city) VALUES (8, 'Wolgograd Arena', 'Wolgograd');
INSERT INTO stadium (id, name, city) VALUES (9, 'Mordowia Arena', 'Saransk');
INSERT INTO stadium (id, name, city) VALUES (10, 'Rostow Arena', 'Rostow am Don');
INSERT INTO stadium (id, name, city) VALUES (11, 'Olympiastadion Sotschi', 'Sotschi');
INSERT INTO stadium (id, name, city) VALUES (12, 'Zentralstadium', 'Jekaterinburg');

-- Matches
-- Match Day 1
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (1, '2018-06-14 17:00:00', 1, 1, 'RUS', 'KSA', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (2, '2018-06-15 14:00:00', 12, 1, 'EGY', 'URU', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (3, '2018-06-15 17:00:00', 3, 1, 'MAR', 'IRN', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (4, '2018-06-15 20:00:00', 11, 1, 'POR', 'ESP', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (5, '2018-06-16 12:00:00', 5, 1, 'FRA', 'AUS', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (6, '2018-06-16 18:00:00', 9, 1, 'PER', 'DNK', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (7, '2018-06-16 15:00:00', 2, 1, 'ARG', 'ISL', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (8, '2018-06-16 21:00:00', 4, 1, 'CRO', 'NIG', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (9, '2018-06-17 14:00:00', 7, 1, 'CRC', 'SRB', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (10, '2018-06-17 20:00:00', 10, 1, 'BRA', 'SUI', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (11, '2018-06-17 17:00:00', 1, 1, 'GER', 'MEX', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (12, '2018-06-18 14:00:00', 6, 1, 'SWE', 'KOR', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (13, '2018-06-18 17:00:00', 11, 1, 'BEL', 'PAN', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (14, '2018-06-18 20:00:00', 8, 1, 'TUN', 'ENG', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (15, '2018-06-19 14:00:00', 9, 1, 'COL', 'JPN', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (16, '2018-06-19 17:00:00', 2, 1, 'POL', 'SEN', null, null, null, null);
-- Match Day 2
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (17, '2018-06-19 14:00:00', 3, 2, 'RUS', 'EGY', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (18, '2018-06-20 17:00:00', 10, 2, 'URU', 'KSA', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (19, '2018-06-20 14:00:00', 1, 2, 'POR', 'MAR', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (20, '2018-06-20 20:00:00', 5, 2, 'IRN', 'ESP', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (21, '2018-06-21 14:00:00', 7, 2, 'DNK', 'AUS', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (22, '2018-06-21 17:00:00', 12, 2, 'FRA', 'PER', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (23, '2018-06-21 20:00:00', 6, 2, 'ARG', 'CRO', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (24, '2018-06-22 17:00:00', 8, 2, 'NIG', 'ISL', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (25, '2018-06-22 14:00:00', 3, 2, 'BRA', 'CRC', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (26, '2018-06-22 20:00:00', 4, 2, 'SRB', 'SUI', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (27, '2018-06-23 17:00:00', 10, 2, 'KOR', 'MEX', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (28, '2018-06-23 20:00:00', 11, 2, 'GER', 'SWE', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (29, '2018-06-23 14:00:00', 2, 2, 'BEL', 'TUN', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (30, '2018-06-24 14:00:00', 6, 2, 'ENG', 'PAN', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (31, '2018-06-24 17:00:00', 12, 2, 'JPN', 'SEN', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (32, '2018-06-24 20:00:00', 5, 2, 'POL', 'COL', null, null, null, null);
-- Match Day 3
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (33, '2018-06-25 16:00:00', 7, 3, 'URU', 'RUS', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (34, '2018-06-25 16:00:00', 8, 3, 'KSA', 'EGY', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (35, '2018-06-25 20:00:00', 9, 3, 'IRN', 'POR', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (36, '2018-06-25 20:00:00', 4, 3, 'ESP', 'MAR', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (37, '2018-06-26 16:00:00', 1, 3, 'DNK', 'FRA', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (38, '2018-06-26 16:00:00', 11, 3, 'AUS', 'PER', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (39, '2018-06-26 20:00:00', 3, 3, 'NIG', 'ARG', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (40, '2018-06-26 20:00:00', 10, 3, 'ISL', 'CRO', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (41, '2018-06-27 20:00:00', 2, 3, 'SRB', 'BRA', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (42, '2018-06-27 20:00:00', 6, 3, 'SUI', 'CRC', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (43, '2018-06-27 16:00:00', 5, 3, 'KOR', 'GER', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (44, '2018-06-27 16:00:00', 12, 3, 'MEX', 'SWE', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (45, '2018-06-28 20:00:00', 4, 3, 'ENG', 'BEL', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (46, '2018-06-28 20:00:00', 9, 3, 'PAN', 'TUN', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (47, '2018-06-28 16:00:00', 8, 3, 'JPN', 'POL', null, null, null, null);
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (48, '2018-06-28 16:00:00', 7, 3, 'SEN', 'COL', null, null, null, null);
-- Round of 16
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (49, '2018-06-30 20:00:00', 11, 4, null, null, '1. Platz Gruppe A', '2. Platz Gruppe B', 'A', 'B');
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (50, '2018-06-30 16:00:00', 5, 4, null, null, '1. Platz Gruppe C', '2. Platz Gruppe D', 'C', 'D');
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (51, '2018-07-02 16:00:00', 7, 4, null, null, '1. Platz Gruppe E', '2. Platz Gruppe F', 'E', 'F');
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (52, '2018-07-02 20:00:00', 10, 4, null, null, '1. Platz Gruppe G', '2. Platz Gruppe H', 'G', 'H');
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (53, '2018-07-01 16:00:00', 1, 4, null, null, '1. Platz Gruppe B', '2. Platz Gruppe A', 'B', 'A');
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (54, '2018-07-01 20:00:00', 6, 4, null, null, '1. Platz Gruppe D', '2. Platz Gruppe C', 'D', 'C');
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (55, '2018-07-03 16:00:00', 3, 4, null, null, '1. Platz Gruppe F', '2. Platz Gruppe E', 'F', 'E');
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (56, '2018-07-03 20:00:00', 2, 4, null, null, '1. Platz Gruppe H', '2. Platz Gruppe G', 'H', 'G');
-- Quarter-finals
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (57, '2018-07-06 16:00:00', 6, 5, null, null, 'Gewinner 49. Spiel', 'Gewinner 50. Spiel', 'AB', 'CD');
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (58, '2018-07-06 20:00:00', 5, 5, null, null, 'Gewinner 53. Spiel', 'Gewinner 54. Spiel', 'EF', 'GH');
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (59, '2018-07-07 20:00:00', 11, 5, null, null, 'Gewinner 51. Spiel', 'Gewinner 52. Spiel', 'AB', 'CD');
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (60, '2018-07-07 16:00:00', 7, 5, null, null, 'Gewinner 55. Spiel', 'Gewinner 56. Spiel', 'EF', 'GH');
-- Semi-finals
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (61, '2018-07-10 20:00:00', 3, 6, null, null, 'Gewinner 57. Spiel', 'Gewinner 58. Spiel', 'ABCD', 'EFGH');
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (62, '2018-07-11 20:00:00', 1, 6, null, null, 'Gewinner 59. Spiel', 'Gewinner 60. Spiel', 'ABCD', 'EFGH');
-- Third place play-off
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (63, '2018-07-14 16:00:00', 3, 7, null, null, 'Verlierer 61. Spiel', 'Verlierer 62. Spiel', 'ABCDEFGH', 'ABCDEFGH');
-- Final
INSERT INTO match (id, datetime, stadium_id, category, team1_code, team2_code, placeholder_team1, placeholder_team2, placeholder_group1, placeholder_group2) 
VALUES (64, '2018-07-15 17:00:00', 1, 7, null, null, 'Gewinner 61. Spiel', 'Gewinner 62. Spiel', 'ABCDEFGH', 'ABCDEFGH');

