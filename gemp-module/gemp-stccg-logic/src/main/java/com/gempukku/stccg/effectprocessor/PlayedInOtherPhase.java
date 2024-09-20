package com.gempukku.stccg.effectprocessor;

import com.gempukku.stccg.cards.blueprints.CardBlueprint;
import com.gempukku.stccg.cards.blueprints.CardBlueprintFactory;
import com.gempukku.stccg.cards.InvalidCardDefinitionException;
import com.gempukku.stccg.common.filterable.Phase;
import com.gempukku.stccg.requirement.Requirement;
import org.json.simple.JSONObject;

public class PlayedInOtherPhase implements EffectProcessor {
    @Override
    public void processEffect(JSONObject value, CardBlueprint blueprint, CardBlueprintFactory environment) throws InvalidCardDefinitionException {
        environment.validateAllowedFields(value, "phase", "requires");

        final Phase phase = environment.getEnum(Phase.class, value.get("phase"), "phase");

        final Requirement[] conditions = environment.getRequirementsFromJSON(value);

        blueprint.appendPlayInOtherPhaseCondition(
                actionContext -> actionContext.getGameState().getCurrentPhase() == phase && actionContext.acceptsAllRequirements(conditions)
        );
    }
}
