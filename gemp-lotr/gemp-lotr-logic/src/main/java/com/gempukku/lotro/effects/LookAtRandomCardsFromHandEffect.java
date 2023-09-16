package com.gempukku.lotro.effects;

import com.gempukku.lotro.cards.lotronly.LotroPhysicalCard;
import com.gempukku.lotro.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.lotro.game.DefaultGame;
import com.gempukku.lotro.rules.GameUtils;

import java.util.Collections;
import java.util.List;

public abstract class LookAtRandomCardsFromHandEffect extends AbstractEffect {
    private final LotroPhysicalCard _source;
    private final int _count;
    private final String _actingPlayer;
    private final String _playerHand;

    public LookAtRandomCardsFromHandEffect(String actingPlayer, String handOfPlayer, LotroPhysicalCard source, int count) {
        _source = source;
        _count = count;
        _actingPlayer = actingPlayer;
        _playerHand = handOfPlayer;
    }

    @Override
    public String getText(DefaultGame game) {
        return "Look at random cards from hand";
    }

    @Override
    public boolean isPlayableInFull(DefaultGame game) {
        if (game.getGameState().getHand(_playerHand).size() < _count)
            return false;
        return _actingPlayer.equals(_playerHand) || game.getModifiersQuerying().canLookOrRevealCardsInHand(game, _playerHand, _actingPlayer);
    }

    @Override
    protected FullEffectResult playEffectReturningResult(DefaultGame game) {
        if (game.getModifiersQuerying().canLookOrRevealCardsInHand(game, _playerHand, _actingPlayer)) {
            List<LotroPhysicalCard> randomCards = GameUtils.getRandomCards(game.getGameState().getHand(_playerHand), _count);

            if (randomCards.size() > 0) {
                game.getUserFeedback().sendAwaitingDecision(_actingPlayer,
                        new ArbitraryCardsSelectionDecision(1, "Random cards from opponent's hand", randomCards, Collections.emptyList(), 0, 0) {
                            @Override
                            public void decisionMade(String result) {
                            }
                        });

                game.getGameState().sendMessage(GameUtils.getCardLink(_source) + " looked at " + randomCards.size() + " cards from " + _playerHand + " hand at random");
            }
            else {
                game.getGameState().sendMessage("No cards in " + _playerHand + " hand to look at");
            }

            cardsSeen(randomCards);

            return new FullEffectResult(randomCards.size() == _count);
        }
        return new FullEffectResult(false);
    }

    protected abstract void cardsSeen(List<LotroPhysicalCard> revealedCards);
}
