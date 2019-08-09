package com.gempukku.lotro.cards.set1.isengard;

import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Keyword;
import com.gempukku.lotro.common.Race;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.cardtype.AbstractMinion;
import com.gempukku.lotro.logic.modifiers.CancelStrengthBonusTargetModifier;
import com.gempukku.lotro.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Shadow
 * Culture: Isengard
 * Twilight Cost: 5
 * Type: Minion • Uruk-Hai
 * Strength: 9
 * Vitality: 4
 * Site: 5
 * Game Text: Damage +1. A character skirmishing this minion does not gain strength bonuses from weapons.
 */
public class Card1_143 extends AbstractMinion {
    public Card1_143() {
        super(5, 9, 4, 5, Race.URUK_HAI, Culture.ISENGARD, "Troop of Uruk-hai");
        addKeyword(Keyword.DAMAGE);
    }

    @Override
    public List<? extends Modifier> getInPlayModifiers(LotroGame game, PhysicalCard self) {
        return Collections.singletonList(new CancelStrengthBonusTargetModifier(self, Filters.inSkirmishAgainst(self), Filters.weapon));
    }
}
