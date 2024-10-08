package com.gempukku.stccg.actions;

import com.gempukku.stccg.cards.physicalcard.PhysicalCard;
import com.gempukku.stccg.game.DefaultGame;

import java.util.Collection;

public interface PreventableCardEffect {
    Collection<PhysicalCard> getAffectedCardsMinusPrevented();

    void preventEffect(DefaultGame game, PhysicalCard affectedCard);
    default PhysicalCard getSource() { return null; }

}
