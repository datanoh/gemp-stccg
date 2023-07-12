package com.gempukku.lotro.cards.build;

import com.gempukku.lotro.game.actions.lotronly.CostToEffectAction;

public interface ActionSource {
    boolean requiresRanger();

    boolean isValid(ActionContext actionContext);

    void createAction(CostToEffectAction action, ActionContext actionContext);
}
