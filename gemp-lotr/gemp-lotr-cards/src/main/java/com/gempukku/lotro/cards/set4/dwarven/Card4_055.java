package com.gempukku.lotro.cards.set4.dwarven;

import com.gempukku.lotro.cards.AbstractEvent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.effects.ChooseAndExertCharactersEffect;
import com.gempukku.lotro.cards.effects.ShuffleDeckEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.effects.ChooseActiveCardEffect;

/**
 * Set: The Two Towers
 * Side: Free
 * Culture: Dwarven
 * Twilight Cost: 2
 * Type: Event
 * Game Text: Regroup: Exert a Dwarf to shuffle a [DWARVEN] condition (and all cards stacked on it) into your draw deck.
 * Exert a minion for each card shuffled into your draw deck.
 */
public class Card4_055 extends AbstractEvent {
    public Card4_055() {
        super(Side.FREE_PEOPLE, Culture.DWARVEN, "Restless Axe", Phase.REGROUP);
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int twilightModifier) {
        return super.checkPlayRequirements(playerId, game, self, twilightModifier)
                && PlayConditions.canExert(self, game.getGameState(), game.getModifiersQuerying(), Filters.race(Race.DWARF));
    }

    @Override
    public int getTwilightCost() {
        return 2;
    }

    @Override
    public PlayEventAction getPlayCardAction(final String playerId, final LotroGame game, PhysicalCard self, int twilightModifier) {
        final PlayEventAction action = new PlayEventAction(self);
        action.appendCost(
                new ChooseAndExertCharactersEffect(action, playerId, 1, 1, Filters.race(Race.DWARF)));
        action.appendEffect(
                new ChooseActiveCardEffect(self, playerId, "Choose a DWARVEN condition", Filters.culture(Culture.DWARVEN), Filters.type(CardType.CONDITION)) {
                    @Override
                    protected void cardSelected(PhysicalCard card) {
                        int count = 0;
                        for (PhysicalCard stackedCard : game.getGameState().getStackedCards(card)) {
                            game.getGameState().removeCardFromZone(stackedCard);
                            game.getGameState().putCardOnBottomOfDeck(stackedCard);
                            count++;
                        }
                        game.getGameState().removeCardFromZone(card);
                        game.getGameState().putCardOnBottomOfDeck(card);
                        count++;

                        action.appendEffect(
                                new ShuffleDeckEffect(playerId));
                        action.appendEffect(
                                new ChooseAndExertCharactersEffect(action, playerId, count, count, Filters.type(CardType.MINION)));
                    }
                });
        return action;
    }
}
