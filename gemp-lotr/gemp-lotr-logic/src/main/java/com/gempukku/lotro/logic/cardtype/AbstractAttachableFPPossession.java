package com.gempukku.lotro.logic.cardtype;

import com.gempukku.lotro.common.CardType;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.PossessionClass;
import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.modifiers.StrengthModifier;
import com.gempukku.lotro.logic.modifiers.VitalityModifier;
import com.gempukku.lotro.logic.modifiers.evaluator.ConstantEvaluator;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractAttachableFPPossession extends AbstractAttachable {
    private int _strength;
    private int _vitality;

    public AbstractAttachableFPPossession(int twilight, int strength, int vitality, Culture culture, PossessionClass possessionClass, String name) {
        this(twilight, strength, vitality, culture, possessionClass, name, null, false);
    }

    public AbstractAttachableFPPossession(int twilight, int strength, int vitality, Culture culture, PossessionClass possessionClass, String name, String subTitle, boolean unique) {
        this(twilight, strength, vitality, culture, CardType.POSSESSION, possessionClass, name, subTitle, unique);
    }

    public AbstractAttachableFPPossession(int twilight, int strength, int vitality, Culture culture, CardType cardType, PossessionClass possessionClass, String name, String subTitle, boolean unique) {
        super(Side.FREE_PEOPLE, cardType, twilight, culture, possessionClass, name, subTitle, unique);
        _strength = strength;
        _vitality = vitality;
    }

    @Override
    public final List<? extends Modifier> getInPlayModifiers(LotroGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        if (_strength != 0)
            modifiers.add(new StrengthModifier(self, Filters.hasAttached(self), null, new ConstantEvaluator(_strength), true));
        if (_vitality != 0)
            modifiers.add(new VitalityModifier(self, Filters.hasAttached(self), _vitality, true));

        List<? extends Modifier> extraModifiers = getNonBasicStatsModifiers(self);
        if (extraModifiers != null)
            modifiers.addAll(extraModifiers);

        return modifiers;
    }

    protected List<? extends Modifier> getNonBasicStatsModifiers(PhysicalCard self) {
        return null;
    }
}
