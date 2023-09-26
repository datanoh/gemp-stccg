package com.gempukku.lotro.effects;

import com.gempukku.lotro.actions.CostToEffectAction;
import com.gempukku.lotro.cards.LotroPhysicalCard;
import com.gempukku.lotro.effects.choose.ChooseAndDiscardCardsFromHandEffect;
import com.gempukku.lotro.game.TribblesGame;

public class ActivateDiscardTribblePowerEffect extends ActivateTribblePowerEffect {
    public ActivateDiscardTribblePowerEffect(CostToEffectAction action, LotroPhysicalCard source) {
        super(action, source);
    }

    @Override
    protected FullEffectResult playEffectReturningResult(TribblesGame game) {
        new ChooseAndDiscardCardsFromHandEffect(_action, _source.getOwner(),false,1).playEffect(game);
        game.getActionsEnvironment().emitEffectResult(_result);
        return new FullEffectResult(true);
    }
}