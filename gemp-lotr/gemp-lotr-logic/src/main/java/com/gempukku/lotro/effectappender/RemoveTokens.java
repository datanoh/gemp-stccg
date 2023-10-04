package com.gempukku.lotro.effectappender;

import com.gempukku.lotro.actions.CostToEffectAction;
import com.gempukku.lotro.cards.CardGenerationEnvironment;
import com.gempukku.lotro.actioncontext.DefaultActionContext;
import com.gempukku.lotro.cards.InvalidCardDefinitionException;
import com.gempukku.lotro.cards.ValueSource;
import com.gempukku.lotro.fieldprocessor.FieldUtils;
import com.gempukku.lotro.effectappender.resolver.CardResolver;
import com.gempukku.lotro.effectappender.resolver.ValueResolver;
import com.gempukku.lotro.cards.PhysicalCard;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Token;
import com.gempukku.lotro.effects.Effect;
import com.gempukku.lotro.effects.RemoveTokenEffect;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.DefaultGame;
import com.gempukku.lotro.modifiers.ModifierFlag;
import com.gempukku.lotro.evaluator.ConstantEvaluator;
import org.json.simple.JSONObject;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class RemoveTokens implements EffectAppenderProducer {
    @Override
    public EffectAppender createEffectAppender(JSONObject effectObject, CardGenerationEnvironment environment) throws InvalidCardDefinitionException {
        FieldUtils.validateAllowedFields(effectObject, "count", "culture", "filter", "memorize");

        final ValueSource valueSource = ValueResolver.resolveEvaluator(effectObject.get("count"), 1, environment);
        final Culture culture = FieldUtils.getEnum(Culture.class, effectObject.get("culture"), "culture");
        final String filter = FieldUtils.getString(effectObject.get("filter"), "filter", "self");
        final String memory = FieldUtils.getString(effectObject.get("memorize"), "memorize", "_temp");

        final Token token = (culture != null) ? Token.findTokenForCulture(culture) : null;

        MultiEffectAppender result = new MultiEffectAppender();

        result.addEffectAppender(
                CardResolver.resolveCards(filter,
                        actionContext -> {
                            final int tokenCount = valueSource.getEvaluator(actionContext).evaluateExpression(actionContext.getGame(), null);
                            if (token != null)
                                return Filters.hasToken(token, tokenCount);
                            else
                                return Filters.hasAnyCultureTokens(tokenCount);
                        },
                        new ConstantEvaluator(1), memory, "you", "Choose card to remove tokens from", environment));
        result.addEffectAppender(
                new DelayedAppender<>() {
                    @Override
                    public boolean isPlayableInFull(DefaultActionContext<DefaultGame> actionContext) {
                        final DefaultGame game = actionContext.getGame();
                        return !game.getModifiersQuerying().hasFlagActive(game, ModifierFlag.CANT_TOUCH_CULTURE_TOKENS);
                    }

                    @Override
                    protected List<Effect> createEffects(boolean cost, CostToEffectAction action, DefaultActionContext actionContext) {
                        final Collection<? extends PhysicalCard> cardsFromMemory = actionContext.getCardsFromMemory(memory);

                        final int tokenCount = valueSource.getEvaluator(actionContext).evaluateExpression(actionContext.getGame(), null);

                        final DefaultGame game = actionContext.getGame();

                        List<Effect> result = new LinkedList<>();
                        for (PhysicalCard card : cardsFromMemory) {
                            if (token != null)
                                result.add(new RemoveTokenEffect(actionContext.getSource(), card, token, tokenCount));
                            else {
                                result.add(new RemoveTokenEffect(actionContext.getSource(), card, getCultureTokenOnCard(game, card), tokenCount));
                            }
                        }

                        return result;
                    }
                });

        return result;
    }

    private Token getCultureTokenOnCard(DefaultGame game, PhysicalCard card) {
        for (Token token : Token.values())
            if (token.getCulture() != null && game.getGameState().getTokenCount(card, token) > 0)
                return token;

        return null;
    }
}
