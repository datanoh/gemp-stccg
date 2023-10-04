package com.gempukku.lotro.effectprocessor;

import com.gempukku.lotro.cards.BuiltLotroCardBlueprint;
import com.gempukku.lotro.cards.CardGenerationEnvironment;
import com.gempukku.lotro.cards.InvalidCardDefinitionException;
import com.gempukku.lotro.fieldprocessor.FieldUtils;
import com.gempukku.lotro.effectappender.EffectAppender;
import com.gempukku.lotro.game.DefaultGame;
import com.gempukku.lotro.modifiers.ExtraPlayCost;
import com.gempukku.lotro.cards.PhysicalCard;
import com.gempukku.lotro.actions.CostToEffectAction;
import com.gempukku.lotro.condition.Condition;
import org.json.simple.JSONObject;

public class ExtraCost implements EffectProcessor {
    @Override
    public void processEffect(JSONObject value, BuiltLotroCardBlueprint blueprint, CardGenerationEnvironment environment) throws InvalidCardDefinitionException {
        FieldUtils.validateAllowedFields(value, "cost");

        final EffectAppender costAppender = environment.getEffectAppenderFactory().getEffectAppender((JSONObject) value.get("cost"), environment);

        blueprint.appendExtraPlayCost(
                (actionContext) -> new ExtraPlayCost() {
                    @Override
                    public void appendExtraCosts(DefaultGame game, CostToEffectAction action, PhysicalCard card) {
                        costAppender.appendEffect(true, action, actionContext);
                    }

                    @Override
                    public boolean canPayExtraCostsToPlay(DefaultGame game, PhysicalCard card) {
                        return costAppender.isPlayableInFull(actionContext);
                    }

                    @Override
                    public Condition getCondition() {
                        return null;
                    }
                });
    }
}
