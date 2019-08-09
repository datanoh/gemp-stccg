package com.gempukku.lotro.cards.set1.isengard;

import com.gempukku.lotro.common.CardType;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Keyword;
import com.gempukku.lotro.common.Race;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.cardtype.AbstractMinion;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.modifiers.StrengthModifier;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Shadow
 * Culture: Isengard
 * Twilight Cost: 2
 * Type: Minion � Uruk-Hai
 * Strength: 6
 * Vitality: 1
 * Site: 5
 * Game Text: Damage +1. While you can spot a weather condition, this minion is strength +3.
 */
public class Card1_149 extends AbstractMinion {
    public Card1_149() {
        super(2, 6, 1, 5, Race.URUK_HAI, Culture.ISENGARD, "Uruk Messenger");
        addKeyword(Keyword.DAMAGE, 1);
    }

    @Override
    public List<? extends Modifier> getInPlayModifiers(LotroGame game, PhysicalCard self) {
        return Collections.singletonList(new StrengthModifier(self,
                Filters.and(
                        self,
                        new Filter() {
                            @Override
                            public boolean accepts(LotroGame game, PhysicalCard physicalCard) {
                                return Filters.canSpot(game, Keyword.WEATHER, CardType.CONDITION);
                            }
                        }), 3));
    }
}
