package com.gempukku.lotro.effectappender.resolver;

import com.gempukku.lotro.actioncontext.ActionContext;
import com.gempukku.lotro.cards.InvalidCardDefinitionException;
import com.gempukku.lotro.cards.PlayerSource;
import com.gempukku.lotro.cards.PhysicalCard;
import com.gempukku.lotro.rules.lotronly.LotroGameUtils;

import java.util.Locale;

public class PlayerResolver {
    public static PlayerSource resolvePlayer(String type) throws InvalidCardDefinitionException {

        if (type.equalsIgnoreCase("you"))
            return ActionContext::getPerformingPlayer;
        if (type.equalsIgnoreCase("owner"))
            return (actionContext) -> actionContext.getSource().getOwner();
        else if (type.equalsIgnoreCase("shadowPlayer") || type.equalsIgnoreCase("shadow")
                || type.equalsIgnoreCase("s"))
            return (actionContext) -> LotroGameUtils.getFirstShadowPlayer(actionContext.getGame());
        else if (type.equalsIgnoreCase("fp") || type.equalsIgnoreCase("freeps")
                || type.equalsIgnoreCase("free peoples") || type.equalsIgnoreCase("free people"))
            return ((actionContext) -> actionContext.getGame().getGameState().getCurrentPlayerId());
        else {
            String memory = type.substring(type.indexOf("(") + 1, type.lastIndexOf(")"));
            if (type.toLowerCase(Locale.ROOT).startsWith("ownerfrommemory(") && type.endsWith(")")) {
                return (actionContext) -> {
                    final PhysicalCard cardFromMemory = actionContext.getCardFromMemory(memory);
                    if (cardFromMemory != null)
                        return cardFromMemory.getOwner();
                    else
                        // Sensible default
                        return actionContext.getPerformingPlayer();
                };
            }
            else if (type.toLowerCase().startsWith("frommemory(") && type.endsWith(")")) {
                return (actionContext) -> actionContext.getValueFromMemory(memory);
            }
        }
        throw new InvalidCardDefinitionException("Unable to resolve player resolver of type: " + type);
    }
}
