package com.gempukku.lotro.cards.set20.gondor;

import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Keyword;
import com.gempukku.lotro.common.Race;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.cardtype.AbstractCompanion;
import com.gempukku.lotro.logic.modifiers.GameHasCondition;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.modifiers.StrengthModifier;
import com.gempukku.lotro.logic.modifiers.evaluator.Evaluator;

import java.util.Collections;
import java.util.List;

/**
 * 3
 * •Boromir, Champion of Minas Tirith
 * Gondor	Companion • Man
 * 7	3	5
 * Ranger.
 * Boromir is strength +1 for each wound on each character in his skirmish.
 */
public class Card20_184 extends AbstractCompanion {
    public Card20_184() {
        super(3, 7, 3, 5, Culture.GONDOR, Race.MAN, null, "Boromir", "Champion of Minas Tirith", true);
        addKeyword(Keyword.RANGER);
    }

    @Override
    public List<? extends Modifier> getInPlayModifiers(LotroGame game, PhysicalCard self) {
        return Collections.singletonList(new StrengthModifier(self, self, new GameHasCondition(self, Filters.inSkirmish),
                new Evaluator() {
                    @Override
                    public int evaluateExpression(LotroGame game, PhysicalCard cardAffected) {
                        int woundCount = 0;
                        for (PhysicalCard physicalCard : Filters.filterActive(game, Filters.character, Filters.inSkirmish, Filters.wounded)) {
                            woundCount+=game.getGameState().getWounds(physicalCard);
                        }
                        return woundCount;
                    }
                }));
    }
}
