package com.gempukku.stccg.rules.st1e;

import com.gempukku.stccg.game.ST1EGame;
import com.gempukku.stccg.rules.generic.AbstractRule;

public abstract class ST1ERule extends AbstractRule {
    protected final ST1EGame _game;

    public ST1ERule(ST1EGame game) {
        _game = game;
    }
}