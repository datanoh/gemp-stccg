package com.gempukku.stccg.actions.sources;

import com.gempukku.stccg.actions.Action;
import com.gempukku.stccg.actions.CostToEffectAction;
import com.gempukku.stccg.actions.Effect;
import com.gempukku.stccg.actions.turn.IncrementPhaseLimitEffect;
import com.gempukku.stccg.actions.turn.IncrementTurnLimitEffect;
import com.gempukku.stccg.cards.ActionContext;
import com.gempukku.stccg.cards.InvalidCardDefinitionException;
import com.gempukku.stccg.cards.blueprints.CardBlueprintFactory;
import com.gempukku.stccg.cards.physicalcard.PhysicalCard;
import com.gempukku.stccg.common.filterable.Phase;
import com.gempukku.stccg.effectappender.AbstractEffectAppender;
import com.gempukku.stccg.effectappender.EffectAppender;
import com.gempukku.stccg.effectappender.EffectAppenderFactory;
import com.gempukku.stccg.requirement.Requirement;
import org.json.simple.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class DefaultActionSource implements ActionSource {
    private final List<Requirement> requirements = new LinkedList<>();

    protected final List<EffectAppender> costs = new LinkedList<>();
    protected final List<EffectAppender> effects = new LinkedList<>();

    protected String _text;

    public void setText(String text) {
        this._text = text;
    }

    public void addRequirement(Requirement requirement) {
        this.requirements.add(requirement);
    }

    public void addCost(EffectAppender effectAppender) {
        costs.add(effectAppender);
    }

    public void addEffect(EffectAppender effectAppender) {
        effects.add(effectAppender);
    }

    @Override
    public boolean isValid(ActionContext actionContext) {
        return actionContext.acceptsAllRequirements(requirements);
    }

    @Override
    public void appendActionToContext(CostToEffectAction action, ActionContext actionContext) {
        if (_text != null)
            action.setText(actionContext.substituteText(_text));

        costs.forEach(cost -> cost.appendEffect(true, action, actionContext));

        effects.forEach(actionEffect -> actionEffect.appendEffect(false, action, actionContext));
    }

    public Action createAction(PhysicalCard card) {
        return null;
        // TODO - This class should eventually be made abstract so that this method can be defined differently for different types of ActionSources
    }

    @Override
    public Action createActionAndAppendToContext(PhysicalCard card, ActionContext actionContext) {
        return null;
        // TODO - This class should eventually be made abstract so that this method can be defined differently for different types of ActionSources
    }

    public void processRequirementsCostsAndEffects(JSONObject value, CardBlueprintFactory environment)
            throws InvalidCardDefinitionException {
        final JSONObject[] requirementArray = environment.getObjectArray(value.get("requires"), "requires");
        final JSONObject[] costArray = environment.getObjectArray(value.get("cost"), "cost");
        final JSONObject[] effectArray = environment.getObjectArray(value.get("effect"), "effect");

        if (costArray.length == 0 && effectArray.length == 0)
            throw new InvalidCardDefinitionException("Action does not contain a cost, nor effect");

        for (JSONObject requirement : requirementArray) {
            addRequirement(environment.getRequirement(requirement));
        }

        final EffectAppenderFactory effectAppenderFactory = environment.getEffectAppenderFactory();
        for (JSONObject cost : costArray) {
            final EffectAppender effectAppender = effectAppenderFactory.getEffectAppender(cost);
            addRequirement(effectAppender::isPlayableInFull);
            addCost(effectAppender);
        }

        for (JSONObject effect : effectArray) {
            final EffectAppender effectAppender = effectAppenderFactory.getEffectAppender(effect);
            if (effectAppender.isPlayabilityCheckedForEffect())
                addRequirement(effectAppender::isPlayableInFull);
            addEffect(effectAppender);
        }
    }

    public void setPhaseLimit(Phase phase, int limitPerPhase) {
        addRequirement((actionContext) -> actionContext.getSource().checkPhaseLimit(phase, limitPerPhase));
        addCost(
                new AbstractEffectAppender() {
                    @Override
                    protected Effect createEffect(boolean cost, CostToEffectAction action, ActionContext actionContext) {
                        return new IncrementPhaseLimitEffect(actionContext, phase, limitPerPhase);
                    }
                });
    }

    public void setTurnLimit(int limitPerTurn) {
        addRequirement((actionContext) -> actionContext.getSource().checkTurnLimit(limitPerTurn));
        addCost(
            new AbstractEffectAppender() {
                @Override
                protected Effect createEffect(boolean cost, CostToEffectAction action, ActionContext actionContext) {
                    return new IncrementTurnLimitEffect(actionContext, limitPerTurn);
                }
            });
    }
}