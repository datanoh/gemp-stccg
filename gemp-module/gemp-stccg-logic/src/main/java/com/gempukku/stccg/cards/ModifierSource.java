package com.gempukku.stccg.cards;

import com.gempukku.stccg.modifiers.Modifier;

public interface ModifierSource {
    Modifier getModifier(ActionContext actionContext);
}
