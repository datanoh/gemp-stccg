package com.gempukku.stccg.tournament;

import com.gempukku.stccg.common.CardDeck;
import com.gempukku.stccg.db.vo.CollectionType;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ITournamentService {
    void clearCache();

    void addPlayer(String tournamentId, String playerName, CardDeck deck);

    void dropPlayer(String tournamentId, String playerName);

    Set<String> getPlayers(String tournamentId);

    Map<String, CardDeck> getPlayerDecks(String tournamentId, String format);

    Set<String> getDroppedPlayers(String tournamentId);

    CardDeck getPlayerDeck(String tournamentId, String player, String format);

    void addMatch(String tournamentId, int round, String playerOne, String playerTwo);

    void setMatchResult(String tournamentId, int round, String winner);

    void setPlayerDeck(String tournamentId, String player, CardDeck deck);

    List<TournamentMatch> getMatches(String tournamentId);

    Tournament addTournament(String tournamentId, String draftType, String tournamentName, String format, CollectionType collectionType, Tournament.Stage stage, String pairingMechanism, String prizeScheme, Date start);

    void updateTournamentStage(String tournamentId, Tournament.Stage stage);

    void updateTournamentRound(String tournamentId, int round);

    List<Tournament> getOldTournaments(long since);

    List<Tournament> getLiveTournaments();

    Tournament getTournamentById(String tournamentId);

    void addRoundBye(String tournamentId, String player, int round);

    Map<String, Integer> getPlayerByes(String tournamentId);

    List<TournamentQueueInfo> getUnstartedScheduledTournamentQueues(long tillDate);

    void updateScheduledTournamentStarted(String scheduledTournamentId);
}
