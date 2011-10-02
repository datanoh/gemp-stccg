package com.gempukku.lotro.logic.timing.results;

import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.logic.timing.EffectResult;

public class RemoveBurdenResult extends EffectResult {
    private PhysicalCard _source;

    public RemoveBurdenResult(PhysicalCard source) {
        super(EffectResult.Type.REMOVE_BURDEN);
        _source = source;
    }

    public PhysicalCard getSource() {
        return _source;
    }
}
