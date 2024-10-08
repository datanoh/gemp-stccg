package com.gempukku.stccg.actions.tribblepower;

import com.gempukku.stccg.actions.CostToEffectAction;
import com.gempukku.stccg.actions.draw.DrawCardsEffect;
import com.gempukku.stccg.cards.TribblesActionContext;
import com.gempukku.stccg.decisions.MultipleChoiceAwaitingDecision;

import com.gempukku.stccg.game.TribblesGame;

public class ActivateDrawTribblePowerEffect extends ActivateTribblePowerEffect {

    String _drawingPlayer;
    public ActivateDrawTribblePowerEffect(CostToEffectAction action, TribblesActionContext actionContext) {
        super(action, actionContext);
    }

    @Override
    protected FullEffectResult playEffectReturningResult() {
        String[] players = _game.getAllPlayerIds();
        if (players.length == 1)
            playerChosen(players[0], _game);
        else
            _game.getUserFeedback().sendAwaitingDecision(_activatingPlayer,
                    new MultipleChoiceAwaitingDecision("Choose a player", players) {
                        @Override
                        protected void validDecisionMade(int index, String result) {
                            playerChosen(result, _game);
                        }
                    });
        _game.getActionsEnvironment().emitEffectResult(_result);
        return new FullEffectResult(true);
    }

    private void playerChosen(String playerId, TribblesGame game) {
        _drawingPlayer = playerId;
        new DrawCardsEffect(game, _action, playerId, 1).playEffect();
    }
}