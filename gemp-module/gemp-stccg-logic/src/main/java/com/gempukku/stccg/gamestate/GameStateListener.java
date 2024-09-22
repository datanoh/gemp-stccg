package com.gempukku.stccg.gamestate;

import com.gempukku.stccg.common.AwaitingDecision;
import com.gempukku.stccg.game.Player;

import java.util.List;

public interface GameStateListener {
    String getPlayerId();
    void sendEvent(GameEvent gameEvent);
    void sendEvent(GameEvent.Type eventType);

    void initializeBoard(List<String> playerIds, boolean discardIsPublic);

    void setPlayerDecked(Player player, boolean bool);
    void setPlayerScore(String playerId, int points);

    void setTribbleSequence(String tribbleSequence);

    void setCurrentPlayerId(String playerId);

    void setCurrentPhase(String currentPhase);

    void sendMessage(String message);

    void decisionRequired(String playerId, AwaitingDecision awaitingDecision);

    void sendWarning(String playerId, String warning);

    void endGame();

}
