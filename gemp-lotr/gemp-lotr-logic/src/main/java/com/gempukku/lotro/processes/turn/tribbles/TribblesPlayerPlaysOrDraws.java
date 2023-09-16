package com.gempukku.lotro.processes.turn.tribbles;

import com.gempukku.lotro.decisions.CardActionSelectionDecision;
import com.gempukku.lotro.decisions.DecisionResultInvalidException;
import com.gempukku.lotro.game.DefaultGame;
import com.gempukku.lotro.actions.Action;
import com.gempukku.lotro.processes.GameProcess;

import java.util.List;

public class TribblesPlayerPlaysOrDraws implements GameProcess {
    private final String _playerId;
    private GameProcess _nextProcess;

    public TribblesPlayerPlaysOrDraws(String playerId, GameProcess followingGameProcess) {
        _playerId = playerId;
        _nextProcess = followingGameProcess;
    }

    @Override
    public void process(final DefaultGame game) {
        final List<Action> playableActions = game.getActionsEnvironment().getPhaseActions(_playerId);

        if (playableActions.size() == 0 && game.shouldAutoPass(_playerId, game.getGameState().getCurrentPhase())) {
            _nextProcess = new TribblesPlayerDrawsAndCanPlayProcess(_playerId);
        } else {
            String userMessage;
            if (playableActions.size() == 0) {
                userMessage = "No Tribbles can be played. Click 'Pass' to draw a card.";
            } else {
                userMessage = "Select Tribble to play or click 'Pass' to draw a card.";
            }
            game.getUserFeedback().sendAwaitingDecision(_playerId,
                    new CardActionSelectionDecision(game, 1, userMessage, playableActions) {
                        @Override
                        public void decisionMade(String result) throws DecisionResultInvalidException {
                            Action action = getSelectedAction(result);
                            if (action != null) {
                                _nextProcess = new TribblesEndOfTurnGameProcess();
                                game.getActionsEnvironment().addActionToStack(action);
                            } else
                                _nextProcess = new TribblesPlayerDrawsAndCanPlayProcess(_playerId);
                        }
                    });
        }
    }

    @Override
    public GameProcess getNextProcess() {
        return _nextProcess;
    }
}
