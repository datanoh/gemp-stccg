package com.gempukku.lotro.effects;

import com.gempukku.lotro.cards.LotroPhysicalCard;
import com.gempukku.lotro.effects.choose.ChooseArbitraryCardsEffect;
import com.gempukku.lotro.game.DefaultGame;
import com.gempukku.lotro.actions.CostToEffectAction;
import com.gempukku.lotro.actions.SubAction;
import com.gempukku.lotro.actions.Action;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReorderTopCardsOfDeckEffect extends AbstractSubActionEffect {
    private final Action _action;
    private final String _playerId;
    private final String _deckId;
    private final int _count;

    public ReorderTopCardsOfDeckEffect(Action action, String playerId, int count) {
        _action = action;
        _playerId = playerId;
        _deckId = playerId;
        _count = count;
    }

    public ReorderTopCardsOfDeckEffect(Action action, String playerId, String deckId, int count) {
        _action = action;
        _playerId = playerId;
        _deckId = deckId;
        _count = count;
    }

    @Override
    public String getText(DefaultGame game) {
        return null;
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public boolean isPlayableInFull(DefaultGame game) {
        return game.getGameState().getDeck(_deckId).size() >= _count;
    }

    @Override
    public void playEffect(DefaultGame game) {
        final List<? extends LotroPhysicalCard> deck = game.getGameState().getDeck(_deckId);
        int count = Math.min(deck.size(), _count);
        Set<LotroPhysicalCard> cards = new HashSet<>(deck.subList(0, count));

        game.getGameState().sendMessage(_playerId + " reorders top " + count + " cards of draw deck");

        SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new ChooseAndPutNextCardFromDeckOnTopOfDeck(subAction, cards));
        processSubAction(game, subAction);
    }

    private class ChooseAndPutNextCardFromDeckOnTopOfDeck extends ChooseArbitraryCardsEffect {
        private final Collection<LotroPhysicalCard> _remainingCards;
        private final CostToEffectAction _subAction;

        public ChooseAndPutNextCardFromDeckOnTopOfDeck(CostToEffectAction subAction, Collection<LotroPhysicalCard> remainingCards) {
            super(_playerId, "Choose a card to put on top of the deck", remainingCards, 1, 1);
            _subAction = subAction;
            _remainingCards = remainingCards;
        }

        @Override
        protected void cardsSelected(DefaultGame game, Collection<LotroPhysicalCard> selectedCards) {
            for (LotroPhysicalCard selectedCard : selectedCards) {
                _subAction.appendEffect(
                        new PutCardFromDeckOnTopOfDeckEffect(_playerId, selectedCard, false));
                _remainingCards.remove(selectedCard);
                if (_remainingCards.size() > 0)
                    _subAction.appendEffect(
                            new ChooseAndPutNextCardFromDeckOnTopOfDeck(_subAction, _remainingCards));
            }
        }
    }

}
