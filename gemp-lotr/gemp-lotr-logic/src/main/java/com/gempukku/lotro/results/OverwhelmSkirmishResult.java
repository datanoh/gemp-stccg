package com.gempukku.lotro.results;

import com.gempukku.lotro.cards.PhysicalCard;
import com.gempukku.lotro.effects.EffectResult;

import java.util.Set;

public class OverwhelmSkirmishResult extends SkirmishResult {
    public OverwhelmSkirmishResult(Set<PhysicalCard> winners, Set<PhysicalCard> losers, Set<PhysicalCard> removedFromSkirmish) {
        super(EffectResult.Type.SKIRMISH_FINISHED_WITH_OVERWHELM, winners, losers, removedFromSkirmish);
    }
}
