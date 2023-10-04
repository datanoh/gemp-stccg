package com.gempukku.lotro.effects.choose;

import com.gempukku.lotro.actions.Action;
import com.gempukku.lotro.actions.SubAction;
import com.gempukku.lotro.cards.PhysicalCard;
import com.gempukku.lotro.common.Filterable;
import com.gempukku.lotro.common.Zone;
import com.gempukku.lotro.effects.PutCardFromZoneIntoHandEffect;
import com.gempukku.lotro.game.DefaultGame;

import java.util.Collection;

public class ChooseAndPutCardFromDeckIntoHandEffect extends ChooseCardsFromDeckEffect {
    private final Action _action;

    public ChooseAndPutCardFromDeckIntoHandEffect(Action action, String playerId, int minimum, int maximum, Filterable... filters) {
        super(playerId, playerId, minimum, maximum, filters);
        _action = action;
    }

    public ChooseAndPutCardFromDeckIntoHandEffect(Action action, String playerId, String deckId, int minimum, int maximum, Filterable... filters) {
        super(playerId, deckId, minimum, maximum, filters);
        _action = action;
    }

    @Override
    protected void cardsSelected(DefaultGame game, Collection<PhysicalCard> cards) {
        if (cards.size() > 0) {
            SubAction subAction = new SubAction(_action);
            for (PhysicalCard card : cards)
                subAction.appendEffect(new PutCardFromZoneIntoHandEffect(card, Zone.DRAW_DECK, true));
            game.getActionsEnvironment().addActionToStack(subAction);
        }
    }
}
