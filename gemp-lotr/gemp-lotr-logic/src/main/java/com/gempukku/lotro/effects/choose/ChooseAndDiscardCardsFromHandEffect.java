package com.gempukku.lotro.effects.choose;

import com.gempukku.lotro.common.Filterable;
import com.gempukku.lotro.decisions.CardsSelectionDecision;
import com.gempukku.lotro.decisions.DecisionResultInvalidException;
import com.gempukku.lotro.effects.AbstractSubActionEffect;
import com.gempukku.lotro.effects.DiscardCardsFromHandEffect;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.cards.LotroPhysicalCard;
import com.gempukku.lotro.game.DefaultGame;
import com.gempukku.lotro.actions.SubAction;
import com.gempukku.lotro.evaluator.ConstantEvaluator;
import com.gempukku.lotro.evaluator.Evaluator;
import com.gempukku.lotro.actions.Action;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class ChooseAndDiscardCardsFromHandEffect extends AbstractSubActionEffect {
    private final Action _action;
    private final String _playerId;
    private final boolean _forced;
    private final Evaluator<DefaultGame> _minimum;
    private final Evaluator<DefaultGame> _maximum;
    private final Filterable[] _filter;
    private String _text = "Choose cards to discard";

    public ChooseAndDiscardCardsFromHandEffect(Action action, String playerId, boolean forced,
                                               Evaluator<DefaultGame> minimum, Evaluator<DefaultGame> maximum,
                                               Filterable... filters) {
        _action = action;
        _playerId = playerId;
        _forced = forced;
        _minimum = minimum;
        _maximum = maximum;
        _filter = filters;
    }

    public ChooseAndDiscardCardsFromHandEffect(Action action, String playerId, boolean forced, int minimum, int maximum, Filterable... filters) {
        this(action, playerId, forced, new ConstantEvaluator(minimum), new ConstantEvaluator(maximum), filters);
    }

    public ChooseAndDiscardCardsFromHandEffect(Action action, String playerId, boolean forced, int count, Filterable... filters) {
        this(action, playerId, forced, count, count, filters);
    }

    public void setText(String text) {
        _text = text;
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public String getText(DefaultGame game) {
        return null;
    }

    @Override
    public boolean isPlayableInFull(DefaultGame game) {
        return Filters.filter(game.getGameState().getHand(_playerId), game, _filter).size()
                >= _minimum.evaluateExpression(game, null);
    }

    @Override
    public void playEffect(final DefaultGame game) {
        if (_forced && !game.getModifiersQuerying().canDiscardCardsFromHand(game, _playerId, _action.getActionSource()))
            return;

        Collection<LotroPhysicalCard> hand = Filters.filter(game.getGameState().getHand(_playerId), game, _filter);
        int maximum = Math.min(_maximum.evaluateExpression(game, null), hand.size());

        int minimum = _minimum.evaluateExpression(game, null);
        if (maximum == 0) {
            cardsBeingDiscardedCallback(Collections.emptySet());
        } else if (hand.size() <= minimum) {
            SubAction subAction = new SubAction(_action);
            subAction.appendEffect(new DiscardCardsFromHandEffect(_action.getActionSource(), _playerId, hand, _forced));
            processSubAction(game, subAction);
            cardsBeingDiscardedCallback(hand);
        } else {
            game.getUserFeedback().sendAwaitingDecision(_playerId,
                    new CardsSelectionDecision(1, _text, hand, minimum, maximum) {
                        @Override
                        public void decisionMade(String result) throws DecisionResultInvalidException {
                            Set<LotroPhysicalCard> cards = getSelectedCardsByResponse(result);
                            SubAction subAction = new SubAction(_action);
                            subAction.appendEffect(new DiscardCardsFromHandEffect(_action.getActionSource(), _playerId, cards, _forced));
                            processSubAction(game, subAction);
                            cardsBeingDiscardedCallback(cards);
                        }
                    });
        }
    }

    protected void cardsBeingDiscardedCallback(Collection<LotroPhysicalCard> cardsBeingDiscarded) {
    }
}
