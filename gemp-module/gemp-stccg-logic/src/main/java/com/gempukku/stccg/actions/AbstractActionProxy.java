package com.gempukku.stccg.actions;

import java.util.List;

public abstract class AbstractActionProxy implements ActionProxy {
    @Override
    public List<? extends Action> getPhaseActions(String playerId) {
        return null;
    }

    @Override
    public List<? extends Action> getOptionalBeforeActions(String playerId, Effect effect) { return null; }

    @Override
    public List<? extends Action> getOptionalAfterActions(String playerId, EffectResult effectResult) {
        return null;
    }

    @Override
    public List<? extends Action> getRequiredBeforeTriggers(Effect effect) { return null; }

    @Override
    public List<? extends Action> getRequiredAfterTriggers(EffectResult effectResult) {
        return null;
    }

    @Override
    public List<? extends Action> getOptionalBeforeTriggerActions(String playerId, Effect effect) {
        return null;
    }
}
