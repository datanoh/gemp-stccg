package com.gempukku.lotro.rules;

import com.gempukku.lotro.actions.AbstractActionProxy;
import com.gempukku.lotro.common.CardType;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.DefaultGame;
import com.gempukku.lotro.cards.lotronly.LotroPhysicalCard;
import com.gempukku.lotro.actions.DefaultActionsEnvironment;
import com.gempukku.lotro.actions.lotronly.RequiredTriggerAction;
import com.gempukku.lotro.effects.Effect;
import com.gempukku.lotro.effects.EffectResult;

import java.util.LinkedList;
import java.util.List;

public class RequiredTriggersRule {
    private final DefaultActionsEnvironment actionsEnvironment;

    public RequiredTriggersRule(DefaultActionsEnvironment actionsEnvironment) {
        this.actionsEnvironment = actionsEnvironment;
    }

    public void applyRule() {
        actionsEnvironment.addAlwaysOnActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<? extends RequiredTriggerAction> getRequiredBeforeTriggers(DefaultGame game, Effect effect) {
                        List<RequiredTriggerAction> result = new LinkedList<>();
                        for (LotroPhysicalCard activatableCard : Filters.filter(game.getGameState().getInPlay(), game, getActivatableCardsFilter())) {
                            if (!game.getModifiersQuerying().hasTextRemoved(game, activatableCard)) {
                                final List<? extends RequiredTriggerAction> actions = activatableCard.getBlueprint().getRequiredBeforeTriggers(game, effect, activatableCard);
                                if (actions != null)
                                    result.addAll(actions);
                            }
                        }

                        return result;
                    }

                    @Override
                    public List<? extends RequiredTriggerAction> getRequiredAfterTriggers(DefaultGame game, EffectResult effectResult) {
                        List<RequiredTriggerAction> result = new LinkedList<>();
                        for (LotroPhysicalCard activatableCard : Filters.filter(game.getGameState().getInPlay(), game, getActivatableCardsFilter())) {
                            if (!game.getModifiersQuerying().hasTextRemoved(game, activatableCard)) {
                                final List<? extends RequiredTriggerAction> actions = activatableCard.getBlueprint().getRequiredAfterTriggers(game, effectResult, activatableCard);
                                if (actions != null)
                                    result.addAll(actions);
                            }
                        }

                        return result;
                    }
                }
        );
    }

    private Filter getActivatableCardsFilter() {
        return Filters.or(
                Filters.and(CardType.SITE,
                        (Filter) (game, physicalCard) -> {
                            if (game.getGameState().getCurrentPhase().isRealPhase())
                                return Filters.currentSite.accepts(game, physicalCard);
                            return false;
                        }),
                Filters.and(Filters.not(CardType.SITE), Filters.active));
    }
}
