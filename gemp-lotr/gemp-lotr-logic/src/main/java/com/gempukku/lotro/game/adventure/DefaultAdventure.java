package com.gempukku.lotro.game.adventure;

import com.gempukku.lotro.cards.LotroCardBlueprint;
import com.gempukku.lotro.cards.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.game.DefaultGame;
import com.gempukku.lotro.game.actions.DefaultActionsEnvironment;
import com.gempukku.lotro.game.timing.PlayOrder;
import com.gempukku.lotro.game.actions.SystemQueueAction;
import com.gempukku.lotro.game.effects.PlaySiteEffect;
import com.gempukku.lotro.game.modifiers.ModifiersLogic;
import com.gempukku.lotro.game.timing.PlayerOrderFeedback;
import com.gempukku.lotro.game.timing.UnrespondableEffect;
import com.gempukku.lotro.game.timing.processes.GameProcess;
import com.gempukku.lotro.game.timing.processes.pregame.BiddingGameProcess;
import com.gempukku.lotro.game.timing.processes.turn.lotro.ShadowPhasesGameProcess;
import com.gempukku.lotro.game.timing.processes.turn.lotro.archery.FellowshipPlayerChoosesShadowPlayerToAssignDamageToGameProcess;
import com.gempukku.lotro.game.timing.processes.turn.lotro.assign.ShadowPlayersAssignTheirMinionsGameProcess;
import com.gempukku.lotro.game.timing.processes.turn.lotro.regroup.DiscardAllMinionsGameProcess;
import com.gempukku.lotro.game.timing.processes.turn.lotro.regroup.PlayerReconcilesGameProcess;
import com.gempukku.lotro.game.timing.processes.turn.lotro.regroup.ReturnFollowersToSupportGameProcess;
import com.gempukku.lotro.game.timing.processes.turn.lotro.regroup.ShadowPlayersReconcileGameProcess;
import com.gempukku.lotro.game.timing.rules.WinConditionRule;

import java.util.Set;

public class DefaultAdventure implements Adventure {
    @Override
    public void applyAdventureRules(DefaultGame game, DefaultActionsEnvironment actionsEnvironment, ModifiersLogic modifiersLogic) {
        new WinConditionRule(actionsEnvironment).applyRule();
    }

    @Override
    public void appendNextSiteAction(final SystemQueueAction action) {
        action.appendEffect(
                new UnrespondableEffect() {
                    @Override
                    protected void doPlayEffect(DefaultGame game) {
                        GameState gameState = game.getGameState();

                        final int nextSiteNumber = gameState.getCurrentSiteNumber() + 1;
                        PhysicalCard nextSite = gameState.getSite(nextSiteNumber);

                        if (nextSite == null) {
                            LotroCardBlueprint.Direction nextSiteDirection = gameState.getCurrentSite().getBlueprint().getSiteDirection();
                            String playerToPlaySite;
                            if (nextSiteDirection == LotroCardBlueprint.Direction.LEFT) {
                                PlayOrder order = gameState.getPlayerOrder().getClockwisePlayOrder(gameState.getCurrentPlayerId(), false);
                                order.getNextPlayer();
                                playerToPlaySite = order.getNextPlayer();
                            } else {
                                PlayOrder order = gameState.getPlayerOrder().getCounterClockwisePlayOrder(gameState.getCurrentPlayerId(), false);
                                order.getNextPlayer();
                                playerToPlaySite = order.getNextPlayer();
                            }

                            action.insertEffect(
                                    new PlaySiteEffect(action, playerToPlaySite, null, nextSiteNumber));
                        }
                    }
                });
    }

    @Override
    public GameProcess getAfterFellowshipArcheryGameProcess(int fellowshipArcheryTotal, GameProcess followingProcess) {
        return new FellowshipPlayerChoosesShadowPlayerToAssignDamageToGameProcess(fellowshipArcheryTotal, followingProcess);
    }

    @Override
    public GameProcess getAfterFellowshipAssignmentGameProcess(Set<PhysicalCard> leftoverMinions, GameProcess followingProcess) {
        return new ShadowPlayersAssignTheirMinionsGameProcess(followingProcess, leftoverMinions);
    }

    @Override
    public GameProcess getBeforeFellowshipChooseToMoveGameProcess(GameProcess followingProcess) {
        return new ShadowPlayersReconcileGameProcess(followingProcess);
    }

    @Override
    public GameProcess getPlayerStaysGameProcess(DefaultGame game, GameProcess followingProcess) {
        return new PlayerReconcilesGameProcess(game.getGameState().getCurrentPlayerId(),
                new ReturnFollowersToSupportGameProcess(
                        new DiscardAllMinionsGameProcess(followingProcess)));
    }

    @Override
    public GameProcess getAfterFellowshipPhaseGameProcess() {
        return new ShadowPhasesGameProcess();
    }

    @Override
    public GameProcess getStartingGameProcess(Set<String> players, PlayerOrderFeedback playerOrderFeedback) {
        return new BiddingGameProcess(players, playerOrderFeedback);
    }

    @Override
    public boolean isSolo() {
        return false;
    }
}
