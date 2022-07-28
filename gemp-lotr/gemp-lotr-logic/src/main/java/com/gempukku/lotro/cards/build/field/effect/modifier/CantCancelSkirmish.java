package com.gempukku.lotro.cards.build.field.effect.modifier;

import com.gempukku.lotro.cards.build.*;
import com.gempukku.lotro.cards.build.field.FieldUtils;
import com.gempukku.lotro.logic.modifiers.CantCancelSkirmishModifier;
import com.gempukku.lotro.logic.modifiers.CantDiscardFromPlayModifier;
import org.json.simple.JSONObject;

public class CantCancelSkirmish implements ModifierSourceProducer {
    @Override
    public ModifierSource getModifierSource(JSONObject object, CardGenerationEnvironment environment) throws InvalidCardDefinitionException {
        FieldUtils.validateAllowedFields(object, "filter", "condition");

        final JSONObject[] conditionArray = FieldUtils.getObjectArray(object.get("condition"), "condition");
        final String filter = FieldUtils.getString(object.get("filter"), "filter", "any");

        final FilterableSource filterableSource = environment.getFilterFactory().generateFilter(filter, environment);
        final Requirement[] requirements = environment.getRequirementFactory().getRequirements(conditionArray, environment);

        return (actionContext) -> {
            return new CantCancelSkirmishModifier(actionContext.getSource(),
                    new RequirementCondition(requirements, actionContext),
                    filterableSource.getFilterable(actionContext));
        };
    }
}
