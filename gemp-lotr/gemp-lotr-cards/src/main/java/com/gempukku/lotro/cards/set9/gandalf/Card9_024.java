package com.gempukku.lotro.cards.set9.gandalf;

import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.cardtype.AbstractAttachable;
import com.gempukku.lotro.logic.effects.AddUntilEndOfPhaseModifierEffect;
import com.gempukku.lotro.logic.effects.ExertCharactersEffect;
import com.gempukku.lotro.logic.modifiers.KeywordModifier;
import com.gempukku.lotro.logic.timing.PlayConditions;

import java.util.Collections;
import java.util.List;

/**
 * Set: Reflections
 * Side: Free
 * Culture: Gandalf
 * Twilight Cost: 1
 * Type: Condition
 * Vitality: +1
 * Game Text: To play, spot an Ent. Bearer must be a companion. Limit 1 per bearer. Skirmish: Exert bearer to make him
 * or her damage +1.
 */
public class Card9_024 extends AbstractAttachable {
    public Card9_024() {
        super(Side.FREE_PEOPLE, CardType.CONDITION, 1, Culture.GANDALF, null, "Ent Draught");
    }

    @Override
    public boolean checkPlayRequirements(LotroGame game, PhysicalCard self) {
        return (Filters.countActive(game, Race.ENT) + game.getModifiersQuerying().getSpotBonus(game, Race.ENT)) >= 1;
    }

    @Override
    public Filterable getValidTargetFilter(String playerId, LotroGame game, PhysicalCard self) {
        return Filters.and(CardType.COMPANION, Filters.not(Filters.hasAttached(Filters.name(getTitle()))));
    }

    @Override
    public int getVitality() {
        return 1;
    }

    @Override
    public List<? extends ActivateCardAction> getPhaseActionsInPlay(String playerId, LotroGame game, PhysicalCard self) {
        if (PlayConditions.canUseFPCardDuringPhase(game, Phase.SKIRMISH, self)
                && PlayConditions.canExert(self, game, Filters.hasAttached(self))) {
            ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(
                    new ExertCharactersEffect(action, self, self.getAttachedTo()));
            action.appendEffect(
                    new AddUntilEndOfPhaseModifierEffect(
                            new KeywordModifier(self, self.getAttachedTo(), Keyword.DAMAGE, 1)));
            return Collections.singletonList(action);
        }
        return null;
    }
}
