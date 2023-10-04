package com.gempukku.lotro.modifiers.lotronly;

import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.cards.PhysicalCard;
import com.gempukku.lotro.game.DefaultGame;
import com.gempukku.lotro.modifiers.AbstractModifier;
import com.gempukku.lotro.modifiers.ModifierEffect;
import com.gempukku.lotro.condition.Condition;
import com.gempukku.lotro.rules.lotronly.LotroGameUtils;

public class FPCulturesSideSpotCountModifier extends AbstractModifier {
    private final Side _sidePlayer;
    private final int _modifier;

    public FPCulturesSideSpotCountModifier(PhysicalCard source, Side sidePlayer, int modifier) {
        this(source, sidePlayer, null, modifier);
    }

    public FPCulturesSideSpotCountModifier(PhysicalCard source, Side sidePlayer, Condition condition, int modifier) {
        super(source, "Modifies FP culture count", null, condition, ModifierEffect.SPOT_MODIFIER);
        _sidePlayer = sidePlayer;
        _modifier = modifier;
    }

    @Override
    public int getFPCulturesSpotCountModifier(DefaultGame game, String playerId) {
        if (LotroGameUtils.isSide(game, _sidePlayer, playerId))
            return _modifier;
        return 0;
    }
}
