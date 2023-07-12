package com.gempukku.lotro.game.timing.results;

import com.gempukku.lotro.cards.PhysicalCard;
import com.gempukku.lotro.game.actions.Action;
import com.gempukku.lotro.game.effects.EffectResult;

public class ExertResult extends EffectResult {
    private final Action _action;
    private final PhysicalCard _card;
    private final boolean _forToil;

    public ExertResult(Action action, PhysicalCard card, boolean forToil) {
        super(Type.FOR_EACH_EXERTED);
        _action = action;
        _card = card;
        _forToil = forToil;
    }

    public PhysicalCard getExertedCard() {
        return _card;
    }

    public Action getAction() {
        return _action;
    }

    public boolean isForToil() {
        return _forToil;
    }
}
