package com.gempukku.lotro.cards.set2.moria;

import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.OptionalTriggerAction;
import com.gempukku.lotro.logic.actions.RequiredTriggerAction;
import com.gempukku.lotro.logic.cardtype.AbstractAttachable;
import com.gempukku.lotro.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.lotro.logic.effects.AddUntilStartOfPhaseModifierEffect;
import com.gempukku.lotro.logic.effects.ChooseAndWoundCharactersEffect;
import com.gempukku.lotro.logic.effects.PlayoutDecisionEffect;
import com.gempukku.lotro.logic.modifiers.ShouldSkipPhaseModifier;
import com.gempukku.lotro.logic.timing.EffectResult;
import com.gempukku.lotro.logic.timing.TriggerConditions;

import java.util.Collections;
import java.util.List;

/**
 * Set: Mines of Moria
 * Side: Shadow
 * Culture: Moria
 * Twilight Cost: 1
 * Type: Possession
 * Strength: +2
 * Game Text: Bearer must be Cave Troll of Moria. The Free Peoples player may choose for the archery phase to
 * be skipped. Each time Cave Troll of Moria takes a wound during the archery phase, you may wound an archer companion.
 */
public class Card2_053 extends AbstractAttachable {
    public Card2_053() {
        super(Side.SHADOW, CardType.POSSESSION, 1, Culture.MORIA, null, "Cave Troll's Chain", null, true);
    }

    @Override
    public Filter getValidTargetFilter(String playerId, LotroGame game, PhysicalCard self) {
        return Filters.name("Cave Troll of Moria");
    }

    @Override
    public int getStrength() {
        return 2;
    }

    @Override
    public List<RequiredTriggerAction> getRequiredAfterTriggers(LotroGame game, EffectResult effectResult, final PhysicalCard self) {
        if (TriggerConditions.endOfPhase(game, effectResult, Phase.MANEUVER)) {
            final RequiredTriggerAction action = new RequiredTriggerAction(self);
            action.appendEffect(
                    new PlayoutDecisionEffect(game.getGameState().getCurrentPlayerId(),
                            new MultipleChoiceAwaitingDecision(1, "Would you like to skip Archery phase?", new String[]{"Yes", "No"}) {
                                @Override
                                protected void validDecisionMade(int index, String result) {
                                    if (result.equals("Yes")) {
                                        action.appendEffect(
                                                new AddUntilStartOfPhaseModifierEffect(
                                                        new ShouldSkipPhaseModifier(self, Phase.ARCHERY), Phase.REGROUP));
                                    }
                                }
                            }));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    public List<OptionalTriggerAction> getOptionalAfterTriggers(final String playerId, LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (TriggerConditions.forEachWounded(game, effectResult, Filters.hasAttached(self))
                && game.getGameState().getCurrentPhase() == Phase.ARCHERY) {
            final OptionalTriggerAction action = new OptionalTriggerAction(self);
            action.appendEffect(
                    new ChooseAndWoundCharactersEffect(action, playerId, 1, 1, Keyword.ARCHER, CardType.COMPANION));
            return Collections.singletonList(action);
        }
        return null;
    }
}
