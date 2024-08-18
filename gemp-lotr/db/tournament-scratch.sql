

SELECT *
FROM scheduled_tournament st 
ORDER BY ID desc

UPDATE scheduled_tournament 
SET start_date = '2023-10-14 20:35:00'#playoff = 'wc-swiss' #start_date = '2023-10-14 20:30:00', started = false
WHERE id IN (17)

INSERT INTO gemp_db.scheduled_tournament
(tournament_id, name, format, start_date, cost, playoff, tiebreaker, prizes, minimum_players, manual_kickoff, started)
VALUES('2024-wc-champ-pc-fotr', '2024 PC-FOTR Championship', 'pc_fotr_block', '2024-08-18 00:04:00', 0, 'swiss', 'owr', 'daily', 2, true, false);
VALUES('2023-wc-group-stage-pc-movie', '2023 WC PC-Movie Group Stage', 'pc_movie', '2023-10-14 17:00:00', 0, 'swiss', 'owr', 'daily', 2, true, false);

DELETE 
FROM scheduled_tournament st 
WHERE id = 18



SELECT *
FROM player p 
WHERE name IN ('ketura', 'Tunadan', 'Ringbearer', 'johnec', 'stephan77', 'GeriGeli', 'Tonio', 'thedast7', 'Pokec', 'Beezey', 'balrog69', 'sempolPL', 'neergreve', 'dstaley', 'thefaker', 'Chadwick537', 'scyld', 'bign19', 'Pizdec', 'Axter', 'LukasSchor', 'talial', 'rbranco', 'Raelag', 'olga06', 'Yk1030', 'enolen', 'Aaron_Brutcher', 'MockingbirdME', 'basmelis', 'fnlgroove', 'Icarus')

UPDATE player 
SET type = 'upca'
WHERE id = 31537


SELECT *
FROM deck d 
where player_id = 20214
AND name LIKE 'Nine Walkers%'

#Nine Walkers fotr pc


SELECT *
FROM league l 
ORDER BY id DESC

SELECT *
FROM league_participation lp 
WHERE lp.league_type = '1723768400434'
ORDER BY id DESC

SELECT *
FROM league_match lm
-- LEFT JOIN league_participation lp 
--  	ON lp.player_name = lm.winner AND lp.league_type = lm.league_type 
-- LEFT JOIN player p
-- 	ON p.name = lm.loser
-- WHERE lm.league_type = '1723768400434'
ORDER BY ID DESC;


SELECT *
FROM game_history gh 
WHERE tournament = 'Sealed Team Gala – Reflections Multipath - Serie 1'
ORDER BY id DESC;

DELETE
FROM league_match
WHERE id IN (219953)

INSERT INTO gemp_db.league_match
(league_type, season_type, winner, loser)
VALUES
('1723768400434', 'Serie 1', 'Dbix', 'Cwianacus'),
('1723768400434', 'Serie 1', 'Cwianacus', 'Dbix'),
('1723768400434', 'Serie 1', 'bign19', 'cmndante89'),
('1723768400434', 'Serie 1', 'cmndante89', 'bign19'),
('1723768400434', 'Serie 1', 'Axter', 'LukasSchor'),
('1723768400434', 'Serie 1', 'LukasSchor', 'Axter'),
('1723768400434', 'Serie 1', 'domgam', 'fnlgroove'),
('1723768400434', 'Serie 1', 'fnlgroove', 'domgam'),
('1723768400434', 'Serie 1', 'Amser', 'Quiniscal'),
('1723768400434', 'Serie 1', 'Quiniscal', 'Amser'),
('1723768400434', 'Serie 1', 'shuler', 'Wend'),
('1723768400434', 'Serie 1', 'Wend', 'shuler'),
('1723768400434', 'Serie 1', 'tony784', 'dmaz'),
('1723768400434', 'Serie 1', 'dmaz', 'tony784'),
('1723768400434', 'Serie 1', 'thefaker', 'Pizdec'),
('1723768400434', 'Serie 1', 'Pizdec', 'thefaker'),
('1723768400434', 'Serie 1', 'Sickofpalantirs', 'macheteman'),
('1723768400434', 'Serie 1', 'macheteman', 'Sickofpalantirs'),
('1723768400434', 'Serie 1', 'dstaley', 'balrog69'),
('1723768400434', 'Serie 1', 'balrog69', 'dstaley'),
('1723768400434', 'Serie 1', 'sempolPL', 'Modrzew'),
('1723768400434', 'Serie 1', 'Modrzew', 'sempolPL'),
('1723768400434', 'Serie 1', 'hrothlackin', 'Joebers'),
('1723768400434', 'Serie 1', 'Joebers', 'hrothlackin'),
('1723768400434', 'Serie 1', 'tristelune', 'Tunadan'),
('1723768400434', 'Serie 1', 'Tunadan', 'tristelune'),
('1723768400434', 'Serie 1', 'miortolan', 'bebpc'),
('1723768400434', 'Serie 1', 'bebpc', 'miortolan'),
('1723768400434', 'Serie 1', 'Cleston', 'rbranco'),
('1723768400434', 'Serie 1', 'rbranco', 'Cleston'),
('1723768400434', 'Serie 1', 'basmelis', 'olga06'),
('1723768400434', 'Serie 1', 'olga06', 'basmelis'),
('1723768400434', 'Serie 1', 'm_scarpato', 'Kainbr'),
('1723768400434', 'Serie 1', 'Kainbr', 'm_scarpato'),
('1723768400434', 'Serie 1', 'Thayli', 'Deets'),
('1723768400434', 'Serie 1', 'Deets', 'Thayli'),
('1723768400434', 'Serie 1', 'tauri_p-90', 'daisukeman'),
('1723768400434', 'Serie 1', 'daisukeman', 'tauri_p-90'),


SELECT class, count(*)
FROM league l
group by class

UPDATE league 
SET parameters = '{"code":1723768400434,"collectionName":"Sealed Team Gala – Reflections Multipath","cost":0,"description":"This is a team event that is only open to players who have registered on the sign-up sheet: <a href=\\\"https://docs.google.com/spreadsheets/d/1uPqJvqlNXRIr62amSaBb89l-elXlllOJJE27y477H8M/edit?usp=sharing\\\">here</a>. <br><br>Each player will be given the following sealed items: <br>12 Reflections Boosters,<br>2 Shotgun Enquea,<br>1 Choice of Starter Deck from sets 1-8<br><br>Once players have opened their Reflections boosters, they will need to consult with their teammate on which starter deck they want to use. Teammates <b>MUST</b> choose different starter decks.<br><br>The league uses the multipath site-path, so Ring-bearer skirmishes <b> CANNOT</b> be cancelled. The total number of games is 10; each team will have 2 fake games entered into the database (a win and a loss for each) to prevent you from accidentally facing your teammate.<br><br> Have fun everyone!","inviteOnly":true,"maxRepeatMatches":1,"name":"Sealed Team Gala – Reflections Multipath","series":[{"duration":20,"format":"ref_sealed","matches":12}],"start":"2024-08-16 00:00:00"}'
WHERE id = 728

SELECT *
FROM game_history gh 
ORDER BY id DESC


SELECT *
FROM tournament_match tm 
ORDER BY id DESC

DELETE 
FROM tournament_match
WHERE id >= 14351


SELECT 
	 gh.winner 
	,gh.loser
	,'' AS round
	,gh.win_reason 
	,gh.lose_reason 
	,gh.start_date AS `Start`
	,gh.end_date AS `End`
	,CONCAT('https://play.lotrtcgpc.net/gemp-lotr/game.html?replayId=',REPLACE(winner, '_', '%5F'), '$', win_recording_id) AS winner_replay
	,CONCAT('https://play.lotrtcgpc.net/gemp-lotr/game.html?replayId=',REPLACE(loser, '_', '%5F'), '$', lose_recording_id) AS loser_replay
FROM game_history gh 
WHERE tournament = '2024 PC-FOTR Championship'
	#AND (winner = 'Chadwick537' OR loser = 'Chadwick537')



SELECT *
FROM scheduled_tournament
ORDER BY ID DESC


SELECT *
FROM tournament t 
ORDER BY id DESC


UPDATE tournament 
SET stage = 'Playing games'
WHERE id = 1387

UPDATE tournament 
SET round = '4'
WHERE id = 1387


UPDATE tournament 
SET collection = 'default'
WHERE id IN (1411, 1412, 1413, 1414, 1415)


SELECT *
FROM tournament_player tp 
ORDER BY ID DESC

DELETE
FROM tournament_match
WHERE id IN (14315)

UPDATE tournament_player 
SET dropped = 1
WHERE id = 11304

SELECT *
FROM tournament_match tm 
ORDER BY id DESC


SELECT *
FROM deck d
INNER JOIN player p
	ON p.id = d.player_id 
WHERE p.name = 'CrisSilva'


INSERT INTO gemp_db.tournament_player
(tournament_id, player, deck_name, deck, dropped)
VALUES('2023-wc-group-stage-pc-fotr', 'Raelag', 'Nine Walkers / Uruk', '2_102*|1_1|1_324,1_331,1_341,2_119,1_349,1_352,1_354,3_117,1_361|1_13,1_50,2_122*,1_89*,1_97*,1_302,1_307,2_114,1_27,3_10,1_34*,51_40,51_40,51_40,51_45,51_45,1_56,1_57,1_286,1_127,3_66,53_68,53_68,53_68,1_143,1_143,1_143,2_46,2_46,1_148,1_148,1_148,3_75,3_75,3_75,1_156,1_156,1_156,1_156,2_93,51_313,51_313,1_318,2_105,1_44*,1_44*,2_20,2_20,1_296,1_296,1_296,1_298,1_298,1_298,1_298,2_39,1_121*,1_121*,1_136,51_139,51_139,51_139,51_139,52_108,1_133,1_133,1_133,1_133,1_127,1_148', '0');

UPDATE gemp_db.tournament_player
SET deck_name= '(MOVIE-PC) FrodoDw- Gollum', deck='2_102|7_2|7_336,7_339,8_118,10_119,7_349,8_120,7_359,7_360,7_329|9_28,10_21,5_25,10_23,10_23,10_23,10_23,9_48,9_48,1_231,1_231,8_30,8_30,8_30,10_19,7_53,7_53,7_53,7_53,7_74,5_30,5_30,5_22,7_57,7_57,7_57,10_20,8_22,8_22,8_24,8_24,8_24,8_24,10_22,10_22,6_46,6_46,6_46,6_46,10_24,10_24,10_116,53_108,1_296,52_108,2_105,1_308,1_317,1_89,1_3,1_3,1_3,1_3,8_3,9_3,9_3,9_3,9_3,51_9,51_9,51_9,1_8,2_5,2_5,1_15,8_5,10_1,2_10,2_10,2_10,1_21,9_6,9_7,9_8,1_27,2_10,51_9,2_5,8_3,2_4,2_4,9_5|100_4'
WHERE id = 11409



SELECT *
FROM tournament_match tm 
ORDER BY id DESC


SELECT *
FROM game_history gh 
-- WHERE tournament IN ('2024 PC-Movie Championship')
WHERE format_name = 'Movie Block (PC)'
ORDER BY id DESC


UPDATE tournament_match 
SET winner = 'dstaley'
WHERE id = 14638

UPDATE tournament_match 
SET winner = stephan77
WHERE id = 14637


UPDATE game_history 
SET winner = 'dstaley', winnerId = 33114, loser = 'johnec', loserId = 35804
WHERE id = 1214409

UPDATE game_history 
SET winner = 'stephan77', winnerId = 30969, loser = 'balrog69', loserId = 35908
WHERE id = 1214410



SELECT FROM_UNIXTIME('1695509366664')

SELECT *, FROM_UNIXTIME(floor(transfer_date / 1000)) AS transfer_time
FROM transfer t 
WHERE player = 'Aaron_Brutcher'
	AND collection LIKE '%2_125%'
ORDER BY id DESC

SELECT *
FROM collection c 
INNER JOIN player p 
	ON p.id = c.player_id 
INNER JOIN collection_entries ce 
	ON ce.collection_id = c.id 
WHERE p.name = 'Aaron_Brutcher'
	AND c.id IN (199929, 202698)
	AND ce.product = '2_125'
ORDER BY IFNULL(ce.modified_date, ce.created_date)


UPDATE collection_entries 
SET quantity = 4
WHERE collection_id = 199929 AND product = '2_125'



SELECT c.*
FROM collection c 
INNER JOIN player p 
	ON p.id = c.player_id 
WHERE p.name = 'Aaron_Brutcher'

