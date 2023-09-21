package com.gempukku.lotro.effects;

import com.gempukku.lotro.game.DefaultGame;
import com.gempukku.lotro.actions.PlayerReconcilesAction;

public class ReconcileHandEffect extends AbstractEffect {
    private final String _playerId;

    public ReconcileHandEffect(String playerId) {
        _playerId = playerId;
    }

    @Override
    public String getText(DefaultGame game) {
        return "Reconcile hand";
    }

    @Override
    public boolean isPlayableInFull(DefaultGame game) {
        return true;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(DefaultGame game) {
        PlayerReconcilesAction action = new PlayerReconcilesAction(game, _playerId);
        game.getActionsEnvironment().addActionToStack(action);
        return new FullEffectResult(true);
    }
}
