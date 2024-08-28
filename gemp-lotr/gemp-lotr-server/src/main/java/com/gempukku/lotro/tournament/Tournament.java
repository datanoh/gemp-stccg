package com.gempukku.lotro.tournament;

import com.gempukku.lotro.collection.CollectionsManager;
import com.gempukku.lotro.collection.DeckRenderer;
import com.gempukku.lotro.competitive.PlayerStanding;
import com.gempukku.lotro.db.vo.CollectionType;
import com.gempukku.lotro.draft.Draft;
import com.gempukku.lotro.game.CardNotFoundException;
import com.gempukku.lotro.logic.vo.LotroDeck;
import com.gempukku.lotro.packs.ProductLibrary;
import com.gempukku.lotro.tournament.action.TournamentProcessAction;

import java.util.List;

public interface Tournament {
    public enum Stage {
        DRAFT("Drafting"),
        DECK_BUILDING("Deck building"),
        PLAYING_GAMES("Playing games"),
        FINISHED("Finished");

        private final String _humanReadable;

        Stage(String humanReadable) {
            _humanReadable = humanReadable;
        }

        public String getHumanReadable() {
            return _humanReadable;
        }

        public static Stage parseStage(String name) {
            String nameCaps = name.toUpperCase().replace(' ', '_').replace('-', '_');
            String nameLower = name.toLowerCase();

            for (Stage stage : values()) {
                if (stage.getHumanReadable().toLowerCase().equals(nameLower)
                        || stage.toString().equals(nameCaps))
                    return stage;
            }
            return null;
        }
    }

    public static TournamentPrizes getTournamentPrizes(ProductLibrary productLibrary, String prizesScheme) {
        if (prizesScheme == null || prizesScheme.equals("none"))
            return new NoPrizes();

        return new DailyTournamentPrizes(prizesScheme, productLibrary);
    }

    public static PairingMechanism getPairingMechanism(String pairingType) {
        if (pairingType.equals("singleElimination"))
            return new SingleEliminationPairing("singleElimination");
        if (pairingType.equals("swiss"))
            return new SwissPairingMechanism("swiss");
        if (pairingType.equals("swiss-3"))
            return new SwissPairingMechanism("swiss-3", 3);
        if (pairingType.equals("wc-swiss"))
            return new ChampionshipSwissPairingMechanism("wc-swiss", 8);

        return null;
    }

    public String getTournamentId();
    public String getFormat();
    public CollectionType getCollectionType();
    public String getTournamentName();
    public String getPlayOffSystem();

    public Stage getTournamentStage();
    public int getCurrentRound();
    public int getPlayersInCompetitionCount();
    public String getPlayerList();

    public List<TournamentProcessAction> advanceTournament(CollectionsManager collectionsManager);

    public void reportGameFinished(String winner, String loser);

    public void playerChosenCard(String playerName, String cardId);
    public void playerSubmittedDeck(String player, LotroDeck deck);
    public LotroDeck getPlayerDeck(String player);
    public boolean dropPlayer(String player);

    public Draft getDraft();

    public List<PlayerStanding> getCurrentStandings();

    public boolean isPlayerInCompetition(String player);

    public String produceReport(DeckRenderer renderer) throws CardNotFoundException;
}
