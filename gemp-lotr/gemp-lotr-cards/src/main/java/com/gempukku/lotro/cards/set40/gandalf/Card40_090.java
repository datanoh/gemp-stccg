package com.gempukku.lotro.cards.set40.gandalf;

import com.gempukku.lotro.common.CardType;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Phase;
import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.PlayEventAction;
import com.gempukku.lotro.logic.cardtype.AbstractEvent;
import com.gempukku.lotro.logic.effects.choose.ChooseAndPlayCardFromDeckEffect;

/**
 * Title: A Wizard is Never Late
 * Set: Second Edition
 * Side: Free
 * Culture: Gandalf
 * Twilight Cost: 1
 * Type: Event - Fellowship
 * Card Number: 1R90
 * Game Text: Play a [GANDALF] character from your draw deck.
 */
public class Card40_090 extends AbstractEvent {
    public Card40_090() {
        super(Side.FREE_PEOPLE, 1, Culture.GANDALF, "A Wizard Is Never Late", Phase.FELLOWSHIP);
    }

    @Override
    public PlayEventAction getPlayEventCardAction(final String playerId, final LotroGame game, PhysicalCard self) {
        PlayEventAction action = new PlayEventAction(self);
        action.appendEffect(
                new ChooseAndPlayCardFromDeckEffect(playerId, Filters.and(Culture.GANDALF, Filters.or(CardType.COMPANION, CardType.ALLY))));
        return action;
    }
}
