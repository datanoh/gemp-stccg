package com.gempukku.lotro.cards.set20.moria;

import com.gempukku.lotro.common.CardType;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Race;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.cardtype.AbstractMinion;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.modifiers.StrengthModifier;
import com.gempukku.lotro.logic.modifiers.evaluator.CountActiveEvaluator;

import java.util.Collections;
import java.util.List;

/**
 * 3
 * Goblin Strategist
 * Moria	Minion • Goblin
 * 7	2	4
 * This minion is strength +1 for each [Moria] condition with a Goblin stacked on it.
 */
public class Card20_273 extends AbstractMinion{
    public Card20_273() {
        super(3, 7, 2, 4, Race.GOBLIN, Culture.MORIA, "Goblin Strategist");
    }

    @Override
    public List<? extends Modifier> getInPlayModifiers(LotroGame game, PhysicalCard self) {
return Collections.singletonList(new StrengthModifier(self, self, null,
new CountActiveEvaluator(Culture.MORIA, CardType.CONDITION, Filters.hasStacked(Race.GOBLIN))));
}
}
