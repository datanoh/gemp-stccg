package com.gempukku.lotro.cards.set2.moria;

import com.gempukku.lotro.common.CardType;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.RequiredTriggerAction;
import com.gempukku.lotro.logic.cardtype.AbstractAttachable;
import com.gempukku.lotro.logic.effects.ExertCharactersEffect;
import com.gempukku.lotro.logic.timing.EffectResult;
import com.gempukku.lotro.logic.timing.TriggerConditions;

import java.util.Collections;
import java.util.List;

/**
 * Set: Mines of Moria
 * Side: Shadow
 * Culture: Moria
 * Twilight Cost: 1
 * Type: Condition
 * Strength: -2
 * Game Text: To play, spot The Balrog. Plays on Gandalf. Each time the fellowship moves, Gandalf must exert.
 */
public class Card2_054 extends AbstractAttachable {
    public Card2_054() {
        super(Side.SHADOW, CardType.CONDITION, 1, Culture.MORIA, null, "Dark Fire");
    }

    @Override
    public boolean checkPlayRequirements(LotroGame game, PhysicalCard self) {
        return Filters.canSpot(game, Filters.balrog);
    }

    @Override
    public Filter getValidTargetFilter(String playerId, LotroGame game, PhysicalCard self) {
        return Filters.gandalf;
    }

    @Override
    public int getStrength() {
        return -2;
    }

    @Override
    public List<RequiredTriggerAction> getRequiredAfterTriggers(LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (TriggerConditions.moves(game, effectResult)) {
            RequiredTriggerAction action = new RequiredTriggerAction(self);
            action.appendEffect(
                    new ExertCharactersEffect(action, self, self.getAttachedTo()));
            return Collections.singletonList(action);
        }
        return null;
    }
}
