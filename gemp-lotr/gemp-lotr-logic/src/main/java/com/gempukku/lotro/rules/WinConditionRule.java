package com.gempukku.lotro.rules;

import com.gempukku.lotro.actions.AbstractActionProxy;
import com.gempukku.lotro.actions.RequiredTriggerAction;
import com.gempukku.lotro.common.CardType;
import com.gempukku.lotro.common.Phase;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.DefaultGame;
import com.gempukku.lotro.actions.DefaultActionsEnvironment;
import com.gempukku.lotro.modifiers.ModifierFlag;
import com.gempukku.lotro.effects.EffectResult;
import com.gempukku.lotro.results.ReconcileResult;
import com.gempukku.lotro.rules.lotronly.LotroGameUtils;

import java.util.List;

public class WinConditionRule {
    private final DefaultActionsEnvironment _actionsEnvironment;

    public WinConditionRule(DefaultActionsEnvironment actionsEnvironment) {
        _actionsEnvironment = actionsEnvironment;
    }

    public void applyRule() {
        _actionsEnvironment.addAlwaysOnActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<? extends RequiredTriggerAction> getRequiredAfterTriggers(DefaultGame game, EffectResult effectResults) {
                        if (game.getGameState().getCurrentPhase() == Phase.REGROUP
                                && game.getGameState().getCurrentSiteNumber() == 9) {
                            if (isWinAtReconcile(game)) {
                                if (effectResults.getType() == EffectResult.Type.RECONCILE
                                        && !((ReconcileResult) effectResults).getPlayerId().equals(game.getGameState().getCurrentPlayerId())) {
                                    game.playerWon(game.getGameState().getCurrentPlayerId(), "Surviving to end of Regroup phase on site 9");
                                }
                            } else if (effectResults.getType() == EffectResult.Type.START_OF_PHASE) {
                                game.playerWon(game.getGameState().getCurrentPlayerId(), "Surviving to Regroup phase on site 9");
                            }
                        } else if (game.getFormat().winOnControlling5Sites()
                                && effectResults.getType() == EffectResult.Type.TAKE_CONTROL_OF_SITE) {
                            for (String opponent : LotroGameUtils.getShadowPlayers(game)) {
                                if (Filters.countActive(game, CardType.SITE, Filters.siteControlled(opponent)) >= 5)
                                    game.playerWon(opponent, "Controls 5 sites");
                            }
                        }
                        return null;
                    }
                });
    }

    private boolean isWinAtReconcile(DefaultGame game) {
        return (game.getModifiersQuerying().hasFlagActive(game, ModifierFlag.WIN_CHECK_AFTER_SHADOW_RECONCILE)
                || game.getFormat().winWhenShadowReconciles());
    }
}
