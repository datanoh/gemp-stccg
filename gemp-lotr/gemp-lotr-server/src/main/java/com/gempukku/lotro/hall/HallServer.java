package com.gempukku.lotro.hall;

import com.gempukku.lotro.AbstractServer;
import com.gempukku.lotro.chat.ChatServer;
import com.gempukku.lotro.game.*;
import com.gempukku.lotro.logic.vo.LotroDeck;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HallServer extends AbstractServer {
    private ChatServer _chatServer;
    private LotroServer _lotroServer;

    private LotroFormat _lotroFormat = new DefaultLotroFormat(true);

    private Map<String, AwaitingTable> _awaitingTables = new ConcurrentHashMap<String, AwaitingTable>();
    private Map<String, String> _runningTables = Collections.synchronizedMap(new LinkedHashMap<String, String>());
    private int _nextTableId = 1;

    private final int _playerInactivityPeriod = 1000 * 10; // 10 seconds

    private Map<String, Long> _lastVisitedPlayers = Collections.synchronizedMap(new LinkedHashMap<String, Long>());

    public HallServer(LotroServer lotroServer, ChatServer chatServer) {
        _lotroServer = lotroServer;
        _chatServer = chatServer;
        _chatServer.createChatRoom("default");
    }

    /**
     * @param playerId
     * @return If table created, otherwise <code>false</code> (if the user already is sitting at a table or playing).
     */
    public synchronized void createNewTable(String playerId) throws HallException {
        LotroDeck lotroDeck = validateUserAndDeck(playerId);

        String tableId = String.valueOf(_nextTableId++);
        AwaitingTable table = new AwaitingTable();
        _awaitingTables.put(tableId, table);

        joinTableInternal(tableId, playerId, table, lotroDeck);
    }

    private LotroDeck validateUserAndDeck(String playerId) throws HallException {
        if (isPlayerBusy(playerId))
            throw new HallException("You can't play more than one game at a time or wait at more than one table");

        LotroDeck lotroDeck = _lotroServer.getParticipantDeck(playerId);
        if (lotroDeck == null)
            throw new HallException("You don't have a deck registered yet");

        boolean valid = _lotroFormat.validateDeck(lotroDeck);
        if (!valid)
            throw new HallException("Your registered deck is not valid for this format");
        return lotroDeck;
    }

    /**
     * @param playerId
     * @return If table joined, otherwise <code>false</code> (if the user already is sitting at a table or playing).
     */
    public synchronized boolean joinTableAsPlayer(String tableId, String playerId) throws HallException {
        AwaitingTable awaitingTable = _awaitingTables.get(tableId);
        if (awaitingTable == null)
            throw new HallException("Table is already taken or was removed");

        LotroDeck lotroDeck = validateUserAndDeck(playerId);

        joinTableInternal(tableId, playerId, awaitingTable, lotroDeck);

        return true;
    }

    private void joinTableInternal(String tableId, String playerId, AwaitingTable awaitingTable, LotroDeck lotroDeck) {
        boolean tableFull = awaitingTable.addPlayer(new LotroGameParticipant(playerId, lotroDeck));
        if (tableFull) {
            Set<LotroGameParticipant> players = awaitingTable.getPlayers();
            LotroGameParticipant[] participants = players.toArray(new LotroGameParticipant[players.size()]);
            String gameId = _lotroServer.createNewGame(new DefaultLotroFormat(true), participants);
            LotroGameMediator lotroGameMediator = _lotroServer.getGameById(gameId);
            lotroGameMediator.startGame();
            _runningTables.put(tableId, gameId);
            _awaitingTables.remove(tableId);
        }
    }

    public synchronized void leaveAwaitingTables(String playerId) {
        Map<String, AwaitingTable> copy = new HashMap<String, AwaitingTable>(_awaitingTables);
        for (Map.Entry<String, AwaitingTable> table : copy.entrySet()) {
            if (table.getValue().hasPlayer(playerId)) {
                boolean empty = table.getValue().removePlayer(playerId);
                if (empty)
                    _awaitingTables.remove(table.getKey());
            }
        }
    }

    private boolean isPlayerWaiting(String playerId) {
        for (AwaitingTable awaitingTable : _awaitingTables.values())
            if (awaitingTable.hasPlayer(playerId))
                return true;
        return false;
    }

    public void processTables(String participantId, HallInfoVisitor visitor) {
        visitor.playerIsWaiting(isPlayerWaiting(participantId));

        Map<String, AwaitingTable> copy = new HashMap<String, AwaitingTable>(_awaitingTables);
        for (Map.Entry<String, AwaitingTable> table : copy.entrySet())
            visitor.visitTable(table.getKey(), "Waiting", table.getValue().getPlayerNames());

        Map<String, String> runningCopy = new LinkedHashMap<String, String>(_runningTables);
        for (Map.Entry<String, String> runningGame : runningCopy.entrySet()) {
            LotroGameMediator lotroGameMediator = _lotroServer.getGameById(runningGame.getValue());
            if (lotroGameMediator != null)
                visitor.visitTable(runningGame.getKey(), lotroGameMediator.getGameStatus(), lotroGameMediator.getPlayersPlaying());
        }

        String playerTable = getPlayerTable(participantId);
        if (playerTable != null) {
            String gameId = _runningTables.get(playerTable);
            if (gameId != null) {
                LotroGameMediator lotroGameMediator = _lotroServer.getGameById(gameId);
                if (lotroGameMediator != null && !lotroGameMediator.getGameStatus().equals("Finished"))
                    visitor.runningPlayerGame(gameId);
            }
        }
    }

    public String getPlayerTable(String playerId) {
        for (Map.Entry<String, AwaitingTable> table : _awaitingTables.entrySet()) {
            if (table.getValue().hasPlayer(playerId))
                return table.getKey();
        }

        for (Map.Entry<String, String> runningTable : _runningTables.entrySet()) {
            String gameId = runningTable.getValue();
            LotroGameMediator lotroGameMediator = _lotroServer.getGameById(gameId);
            if (lotroGameMediator != null)
                if (lotroGameMediator.getPlayersPlaying().contains(playerId))
                    return runningTable.getKey();
        }

        return null;
    }

    private boolean isPlayerBusy(String playerId) {
        for (AwaitingTable awaitingTable : _awaitingTables.values())
            if (awaitingTable.hasPlayer(playerId))
                return true;

        for (String gameId : _runningTables.values()) {
            LotroGameMediator lotroGameMediator = _lotroServer.getGameById(gameId);
            if (lotroGameMediator != null && !lotroGameMediator.getGameStatus().equals("Finished") && lotroGameMediator.getPlayersPlaying().contains(playerId))
                return true;
        }
        return false;
    }

    @Override
    protected void cleanup() {
        // Remove finished games
        HashMap<String, String> copy = new HashMap<String, String>(_runningTables);
        for (Map.Entry<String, String> runningTable : copy.entrySet()) {
            if (_lotroServer.getGameById(runningTable.getValue()) == null)
                _runningTables.remove(runningTable.getKey());
        }

        long currentTime = System.currentTimeMillis();
        Map<String, Long> visitCopy = new LinkedHashMap<String, Long>(_lastVisitedPlayers);
        for (Map.Entry<String, Long> lastVisitedPlayer : visitCopy.entrySet()) {
            if (currentTime > lastVisitedPlayer.getValue() + _playerInactivityPeriod) {
                String playerId = lastVisitedPlayer.getKey();
                _lastVisitedPlayers.remove(playerId);
                leaveAwaitingTables(playerId);
            }
        }
    }
}
