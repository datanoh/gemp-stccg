package com.gempukku.lotro.cards.set20.isengard;

import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.PlayUtils;
import com.gempukku.lotro.logic.cardtype.AbstractAttachable;
import com.gempukku.lotro.logic.timing.Action;
import com.gempukku.lotro.logic.timing.PlayConditions;

import java.util.Collections;
import java.util.List;

/**
 * 1
 * •Vile Blood
 * Isengard	Condition • Minion
 * +1
 * Bearer must be an [Isengard] orc. While you have initiative, you may play this card from your discard pile.
 */
public class Card20_216 extends AbstractAttachable {
    public Card20_216() {
        super(Side.SHADOW, CardType.CONDITION, 1, Culture.ISENGARD, null, "Vile Blood", null, true);
    }

    @Override
    public Filterable getValidTargetFilter(String playerId, LotroGame game, PhysicalCard self) {
        return Filters.and(Culture.ISENGARD, Race.ORC);
    }

    @Override
    public int getVitality() {
        return 1;
    }

    @Override
    public List<? extends Action> getPhaseActionsFromDiscard(String playerId, LotroGame game, PhysicalCard self) {
        if (PlayConditions.isPhase(game, Phase.SHADOW)
                && PlayConditions.hasInitiative(game, Side.SHADOW)
                && PlayConditions.canPlayFromDiscard(playerId, game, self)) {
            return Collections.singletonList(PlayUtils.getPlayCardAction(game, self, 0, Filters.any, false));
        }
        return null;
    }
}
