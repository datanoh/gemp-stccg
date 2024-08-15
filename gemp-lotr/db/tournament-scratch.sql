

SELECT *
FROM scheduled_tournament st 

UPDATE scheduled_tournament 
SET start_date = '2023-10-14 20:35:00'#playoff = 'wc-swiss' #start_date = '2023-10-14 20:30:00', started = false
WHERE id IN (17)

INSERT INTO gemp_db.scheduled_tournament
(tournament_id, name, format, start_date, cost, playoff, tiebreaker, prizes, minimum_players, manual_kickoff, started)
VALUES('2024-wc-champ-pc-fotr', '2024 PC-FOTR Championship', 'pc_fotr_block', '2024-08-10 17:00:00', 0, 'swiss', 'owr', 'daily', 2, true, false);
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

SELECT class, count(*)
FROM league l
group by class

UPDATE league 
SET parameters = '20230908,default,0.69,3,2,open,7,5,rev_tow_sta,7,10'
WHERE id = 642


SELECT *
FROM tournament_match tm 
ORDER BY id DESC


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



SELECT id, tournament_id, name, format, start_date, cost, playoff,
                        tiebreaker, prizes, minimum_players, manual_kickoff, started
                    FROM scheduled_tournament
                    WHERE started = 0
                        AND start_date <= "2024-08-10 16:01"


SELECT *
FROM tournament t 
ORDER BY id DESC

SELECT 
	*
FROM tournament t
WHERE name IS NULL

UPDATE tournament 
SET stage = 'Playing games'
WHERE id = 1432

UPDATE tournament 
SET start_date = '2023-10-01 20:35:00.000'
WHERE id = 1423


UPDATE tournament 
SET collection = 'default'
WHERE id IN (1411, 1412, 1413, 1414, 1415)


SELECT *
FROM tournament_player tp 
ORDER BY ID DESC

UPDATE tournament_player 
SET dropped = 1
WHERE id = 11304

SELECT *
FROM tournament_match tm 
ORDER BY id DESC


INSERT INTO gemp_db.tournament_player
(tournament_id, player, deck_name, deck, dropped)
VALUES('2023-wc-group-stage-pc-fotr', 'Raelag', 'Nine Walkers / Uruk', '2_102*|1_1|1_324,1_331,1_341,2_119,1_349,1_352,1_354,3_117,1_361|1_13,1_50,2_122*,1_89*,1_97*,1_302,1_307,2_114,1_27,3_10,1_34*,51_40,51_40,51_40,51_45,51_45,1_56,1_57,1_286,1_127,3_66,53_68,53_68,53_68,1_143,1_143,1_143,2_46,2_46,1_148,1_148,1_148,3_75,3_75,3_75,1_156,1_156,1_156,1_156,2_93,51_313,51_313,1_318,2_105,1_44*,1_44*,2_20,2_20,1_296,1_296,1_296,1_298,1_298,1_298,1_298,2_39,1_121*,1_121*,1_136,51_139,51_139,51_139,51_139,52_108,1_133,1_133,1_133,1_133,1_127,1_148', '0');

UPDATE gemp_db.tournament_player
SET deck_name= 'Nine Walkers / Uruk', deck='2_102*|1_1|1_324,1_331,1_341,2_119,1_349,1_352,1_354,3_117,1_361|1_13,1_50,2_122*,1_89*,1_97*,1_302,1_307,2_114,1_27,3_10,1_34*,51_40,51_40,51_40,51_45,51_45,1_56,1_57,1_286,1_127,3_66,53_68,53_68,53_68,1_143,1_143,1_143,2_46,2_46,1_148,1_148,1_148,3_75,3_75,3_75,1_156,1_156,1_156,1_156,2_93,51_313,51_313,1_318,2_105,1_44*,1_44*,2_20,2_20,1_296,1_296,1_296,1_298,1_298,1_298,1_298,2_39,1_121*,1_121*,1_136,51_139,51_139,51_139,51_139,52_108,1_133,1_133,1_133,1_133,1_127,1_148'
WHERE id = 11317



SELECT *
FROM tournament_match tm 
ORDER BY id DESC


SELECT *
FROM game_history gh 
WHERE tournament IN ('2024 PC-FOTR Championship')
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

