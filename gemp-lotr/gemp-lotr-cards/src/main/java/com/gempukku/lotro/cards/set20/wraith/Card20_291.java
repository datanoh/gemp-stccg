package com.gempukku.lotro.cards.set20.wraith;

import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.AbstractActionProxy;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.RequiredTriggerAction;
import com.gempukku.lotro.logic.cardtype.AbstractAttachable;
import com.gempukku.lotro.logic.effects.AddBurdenEffect;
import com.gempukku.lotro.logic.effects.AddUntilStartOfPhaseActionProxyEffect;
import com.gempukku.lotro.logic.effects.ChooseActiveCardEffect;
import com.gempukku.lotro.logic.modifiers.AbstractExtraPlayCostModifier;
import com.gempukku.lotro.logic.modifiers.cost.DiscardFromHandExtraPlayCostModifier;
import com.gempukku.lotro.logic.timing.EffectResult;
import com.gempukku.lotro.logic.timing.PlayConditions;
import com.gempukku.lotro.logic.timing.TriggerConditions;

import java.util.Collections;
import java.util.List;

/**
 * 1
 * Fell Beast, Terror of the Skies
 * Ringwraith	Possession • Mount
 * 2
 * To play, discard 2 cards from hand.
 * Bearer must be a Nazgul.
 * When you play this mount, spot a companion with 5 or less resistance.
 * Until the regroup phase, each time that companion exerts or takes a wound, add a burden.
 */
public class Card20_291 extends AbstractAttachable {
    public Card20_291() {
        super(Side.SHADOW, CardType.POSSESSION, 1, Culture.WRAITH, PossessionClass.MOUNT, "Fell Beast", "Terror of the Skies", false);
    }

    @Override
    public Filterable getValidTargetFilter(String playerId, LotroGame game, PhysicalCard self) {
        return Race.NAZGUL;
    }

    @Override
    public int getStrength() {
        return 2;
    }

    @Override
    public boolean checkPlayRequirements(LotroGame game, PhysicalCard self) {
        return PlayConditions.canDiscardCardsFromHandToPlay(self, game, self.getOwner(), 2, Filters.any);
    }

    @Override
    public List<? extends AbstractExtraPlayCostModifier> getExtraCostToPlayModifiers(LotroGame game, PhysicalCard self) {
        return Collections.singletonList(
                new DiscardFromHandExtraPlayCostModifier(self, self, 2, null, Filters.any));
    }

    @Override
    public List<RequiredTriggerAction> getRequiredAfterTriggers(LotroGame game, EffectResult effectResult, final PhysicalCard self) {
        if (TriggerConditions.played(game, effectResult, self)
                && PlayConditions.canSpot(game, CardType.COMPANION, Filters.maxResistance(5))) {
            final RequiredTriggerAction action = new RequiredTriggerAction(self);
            action.appendEffect(
                    new ChooseActiveCardEffect(self, self.getOwner(), "Choose a companion", CardType.COMPANION, Filters.maxResistance(5), Filters.spottable) {
                        @Override
                        protected void cardSelected(LotroGame game, final PhysicalCard companion) {
                            action.appendEffect(
                                    new AddUntilStartOfPhaseActionProxyEffect(
                                            new AbstractActionProxy() {
                                                @Override
                                                public List<? extends RequiredTriggerAction> getRequiredAfterTriggers(LotroGame game, EffectResult effectResult) {
                                                    if (TriggerConditions.forEachExerted(game, effectResult, companion)
                                                            || TriggerConditions.forEachWounded(game, effectResult, companion)) {
                                                        RequiredTriggerAction subAction = new RequiredTriggerAction(self);
                                                        subAction.appendEffect(
                                                                new AddBurdenEffect(self.getOwner(), self, 1));
                                                        return Collections.singletonList(subAction);
                                                    }
                                                    return null;
                                                }
                                            }, Phase.REGROUP));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
