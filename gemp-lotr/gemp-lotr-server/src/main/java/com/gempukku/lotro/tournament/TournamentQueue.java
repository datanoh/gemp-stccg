package com.gempukku.lotro.tournament;

import com.gempukku.lotro.collection.CollectionsManager;
import com.gempukku.lotro.db.vo.CollectionType;
import com.gempukku.lotro.game.User;
import com.gempukku.lotro.cards.LotroDeck;

import java.io.IOException;
import java.sql.SQLException;

public interface TournamentQueue {
    int getCost();

    String getFormat();

    CollectionType getCollectionType();

    String getTournamentQueueName();

    String getPrizesDescription();

    String getPairingDescription();

    String getStartCondition();

    boolean isRequiresDeck();

    boolean process(TournamentQueueCallback tournamentQueueCallback, CollectionsManager collectionsManager) throws SQLException, IOException;

    void joinPlayer(CollectionsManager collectionsManager, User player, LotroDeck deck) throws SQLException, IOException;

    void leavePlayer(CollectionsManager collectionsManager, User player) throws SQLException, IOException;

    void leaveAllPlayers(CollectionsManager collectionsManager) throws SQLException, IOException;

    int getPlayerCount();

    boolean isPlayerSignedUp(String player);

    boolean isJoinable();
}
