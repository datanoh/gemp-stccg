package com.gempukku.lotro.hall;

import com.gempukku.lotro.AbstractServer;
import com.gempukku.lotro.chat.ChatServer;
import com.gempukku.lotro.db.CollectionDAO;
import com.gempukku.lotro.db.vo.League;
import com.gempukku.lotro.db.vo.LeagueSerie;
import com.gempukku.lotro.game.*;
import com.gempukku.lotro.game.formats.*;
import com.gempukku.lotro.league.LeagueService;
import com.gempukku.lotro.logic.vo.LotroDeck;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HallServer extends AbstractServer {
    private ChatServer _chatServer;
    private LeagueService _leagueService;
    private LotroCardBlueprintLibrary _library;
    private CollectionDAO _collectionDao;
    private LotroServer _lotroServer;

    private Map<String, String> _supportedFormatNames = new LinkedHashMap<String, String>();
    private Map<String, LotroFormat> _supportedFormats = new HashMap<String, LotroFormat>();
    private Map<String, String> _formatCollectionIds = new HashMap<String, String>();

    // TODO Reading/writing from/to these maps is done in multiple threads
    private Map<String, AwaitingTable> _awaitingTables = new ConcurrentHashMap<String, AwaitingTable>();
    private Map<String, String> _runningTables = new ConcurrentHashMap<String, String>();
    private Map<String, String> _runningTableFormatNames = new ConcurrentHashMap<String, String>();
    private int _nextTableId = 1;

    private final int _playerInactivityPeriod = 1000 * 10; // 10 seconds

    private Map<Player, Long> _lastVisitedPlayers = Collections.synchronizedMap(new LinkedHashMap<Player, Long>());

    private String _motd;

    public HallServer(LotroServer lotroServer, ChatServer chatServer, LeagueService leagueService, LotroCardBlueprintLibrary library, CollectionDAO collectionDao, boolean test) {
        _lotroServer = lotroServer;
        _chatServer = chatServer;
        _leagueService = leagueService;
        _library = library;
        _collectionDao = collectionDao;
        _chatServer.createChatRoom("Game Hall", 10);

        addFormat("fotr_block", "Fellowship block", "default", new FotRBlockFormat(library, false));
        addFormat("c_fotr_block", "Community Fellowship block", "default", new FotRBlockFormat(library, true));
        addFormat("ttt_block", "Two Towers block", "default", new TTTBlockFormat(library, false));
        addFormat("c_ttt_block", "Community Two Towers block", "default", new TTTBlockFormat(library, true));
        addFormat("towers_standard", "Towers Standard", "default", new TowersStandardFormat(library));
        addFormat("king_block", "King block", "default", new KingBlockFormat(library, false));
        addFormat("c_king_block", "Community King block", "default", new KingBlockFormat(library, true));
        addFormat("movie", "Movie block", "default", new MovieFormat(library));
        addFormat("war_block", "War of the Ring block", "default", new WarOfTheRingBlockFormat(library, false));
        addFormat("c_war_block", "Community War of the Ring block", "default", new WarOfTheRingBlockFormat(library, true));
        addFormat("open", "Open", "default", new OpenFormat(library));
        addFormat("expanded", "Expanded", "default", new ExpandedFormat(library));

//        addFormat("whatever", "Format for testing", "default", new FreeFormat(library));
    }

    public String getMOTD() {
        return _motd;
    }

    public void setMOTD(String motd) {
        _motd = motd;
    }

    private void addFormat(String formatCode, String formatName, String formatCollectionId, LotroFormat format) {
        _supportedFormatNames.put(formatCode, formatName);
        _formatCollectionIds.put(formatCode, formatCollectionId);
        _supportedFormats.put(formatCode, format);
    }

    public int getTablesCount() {
        return _awaitingTables.size() + _runningTables.size();
    }

    public Map<String, String> getSupportedFormatNames() {
        return Collections.unmodifiableMap(_supportedFormatNames);
    }

    public LotroFormat getSupportedFormat(String formatId) {
        return _supportedFormats.get(formatId);
    }

    public Set<League> getRunningLeagues() {
        return _leagueService.getActiveLeagues();
    }

    /**
     * @return If table created, otherwise <code>false</code> (if the user already is sitting at a table or playing).
     */
    public synchronized void createNewTable(String type, Player player, String deckName) throws HallException {
        League league = null;
        LeagueSerie leagueSerie = null;
        String collectionType = _formatCollectionIds.get(type);
        LotroFormat format = _supportedFormats.get(type);
        String formatName = null;
        if (format != null)
            formatName = _supportedFormatNames.get(type);

        if (format == null) {
            // Maybe it's a league format?
            league = _leagueService.getLeagueByType(type);
            if (league != null) {
                leagueSerie = _leagueService.getCurrentLeagueSerie(league);
                if (leagueSerie == null)
                    throw new HallException("There is no ongoing serie for that league");
                format = _supportedFormats.get(leagueSerie.getFormat());
                formatName = _supportedFormatNames.get(leagueSerie.getFormat());
                collectionType = league.getType();
            }
        }
        // It's not a normal format and also not a league one
        if (format == null)
            throw new HallException("This format is not supported: " + type);

        LotroDeck lotroDeck = validateUserAndDeck(format, player, deckName, collectionType);

        String tableId = String.valueOf(_nextTableId++);
        AwaitingTable table = new AwaitingTable(formatName, collectionType, format, league, leagueSerie);
        _awaitingTables.put(tableId, table);

        joinTableInternal(tableId, player.getName(), table, deckName, lotroDeck);
    }

    private LotroDeck validateUserAndDeck(LotroFormat format, Player player, String deckName, String collectionType) throws HallException {
        if (isPlayerBusy(player.getName()))
            throw new HallException("You can't play more than one game at a time or wait at more than one table");

        LotroDeck lotroDeck = _lotroServer.getParticipantDeck(player, deckName);
        if (lotroDeck == null)
            throw new HallException("You don't have a deck registered yet");

        try {
            format.validateDeck(player, lotroDeck);
        } catch (DeckInvalidException e) {
            throw new HallException("Your registered deck is not valid for this format: " + e.getMessage());
        }

        // Now check if player owns all the cards
        CardCollection collection = _collectionDao.getCollectionForPlayer(player, collectionType);

        Map<String, Integer> deckCardCounts = CollectionUtils.getTotalCardCountForDeck(lotroDeck);
        final Map<String, Integer> collectionCardCounts = collection.getAll();

        for (Map.Entry<String, Integer> cardCount : deckCardCounts.entrySet()) {
            final Integer collectionCount = collectionCardCounts.get(cardCount.getKey());
            if (collectionCount == null || collectionCount < cardCount.getValue()) {
                String cardName = _library.getLotroCardBlueprint(cardCount.getKey()).getName();
                int owned = (collectionCount == null) ? 0 : collectionCount;
                throw new HallException("You don't have the required cards in collection: " + cardName + " required " + cardCount.getValue() + ", owned " + owned);
            }
        }

        return lotroDeck;
    }

    /**
     * @return If table joined, otherwise <code>false</code> (if the user already is sitting at a table or playing).
     */
    public synchronized boolean joinTableAsPlayer(String tableId, Player player, String deckName) throws HallException {
        AwaitingTable awaitingTable = _awaitingTables.get(tableId);
        if (awaitingTable == null)
            throw new HallException("Table is already taken or was removed");

        LotroDeck lotroDeck = validateUserAndDeck(awaitingTable.getLotroFormat(), player, deckName, awaitingTable.getCollectionType());

        joinTableInternal(tableId, player.getName(), awaitingTable, deckName, lotroDeck);

        return true;
    }

    private void joinTableInternal(String tableId, String playerId, AwaitingTable awaitingTable, String deckName, LotroDeck lotroDeck) {
        boolean tableFull = awaitingTable.addPlayer(new LotroGameParticipant(playerId, deckName, lotroDeck));
        if (tableFull)
            createGame(tableId, awaitingTable);
    }

    private void createGame(String tableId, AwaitingTable awaitingTable) {
        Set<LotroGameParticipant> players = awaitingTable.getPlayers();
        LotroGameParticipant[] participants = players.toArray(new LotroGameParticipant[players.size()]);
        League league = awaitingTable.getLeague();
        String gameId = _lotroServer.createNewGame(awaitingTable.getLotroFormat(), awaitingTable.getFormatName(), participants, league != null);
        LotroGameMediator lotroGameMediator = _lotroServer.getGameById(gameId);
        if (league != null)
            _leagueService.leagueGameStarting(league, awaitingTable.getLeagueSerie(), lotroGameMediator);
        lotroGameMediator.startGame();
        _runningTables.put(tableId, gameId);
        _runningTableFormatNames.put(tableId, awaitingTable.getFormatName());
        _awaitingTables.remove(tableId);
    }

    public synchronized void leaveAwaitingTables(Player player) {
        Map<String, AwaitingTable> copy = new HashMap<String, AwaitingTable>(_awaitingTables);
        for (Map.Entry<String, AwaitingTable> table : copy.entrySet()) {
            if (table.getValue().hasPlayer(player.getName())) {
                boolean empty = table.getValue().removePlayer(player.getName());
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

    public void processTables(Player player, HallInfoVisitor visitor) {
        _lastVisitedPlayers.put(player, System.currentTimeMillis());
        visitor.playerIsWaiting(isPlayerWaiting(player.getName()));

        Map<String, AwaitingTable> copy = new HashMap<String, AwaitingTable>(_awaitingTables);
        for (Map.Entry<String, AwaitingTable> table : copy.entrySet())
            visitor.visitTable(table.getKey(), null, "Waiting", table.getValue().getFormatName(), table.getValue().getPlayerNames(), null);

        Map<String, String> runningCopy = new LinkedHashMap<String, String>(_runningTables);
        for (Map.Entry<String, String> runningGame : runningCopy.entrySet()) {
            LotroGameMediator lotroGameMediator = _lotroServer.getGameById(runningGame.getValue());
            if (lotroGameMediator != null)
                visitor.visitTable(runningGame.getKey(), runningGame.getValue(), lotroGameMediator.getGameStatus(), _runningTableFormatNames.get(runningGame.getKey()), lotroGameMediator.getPlayersPlaying(), lotroGameMediator.getWinner());
        }

        String playerTable = getNonFinishedPlayerTable(player.getName());
        if (playerTable != null) {
            String gameId = _runningTables.get(playerTable);
            if (gameId != null) {
                LotroGameMediator lotroGameMediator = _lotroServer.getGameById(gameId);
                if (lotroGameMediator != null && !lotroGameMediator.getGameStatus().equals("Finished"))
                    visitor.runningPlayerGame(gameId);
            }
        }
    }

    private String getNonFinishedPlayerTable(String playerId) {
        for (Map.Entry<String, AwaitingTable> table : _awaitingTables.entrySet()) {
            if (table.getValue().hasPlayer(playerId))
                return table.getKey();
        }

        for (Map.Entry<String, String> runningTable : _runningTables.entrySet()) {
            String gameId = runningTable.getValue();
            LotroGameMediator lotroGameMediator = _lotroServer.getGameById(gameId);
            if (lotroGameMediator != null && !lotroGameMediator.getGameStatus().equals("Finished"))
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
            if (_lotroServer.getGameById(runningTable.getValue()) == null) {
                _runningTables.remove(runningTable.getKey());
                _runningTableFormatNames.remove(runningTable.getKey());
            }
        }

        long currentTime = System.currentTimeMillis();
        Map<Player, Long> visitCopy = new LinkedHashMap<Player, Long>(_lastVisitedPlayers);
        for (Map.Entry<Player, Long> lastVisitedPlayer : visitCopy.entrySet()) {
            if (currentTime > lastVisitedPlayer.getValue() + _playerInactivityPeriod) {
                Player player = lastVisitedPlayer.getKey();
                _lastVisitedPlayers.remove(player);
                leaveAwaitingTables(player);
            }
        }
    }
}
