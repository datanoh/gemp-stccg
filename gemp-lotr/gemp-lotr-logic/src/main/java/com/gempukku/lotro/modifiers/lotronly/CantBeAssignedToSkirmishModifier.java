package com.gempukku.lotro.modifiers.lotronly;

import com.gempukku.lotro.cards.LotroPhysicalCard;
import com.gempukku.lotro.common.Filterable;
import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.game.DefaultGame;
import com.gempukku.lotro.modifiers.AbstractModifier;
import com.gempukku.lotro.condition.Condition;
import com.gempukku.lotro.modifiers.ModifierEffect;

public class CantBeAssignedToSkirmishModifier extends AbstractModifier {
    public CantBeAssignedToSkirmishModifier(LotroPhysicalCard source, Filterable affectFilter) {
        super(source, "Can't be assigned to skirmish", affectFilter, ModifierEffect.ASSIGNMENT_MODIFIER);
    }

    public CantBeAssignedToSkirmishModifier(LotroPhysicalCard source, Condition condition, Filterable affectFilter) {
        super(source, "Can't be assigned to skirmish", affectFilter, condition, ModifierEffect.ASSIGNMENT_MODIFIER);
    }

    @Override
    public boolean isPreventedFromBeingAssignedToSkirmish(DefaultGame game, Side sidePlayer, LotroPhysicalCard card) {
        return true;
    }
}
