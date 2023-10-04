package com.gempukku.lotro.actions;

import com.gempukku.lotro.cards.PhysicalCard;
import com.gempukku.lotro.common.Quadrant;
import com.gempukku.lotro.common.Region;
import com.gempukku.lotro.common.Zone;
import com.gempukku.lotro.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.lotro.effects.Effect;
import com.gempukku.lotro.effects.PlayMissionEffect;
import com.gempukku.lotro.game.ST1EGame;
import com.gempukku.lotro.gamestate.ST1EGameState;
import com.gempukku.lotro.rules.GameUtils;

import java.util.Collections;
import java.util.Objects;

public class PlayMissionAction extends AbstractCostToEffectAction<ST1EGame> {
    private final PhysicalCard _missionPlayed;
    private boolean _cardRemoved;
    private Effect _playCardEffect;
    private boolean _cardPlayed;
    private int _locationZoneIndex;
    private final Zone _fromZone;

    public PlayMissionAction(PhysicalCard missionPlayed) {
        _missionPlayed = missionPlayed;
        setText("Play " + GameUtils.getFullName(_missionPlayed));
        setPerformingPlayer(_missionPlayed.getOwner());
        _fromZone = _missionPlayed.getZone();
    }
    
    @Override
    public PhysicalCard getActionSource() {
        return _missionPlayed;
    }

    public ActionType getActionType() { return ActionType.PLAY_CARD; }
    
    @Override
    public PhysicalCard getActionAttachedToCard() {
        return _missionPlayed;
    }

    @Override
    public Effect<ST1EGame> nextEffect(ST1EGame game) {
        Quadrant quadrant = _missionPlayed.getBlueprint().getQuadrant();
        String missionLocation = _missionPlayed.getBlueprint().getLocation();
        Region region = _missionPlayed.getBlueprint().getRegion();
        String playerId = getPerformingPlayer();
        ST1EGameState gameState = game.getGameState();

        if (!gameState.spacelineExists(quadrant)) {
            gameState.createNewSpaceline(quadrant);
        }

        boolean sharedMission = game.getGameState().spacelineHasLocation(missionLocation, quadrant) &&
                _missionPlayed.getBlueprint().isUniversal();

        if (gameState.getSpaceline(quadrant).size() == 0) {
            _locationZoneIndex = 0;
            indexSelected(game,false);
        } else if (sharedMission) {
            _locationZoneIndex = gameState.indexOfLocation(missionLocation, quadrant);
            indexSelected(game,true);
        } else if (gameState.spacelineHasRegion(region, quadrant)) {
            String[] directions = {"LEFT", "RIGHT"};
            game.getUserFeedback().sendAwaitingDecision(playerId,
                new MultipleChoiceAwaitingDecision(1, "Insert on which end of the region?", directions) {
                    @Override
                    protected void validDecisionMade(int index, String result) {
                        PhysicalCard firstRegionMission = gameState.firstInRegion(region, quadrant);
                        PhysicalCard lastRegionMission = gameState.lastInRegion(region, quadrant);
                        if (Objects.equals(result, "LEFT")) {
                            _locationZoneIndex = firstRegionMission.getLocationZoneIndex();
                        } else {
                            _locationZoneIndex = lastRegionMission.getLocationZoneIndex() + 1;
                        }
                        indexSelected(game,false);
                    }
                });
        } else if (_missionPlayed.canInsertIntoSpaceline() && gameState.getSpaceline(quadrant).size() >= 2) {
            // TODO: canInsertIntoSpaceline method not defined
        } else {
            String[] directions = {"LEFT", "RIGHT"};
            game.getUserFeedback().sendAwaitingDecision(playerId,
                    new MultipleChoiceAwaitingDecision(1, "Insert on which end of the quadrant?", directions) {
                        @Override
                        protected void validDecisionMade(int index, String result) {
                            if (Objects.equals(result, "LEFT")) {
                                _locationZoneIndex = 0;
                            } else {
                                _locationZoneIndex = gameState.getSpaceline(quadrant).size();
                            }
                            indexSelected(game,false);
                        }
                    });
        }
        return getNextEffect();
    }

    public void indexSelected(ST1EGame game, boolean sharedMission) {
        if (!_cardRemoved) {
            _cardRemoved = true;
            final Zone playedFromZone = _missionPlayed.getZone();
            game.getGameState().sendMessage(_missionPlayed.getOwner() + " plays " +
                    GameUtils.getCardLink(_missionPlayed) +  " from " + playedFromZone.getHumanReadable());
            game.getGameState().removeCardsFromZone(_missionPlayed.getOwner(),
                    Collections.singleton(_missionPlayed));
            game.getGameState().addCardToZone(game, _missionPlayed, Zone.VOID);
        }

        if (!_cardPlayed) {
            _cardPlayed = true;
            Quadrant quadrant = _missionPlayed.getQuadrant();
            _playCardEffect = new PlayMissionEffect(_fromZone, _missionPlayed, quadrant, _locationZoneIndex);
//            return _playCardEffect;
        }

    }

    public boolean wasCarriedOut() {
        return _cardPlayed && _playCardEffect != null && _playCardEffect.wasCarriedOut();
    }
}







    // TODO: Following player order, starting with first player

    /* The starting player draws the top mission from his or her pile and places it face up on the table,
        beginning the first spaceline. */


// TODO: Empok Nor can seed during mission phase at a universal mission
// TODO: Other actions may happen during mission phase (such as activating Save Stranded Crew text to download All-Consuming Evil)

// Code below borrowed from other process
/*        final List<Action> playableActions = game.getActionsEnvironment().getPhaseActions(_playerId);

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
        }*/


// The second player then draws and places his or her first mission.
// A mission can be placed on either end of the appropriate spaceline.
// If it is the first mission in the quadrant, it is placed on a new spaceline, separate from the others.
        /* Cards that specify they are INSERTED into the spaceline may be placed anywhere in their native quadrant,
            including between two missions already seeded. */
// This continues until both players are finished.

// Cards that specify they are part of a region must be next to each other, forming a single, contiguous region within the quadrant.
// The first location in a region is placed normally.
// Subsequent locations within that region may be inserted into the spaceline at either end of the region.

// If two players seed the same location in the same quadrant, it becomes a shared mission.
// The first version to appear is seeded normally, but the second version is placed on top of the original, wherever it is on the spaceline, leaving half of the original exposed.
// The two missions form only one location and may be completed only once.
// Each player uses their own mission card for gameplay purposes; players may not use the "opponent's end" of their opponent's mission card at a shared mission.

// Missions with the Universal symbol may seed multiple times as multiple locations.
