package com.gempukku.lotro.modifiers;

import com.gempukku.lotro.cards.*;
import com.gempukku.lotro.fieldprocessor.FieldUtils;
import com.gempukku.lotro.effectappender.EffectAppender;
import com.gempukku.lotro.common.Filterable;
import com.gempukku.lotro.game.DefaultGame;
import com.gempukku.lotro.actions.CostToEffectAction;
import com.gempukku.lotro.modifiers.cost.AbstractExtraPlayCostModifier;
import com.gempukku.lotro.requirement.Requirement;
import org.json.simple.JSONObject;

public class ExtraCostToPlay implements ModifierSourceProducer {
    @Override
    public ModifierSource getModifierSource(JSONObject object, CardGenerationEnvironment environment) throws InvalidCardDefinitionException {
        FieldUtils.validateAllowedFields(object, "requires", "cost", "filter");

        final JSONObject[] conditionArray = FieldUtils.getObjectArray(object.get("requires"), "requires");
        final String filter = FieldUtils.getString(object.get("filter"), "filter");

        final FilterableSource filterableSource = environment.getFilterFactory().generateFilter(filter, environment);
        final Requirement[] requirements = environment.getRequirementFactory().getRequirements(conditionArray, environment);

        final JSONObject[] effectArray = FieldUtils.getObjectArray(object.get("cost"), "cost");
        final EffectAppender[] effectAppenders = environment.getEffectAppenderFactory().getEffectAppenders(effectArray, environment);

        return (actionContext) -> {
            final Filterable filterable = filterableSource.getFilterable(actionContext);
            final RequirementCondition condition = new RequirementCondition(requirements, actionContext);

            return new AbstractExtraPlayCostModifier(actionContext.getSource(), "Cost to play is modified", filterable, condition) {
                @Override
                public void appendExtraCosts(DefaultGame game, CostToEffectAction action, PhysicalCard card) {
                    for (EffectAppender effectAppender : effectAppenders)
                        effectAppender.appendEffect(true, action, actionContext);
                }

                @Override
                public boolean canPayExtraCostsToPlay(DefaultGame game, PhysicalCard card) {
                    for (EffectAppender effectAppender : effectAppenders) {
                        if (!effectAppender.isPlayableInFull(actionContext))
                            return false;
                    }

                    return true;
                }
            };
        };
    }
}
