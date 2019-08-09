package com.gempukku.lotro.cards.set1.elven;

import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Keyword;
import com.gempukku.lotro.common.Race;
import com.gempukku.lotro.common.Signet;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.game.state.Skirmish;
import com.gempukku.lotro.logic.cardtype.AbstractCompanion;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.modifiers.StrengthModifier;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Free
 * Culture: Elven
 * Twilight Cost: 2
 * Type: Companion � Elf
 * Strength: 6
 * Vitality: 3
 * Resistance: 6
 * Signet: Gandalf
 * Game Text: Archer. While skirmishing a Nazgul, Legolas is strength +3.
 */
public class Card1_051 extends AbstractCompanion {
    public Card1_051() {
        super(2, 6, 3, 6, Culture.ELVEN, Race.ELF, Signet.GANDALF, "Legolas", "Prince of Mirkwood", true);
        addKeyword(Keyword.ARCHER);
    }

    @Override
    public List<? extends Modifier> getInPlayModifiers(LotroGame game, PhysicalCard self) {
        return Collections.singletonList(new StrengthModifier(self,
                Filters.and(
                        self,
                        Filters.inSkirmish,
                        new Filter() {
                            @Override
                            public boolean accepts(LotroGame game, PhysicalCard physicalCard) {
                                Skirmish activeSkirmish = game.getGameState().getSkirmish();
                                return Filters.filter(activeSkirmish.getShadowCharacters(), game, Race.NAZGUL).size() > 0;
                            }
                        }), 3));
    }
}
