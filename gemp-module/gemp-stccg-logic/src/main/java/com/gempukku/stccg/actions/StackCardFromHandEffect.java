package com.gempukku.stccg.actions;

import com.gempukku.stccg.actions.DefaultEffect;
import com.gempukku.stccg.cards.physicalcard.PhysicalCard;
import com.gempukku.stccg.common.filterable.Zone;
import com.gempukku.stccg.game.DefaultGame;

import java.util.Collections;

public class StackCardFromHandEffect extends DefaultEffect {
    private final PhysicalCard _card;
    private final PhysicalCard _stackOn;
    private final DefaultGame _game;

    public StackCardFromHandEffect(DefaultGame game, PhysicalCard card, PhysicalCard stackOn) {
        super(card);
        _card = card;
        _stackOn = stackOn;
        _game = game;
    }

    @Override
    protected FullEffectResult playEffectReturningResult() {
        if (isPlayableInFull()) {
            _game.sendMessage(_card.getOwnerName() + " stacks " + _card.getCardLink() + " from hand on " + _stackOn.getCardLink());
            _game.getGameState().removeCardsFromZone(_card.getOwnerName(), Collections.singleton(_card));
            _game.getGameState().stackCard(_card, _stackOn);
            return new FullEffectResult(true);
        }
        return new FullEffectResult(false);
    }

    @Override
    public String getText() {
        return "Stack " + _card.getFullName() + " from hand on " + _stackOn.getFullName();
    }

    @Override
    public boolean isPlayableInFull() {
        return _card.getZone() == Zone.HAND;
    }
}
