package com.gempukku.lotro.processes.lotronly.archery;

import com.gempukku.lotro.actions.SystemQueueAction;
import com.gempukku.lotro.common.CardType;
import com.gempukku.lotro.effects.choose.ChooseAndWoundCharactersEffect;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.DefaultGame;
import com.gempukku.lotro.processes.GameProcess;

public class ShadowPlayerAssignsArcheryDamageGameProcess implements GameProcess {
    private final String _playerId;
    private final int _woundsToAssign;
    private final GameProcess _followingGameProcess;

    public ShadowPlayerAssignsArcheryDamageGameProcess(String playerId, int woundsToAssign, GameProcess followingGameProcess) {
        _playerId = playerId;
        _woundsToAssign = woundsToAssign;
        _followingGameProcess = followingGameProcess;
    }

    @Override
    public void process(DefaultGame game) {
        if (_woundsToAssign > 0) {
            Filter filter =
                    Filters.and(
                            CardType.MINION,
                            Filters.owner(_playerId),
                            (Filter) (game1, physicalCard) -> game1.getModifiersQuerying().canTakeArcheryWound(game1, physicalCard));

            SystemQueueAction action = new SystemQueueAction();
            for (int i = 0; i < _woundsToAssign; i++) {
                final int woundsLeft = _woundsToAssign - i;
                ChooseAndWoundCharactersEffect woundCharacter = new ChooseAndWoundCharactersEffect(action, _playerId, 1, 1, filter);
                woundCharacter.setSourceText("Archery Fire");
                woundCharacter.setChoiceText("Choose minion to assign archery wound to - remaining wounds: " + woundsLeft);
                action.appendEffect(woundCharacter);
            }

            game.getActionsEnvironment().addActionToStack(action);
        }
    }

    @Override
    public GameProcess getNextProcess() {
        return _followingGameProcess;
    }
}
