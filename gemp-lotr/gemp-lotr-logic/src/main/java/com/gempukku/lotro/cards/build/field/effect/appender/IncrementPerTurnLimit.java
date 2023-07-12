package com.gempukku.lotro.cards.build.field.effect.appender;

import com.gempukku.lotro.cards.build.ActionContext;
import com.gempukku.lotro.cards.build.CardGenerationEnvironment;
import com.gempukku.lotro.cards.build.InvalidCardDefinitionException;
import com.gempukku.lotro.cards.build.field.FieldUtils;
import com.gempukku.lotro.cards.build.field.effect.EffectAppender;
import com.gempukku.lotro.cards.build.field.effect.EffectAppenderProducer;
import com.gempukku.lotro.game.actions.lotronly.CostToEffectAction;
import com.gempukku.lotro.game.effects.IncrementTurnLimitEffect;
import com.gempukku.lotro.game.effects.Effect;
import com.gempukku.lotro.game.timing.PlayConditions;
import org.json.simple.JSONObject;

public class IncrementPerTurnLimit implements EffectAppenderProducer {
    @Override
    public EffectAppender createEffectAppender(JSONObject effectObject, CardGenerationEnvironment environment) throws InvalidCardDefinitionException {
        FieldUtils.validateAllowedFields(effectObject, "limit");

        final int limit = FieldUtils.getInteger(effectObject.get("limit"), "limit", 1);

        return new DelayedAppender() {
            @Override
            protected Effect createEffect(boolean cost, CostToEffectAction action, ActionContext actionContext) {
                return new IncrementTurnLimitEffect(actionContext.getSource(), limit);
            }

            @Override
            public boolean isPlayableInFull(ActionContext actionContext) {
                return PlayConditions.checkTurnLimit(actionContext.getGame(), actionContext.getSource(), limit);
            }
        };
    }

}
