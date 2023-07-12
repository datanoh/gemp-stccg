package com.gempukku.lotro.game.effects;

import com.gempukku.lotro.common.Filterable;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.cards.PhysicalCard;
import com.gempukku.lotro.game.DefaultGame;
import com.gempukku.lotro.game.actions.lotronly.CostToEffectAction;
import com.gempukku.lotro.game.actions.lotronly.SubAction;
import com.gempukku.lotro.game.actions.Action;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PutCardsFromDeckIntoHandDiscardRestEffect extends AbstractSubActionEffect {
    private final Action _action;
    private final PhysicalCard _source;
    private final String _playerId;
    private final Filterable[] _filters;
    private final Set<PhysicalCard> _cards;
    private int _drawn;
    private final int _maxCount;

    public PutCardsFromDeckIntoHandDiscardRestEffect(Action action, PhysicalCard source, String playerId, Collection<? extends PhysicalCard> cards, Filterable... filters) {
        this(action, source, playerId, cards, Integer.MAX_VALUE, filters);
    }

    public PutCardsFromDeckIntoHandDiscardRestEffect(Action action, PhysicalCard source, String playerId, Collection<? extends PhysicalCard> cards, int maxCount, Filterable... filters) {
        _action = action;
        _source = source;
        _playerId = playerId;
        _maxCount = maxCount;
        _filters = filters;
        _cards = new HashSet<>(cards);
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
        return true;
    }

    @Override
    public void playEffect(DefaultGame game) {
        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new ChooseAndPutNextCardFromDeckIntoHand(subAction, _cards));
        subAction.appendEffect(
                new UnrespondableEffect() {
                    @Override
                    protected void doPlayEffect(DefaultGame game) {
                        for (PhysicalCard card : _cards) {
                            subAction.appendEffect(
                                    new DiscardCardFromDeckEffect(card));
                        }
                    }
                });
        processSubAction(game, subAction);
    }

    private class ChooseAndPutNextCardFromDeckIntoHand extends UnrespondableEffect {
        private final CostToEffectAction _subAction;
        private final Set<PhysicalCard> _remainingCards;

        private ChooseAndPutNextCardFromDeckIntoHand(CostToEffectAction subAction, Set<PhysicalCard> remainingCards) {
            _subAction = subAction;
            _remainingCards = remainingCards;
        }

        @Override
        protected void doPlayEffect(DefaultGame game) {
            if (game.getModifiersQuerying().canDrawCardNoIncrement(game, _playerId)
                    && _drawn < _maxCount
                    && Filters.filter(_remainingCards, game, _filters).size() > 0) {
                _subAction.insertEffect(
                        new ChooseArbitraryCardsEffect(_playerId, "Put next card to put into your hand", _remainingCards, Filters.and(_filters), 1, 1) {
                            @Override
                            protected void cardsSelected(DefaultGame game, Collection<PhysicalCard> selectedCards) {
                                for (PhysicalCard selectedCard : selectedCards) {
                                    _drawn++;
                                    _remainingCards.remove(selectedCard);
                                    _subAction.insertEffect(
                                            new ChooseAndPutNextCardFromDeckIntoHand(_subAction, _remainingCards));
                                    _subAction.insertEffect(
                                            new PutCardFromDeckIntoHandEffect(selectedCard));
                                }
                            }
                        });
            }
        }
    }
}
