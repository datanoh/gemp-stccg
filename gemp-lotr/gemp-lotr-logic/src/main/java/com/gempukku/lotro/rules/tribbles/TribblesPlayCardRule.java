package com.gempukku.lotro.rules.tribbles;

import com.gempukku.lotro.actions.AbstractActionProxy;
import com.gempukku.lotro.actions.Action;
import com.gempukku.lotro.actions.DefaultActionsEnvironment;
import com.gempukku.lotro.actions.TribblesPlayPermanentAction;
import com.gempukku.lotro.cards.LotroPhysicalCard;
import com.gempukku.lotro.common.Zone;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.DefaultGame;
import com.gempukku.lotro.rules.GameUtils;

import java.util.LinkedList;
import java.util.List;

public class TribblesPlayCardRule {
    private final DefaultActionsEnvironment actionsEnvironment;

    public TribblesPlayCardRule(DefaultActionsEnvironment actionsEnvironment) {
        this.actionsEnvironment = actionsEnvironment;
    }

    public void applyRule() {
        actionsEnvironment.addAlwaysOnActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<? extends Action> getPhaseActions(String playerId, DefaultGame game) {
                        if (GameUtils.isCurrentPlayer(game, playerId)) {
                            List<Action> result = new LinkedList<>();
                            for (LotroPhysicalCard card : Filters.filter(game.getGameState().getHand(playerId), game)) {
                                if (game.checkPlayRequirements(card)) {
                                    result.add(new TribblesPlayPermanentAction(card, Zone.PLAY_PILE));
                                }
                            }
                            return result;
                        }
                        return null;
                    }
                }
        );
    }
}
