package com.gempukku.lotro.cards.set1.dwarven;

import com.gempukku.lotro.cards.AbstractEvent;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.effects.CardAffectsCardEffect;
import com.gempukku.lotro.cards.effects.ChooseAndExertCharacterEffect;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Phase;
import com.gempukku.lotro.common.Race;
import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.effects.ChooseActiveCardsEffect;
import com.gempukku.lotro.logic.effects.WoundCharacterEffect;

import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Free
 * Culture: Dwarven
 * Twilight Cost: 0
 * Type: Event
 * Game Text: Maneuver: Exert a Dwarf to wound 2 Orcs or to wound 1 Orc twice.
 */
public class Card1_019 extends AbstractEvent {
    public Card1_019() {
        super(Side.FREE_PEOPLE, Culture.DWARVEN, "Here Lies Balin, Son of Fundin", Phase.MANEUVER);
    }

    @Override
    public int getTwilightCost() {
        return 0;
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int twilightModifier) {
        return super.checkPlayRequirements(playerId, game, self, twilightModifier)
                && Filters.canSpot(game.getGameState(), game.getModifiersQuerying(), Filters.race(Race.DWARF), Filters.canExert());
    }

    @Override
    public PlayEventAction getPlayCardAction(final String playerId, LotroGame game, final PhysicalCard self, int twilightModifier) {
        final PlayEventAction action = new PlayEventAction(self);
        action.addCost(
                new ChooseAndExertCharacterEffect(action, playerId, "Choose Dwarf to exert", true, Filters.race(Race.DWARF), Filters.canExert()));
        action.addEffect(
                new ChooseActiveCardsEffect(playerId, "Choose Orc(s) to wound", 1, 2, Filters.race(Race.ORC)) {
                    @Override
                    protected void cardsSelected(List<PhysicalCard> cards) {
                        if (cards.size() == 2) {
                            action.addEffect(new CardAffectsCardEffect(self, cards.get(0)));
                            action.addEffect(new CardAffectsCardEffect(self, cards.get(1)));
                            action.addEffect(
                                    new WoundCharacterEffect(playerId,
                                            Filters.or(Filters.sameCard(cards.get(0)), Filters.sameCard(cards.get(1)))));
                        } else {
                            action.addEffect(new CardAffectsCardEffect(self, cards.get(0)));
                            action.addEffect(new WoundCharacterEffect(playerId, cards.get(0)));
                            action.addEffect(new CardAffectsCardEffect(self, cards.get(0)));
                            action.addEffect(new WoundCharacterEffect(playerId, cards.get(0)));
                        }
                    }
                }
        );
        return action;
    }
}
