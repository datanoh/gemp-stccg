package com.gempukku.lotro.effects;

import com.gempukku.lotro.cards.LotroPhysicalCard;
import com.gempukku.lotro.common.Zone;
import com.gempukku.lotro.gamestate.GameState;
import com.gempukku.lotro.game.DefaultGame;
import com.gempukku.lotro.rules.GameUtils;
import com.gempukku.lotro.results.DiscardCardsFromPlayResult;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PutCardFromPlayOnBottomOfDeckEffect extends AbstractEffect {
    private final LotroPhysicalCard _physicalCard;

    public PutCardFromPlayOnBottomOfDeckEffect(LotroPhysicalCard physicalCard) {
        _physicalCard = physicalCard;
    }

    @Override
    public boolean isPlayableInFull(DefaultGame game) {
        return _physicalCard.getZone().isInPlay();
    }

    @Override
    protected FullEffectResult playEffectReturningResult(DefaultGame game) {
        if (isPlayableInFull(game)) {
            Set<LotroPhysicalCard> discardedCards = new HashSet<>();
            Set<LotroPhysicalCard> toGoToDiscardCards = new HashSet<>();

            DiscardUtils.cardsToChangeZones(game, Collections.singleton(_physicalCard), discardedCards, toGoToDiscardCards);

            GameState gameState = game.getGameState();

            Set<LotroPhysicalCard> removeFromPlay = new HashSet<>(toGoToDiscardCards);
            removeFromPlay.add(_physicalCard);

            gameState.removeCardsFromZone(_physicalCard.getOwner(), removeFromPlay);

            gameState.putCardOnBottomOfDeck(_physicalCard);
            for (LotroPhysicalCard discardedCard : discardedCards) {
                game.getActionsEnvironment().emitEffectResult(new DiscardCardsFromPlayResult(null, null, discardedCard));
            }
            for (LotroPhysicalCard toGoToDiscardCard : toGoToDiscardCards)
                gameState.addCardToZone(game, toGoToDiscardCard, Zone.DISCARD);

            gameState.sendMessage(_physicalCard.getOwner() + " puts " + GameUtils.getCardLink(_physicalCard) + " from play on the bottom of deck");

            return new FullEffectResult(true);
        }
        return new FullEffectResult(false);
    }

    @Override
    public String getText(DefaultGame game) {
        return "Put " + GameUtils.getFullName(_physicalCard) + " from play on bottom of deck";
    }

}