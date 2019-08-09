package com.gempukku.lotro.cards.set3.site;

import com.gempukku.lotro.common.CardType;
import com.gempukku.lotro.common.Keyword;
import com.gempukku.lotro.common.Phase;
import com.gempukku.lotro.common.SitesBlock;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.cardtype.AbstractSite;
import com.gempukku.lotro.logic.modifiers.AbstractModifier;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.modifiers.ModifierEffect;
import com.gempukku.lotro.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Realms of Elf-lords
 * Twilight Cost: 8
 * Type: Site
 * Site: 8
 * Game Text: River. Maneuver events may not be played.
 */
public class Card3_117 extends AbstractSite {
    public Card3_117() {
        super("Gates of Argonath", SitesBlock.FELLOWSHIP, 8, 8, Direction.RIGHT);
        addKeyword(Keyword.RIVER);
    }

    @Override
    public List<? extends Modifier> getInPlayModifiers(LotroGame game, PhysicalCard self) {
        return Collections.singletonList(
                new AbstractModifier(self, "Maneuver events may not be played", null, ModifierEffect.ACTION_MODIFIER) {
                    @Override
                    public boolean canPlayAction(LotroGame game, String performingPlayer, Action action) {
                        PhysicalCard source = action.getActionSource();
                        if (source != null && action.getActionTimeword() == Phase.MANEUVER && source.getBlueprint().getCardType() == CardType.EVENT)
                            return false;
                        return true;
                    }
                });
    }
}
