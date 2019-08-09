package com.gempukku.lotro.cards.set20.gondor;

import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.cardtype.AbstractPermanent;
import com.gempukku.lotro.logic.modifiers.MinionSiteNumberModifier;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.modifiers.SpotCondition;
import com.gempukku.lotro.logic.modifiers.evaluator.CountActiveEvaluator;

import java.util.Collections;
import java.util.List;

/**
 * ❶ •Into the Wild [Gon]
 * Condition • Support Area
 * While you can spot a [Gon] ranger, the site number of each minion in play is +1 for each site from your adventure deck in the current region
 * <p/>
 * http://lotrtcg.org/coreset/gondor/intothewild(r3).jpg
 */
public class Card20_197 extends AbstractPermanent {
    public Card20_197() {
        super(Side.FREE_PEOPLE, 1, CardType.CONDITION, Culture.GONDOR, "Into the Wild", null, true);
    }

    @Override
    public List<? extends Modifier> getInPlayModifiers(LotroGame game, PhysicalCard self) {
return Collections.singletonList(new MinionSiteNumberModifier(
self, Filters.and(CardType.MINION, Filters.inPlay()), new SpotCondition(Culture.GONDOR, Keyword.RANGER),
new CountActiveEvaluator(CardType.SITE, Zone.ADVENTURE_PATH, Filters.owner(self.getOwner()), Filters.currentRegion)));
}
}
