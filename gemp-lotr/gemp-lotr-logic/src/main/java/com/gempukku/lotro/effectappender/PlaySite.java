package com.gempukku.lotro.effectappender;

import com.gempukku.lotro.actioncontext.DefaultActionContext;
import com.gempukku.lotro.cards.*;
import com.gempukku.lotro.fieldprocessor.FieldUtils;
import com.gempukku.lotro.effectappender.resolver.ValueResolver;
import com.gempukku.lotro.common.Filterable;
import com.gempukku.lotro.common.SitesBlock;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.DefaultGame;
import com.gempukku.lotro.actions.CostToEffectAction;
import com.gempukku.lotro.effects.PlaySiteEffect;
import com.gempukku.lotro.effects.Effect;
import org.json.simple.JSONObject;

public class PlaySite implements EffectAppenderProducer {
    @Override
    public EffectAppender createEffectAppender(JSONObject effectObject, CardGenerationEnvironment environment) throws InvalidCardDefinitionException {
        FieldUtils.validateAllowedFields(effectObject, "block", "filter", "number", "memorize");
        final SitesBlock block = FieldUtils.getEnum(SitesBlock.class, effectObject.get("block"), "block");
        final String filter = FieldUtils.getString(effectObject.get("filter"), "filter", "any");
        final ValueSource valueSource = ValueResolver.resolveEvaluator(effectObject.get("number"), environment);
        final String memorize = FieldUtils.getString(effectObject.get("memorize"), "memorize");

        final FilterableSource filterableSource = environment.getFilterFactory().generateFilter(filter, environment);

        return new DelayedAppender<>() {
            @Override
            public boolean isPlayableInFull(DefaultActionContext<DefaultGame> actionContext) {
                final DefaultGame game = actionContext.getGame();
                final int siteNumber = valueSource.getEvaluator(actionContext).evaluateExpression(game, null);
                final Filterable filterable = filterableSource.getFilterable(actionContext);
                final String playerId = actionContext.getPerformingPlayer();

                if (siteNumber > 9 || siteNumber < 1)
                    return false;

                if (game.getFormat().isOrderedSites()) {
                    Filter printedSiteNumber = (game1, physicalCard) -> physicalCard.getBlueprint().getSiteNumber() == siteNumber;
                    if (block != null)
                        return Filters.filter(game.getGameState().getAdventureDeck(playerId), game, Filters.and(filterable, printedSiteNumber, Filters.siteBlock(block))).size() > 0;
                    else
                        return Filters.filter(game.getGameState().getAdventureDeck(playerId), game, Filters.and(filterable, printedSiteNumber)).size() > 0;
                } else {
                    if (block != null)
                        return Filters.filter(game.getGameState().getAdventureDeck(playerId), game, Filters.and(filterable, Filters.siteBlock(block))).size() > 0;
                    else
                        return Filters.filter(game.getGameState().getAdventureDeck(playerId), game, filterable).size() > 0;
                }
            }

            @Override
            protected Effect createEffect(boolean cost, CostToEffectAction action, DefaultActionContext actionContext) {
                final DefaultGame game = actionContext.getGame();
                final int siteNumber = valueSource.getEvaluator(actionContext).evaluateExpression(game, null);
                final Filterable filterable = filterableSource.getFilterable(actionContext);
                return new PlaySiteEffect(action, actionContext.getPerformingPlayer(), block, siteNumber, filterable) {
                    @Override
                    protected void sitePlayedCallback(LotroPhysicalCard site) {
                        if (memorize != null) {
                            actionContext.setCardMemory(memorize, site);
                        }
                    }
                };
            }
        };
    }
}
