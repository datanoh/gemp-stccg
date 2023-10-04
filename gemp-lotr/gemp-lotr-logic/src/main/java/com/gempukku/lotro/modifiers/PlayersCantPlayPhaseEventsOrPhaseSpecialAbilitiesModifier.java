package com.gempukku.lotro.modifiers;

import com.gempukku.lotro.common.Phase;
import com.gempukku.lotro.cards.PhysicalCard;
import com.gempukku.lotro.game.DefaultGame;
import com.gempukku.lotro.actions.Action;
import com.gempukku.lotro.condition.Condition;

public class PlayersCantPlayPhaseEventsOrPhaseSpecialAbilitiesModifier extends AbstractModifier {
    private final Phase _phase;

    public PlayersCantPlayPhaseEventsOrPhaseSpecialAbilitiesModifier(PhysicalCard source, Phase phase) {
        this(source, null, phase);
    }

    public PlayersCantPlayPhaseEventsOrPhaseSpecialAbilitiesModifier(PhysicalCard source, Condition condition, Phase phase) {
        super(source, null, null, condition, ModifierEffect.ACTION_MODIFIER);
        _phase = phase;
    }

    @Override
    public boolean canPlayAction(DefaultGame game, String performingPlayer, Action action) {
        return (action.getActionType() != Action.ActionType.PLAY_CARD && action.getActionType() != Action.ActionType.SPECIAL_ABILITY)
                || action.getActionTimeword() != _phase;
    }
}
