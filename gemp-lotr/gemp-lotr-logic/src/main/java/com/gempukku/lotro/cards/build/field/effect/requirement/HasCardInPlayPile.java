package com.gempukku.lotro.cards.build.field.effect.requirement;

import com.gempukku.lotro.cards.build.*;
import com.gempukku.lotro.cards.build.field.FieldUtils;
import com.gempukku.lotro.cards.build.field.effect.appender.resolver.PlayerResolver;
import com.gempukku.lotro.common.Filterable;
import com.gempukku.lotro.game.PlayConditions;
import com.gempukku.lotro.game.TribblesGame;
import org.json.simple.JSONObject;

public class HasCardInPlayPile implements RequirementProducer {
    @Override
    public Requirement<TribblesGame> getPlayRequirement(JSONObject object, CardGenerationEnvironment environment)
            throws InvalidCardDefinitionException {
        FieldUtils.validateAllowedFields(object, "player", "count", "filter");

        final String player = FieldUtils.getString(object.get("player"), "player", "you");
        final int count = FieldUtils.getInteger(object.get("count"), "count", 1);
        final String filter = FieldUtils.getString(object.get("filter"), "filter");

        final PlayerSource playerSource = PlayerResolver.resolvePlayer(player);

        final FilterableSource<TribblesGame> filterableSource =
                environment.getFilterFactory().generateFilter(filter, environment);
        return (actionContext) -> {
            final String playerId = playerSource.getPlayer(actionContext);
            final Filterable filterable = filterableSource.getFilterable(actionContext);
            return PlayConditions.hasCardInPlayPile(
                    actionContext.getGame(), playerId, count, filterable
            );
        };
    }
}
