package com.gempukku.lotro.cards;

import com.gempukku.lotro.actions.*;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.effects.Effect;
import com.gempukku.lotro.effects.EffectResult;
import com.gempukku.lotro.game.DefaultGame;
import com.gempukku.lotro.game.TribblesGame;
import com.gempukku.lotro.modifiers.ExtraPlayCost;
import com.gempukku.lotro.modifiers.Modifier;

import java.util.List;
import java.util.Set;

public interface LotroCardBlueprint {
    enum Direction {
        LEFT, RIGHT
    }

    Side getSide();

    CardType getCardType();

    Culture getCulture();

    Race getRace();

    Uniqueness getUniqueness();

    boolean isUnique();

    String getTitle();
    String getImageUrl();

    String getSubtitle();

    boolean hasKeyword(Keyword keyword);

    int getKeywordCount(Keyword keyword);

    Filterable getValidTargetFilter(String playerId, DefaultGame game, LotroPhysicalCard self);

    int getTwilightCost();

    int getTwilightCostModifier(DefaultGame game, LotroPhysicalCard self, LotroPhysicalCard target);

    int getStrength();

    int getVitality();

    int getResistance();

    int[] getAllyHomeSiteNumbers();
    int getTribbleValue();
    TribblePower getTribblePower();

    SitesBlock getAllyHomeSiteBlock();

    PlayEventAction getPlayEventCardAction(String playerId, DefaultGame game, LotroPhysicalCard self);

    List<? extends Modifier> getInPlayModifiers(DefaultGame game, LotroPhysicalCard self);

    List<? extends Modifier> getStackedOnModifiers(DefaultGame game, LotroPhysicalCard self);

    List<? extends Modifier> getInDiscardModifiers(DefaultGame game, LotroPhysicalCard self);

    List<? extends Modifier> getControlledSiteModifiers(DefaultGame game, LotroPhysicalCard self);

    boolean playRequirementsNotMet(DefaultGame game, LotroPhysicalCard self);

    List<? extends Action> getPhaseActionsInHand(String playerId, DefaultGame game, LotroPhysicalCard self);

    List<? extends Action> getPhaseActionsFromDiscard(String playerId, DefaultGame game, LotroPhysicalCard self);

    List<? extends ActivateCardAction> getPhaseActionsInPlay(String playerId, DefaultGame game, LotroPhysicalCard self);

    List<? extends ActivateCardAction> getPhaseActionsFromStacked(String playerId, DefaultGame game, LotroPhysicalCard self);

    List<RequiredTriggerAction> getRequiredBeforeTriggers(DefaultGame game, Effect effect, LotroPhysicalCard self);

    List<RequiredTriggerAction> getRequiredAfterTriggers(DefaultGame game, EffectResult effectResult, LotroPhysicalCard self);


    List<OptionalTriggerAction> getOptionalBeforeTriggers(String playerId, DefaultGame game, Effect effect, LotroPhysicalCard self);

    List<ActionSource> getOptionalAfterTriggers();

//    List<OptionalTriggerAction> getOptionalAfterTriggerActions(String playerId, DefaultGame game, EffectResult effectResult, LotroPhysicalCard self);


    List<? extends ActivateCardAction> getOptionalInPlayBeforeActions(String playerId, DefaultGame game, Effect effect, LotroPhysicalCard self);

    List<? extends ActivateCardAction> getOptionalInPlayAfterActions(String playerId, DefaultGame game, EffectResult effectResult, LotroPhysicalCard self);


    List<PlayEventAction> getPlayResponseEventAfterActions(String playerId, DefaultGame game, EffectResult effectResult, LotroPhysicalCard self);

    List<PlayEventAction> getPlayResponseEventBeforeActions(String playerId, DefaultGame game, Effect effect, LotroPhysicalCard self);


    List<OptionalTriggerAction> getOptionalInHandAfterTriggers(String playerId, DefaultGame game, EffectResult effectResult, LotroPhysicalCard self);


    RequiredTriggerAction getDiscardedFromPlayRequiredTrigger(DefaultGame game, LotroPhysicalCard self);

    OptionalTriggerAction getDiscardedFromPlayOptionalTrigger(String playerId, DefaultGame game, LotroPhysicalCard self);


    RequiredTriggerAction getKilledRequiredTrigger(DefaultGame game, LotroPhysicalCard self);

    OptionalTriggerAction getKilledOptionalTrigger(String playerId, DefaultGame game, LotroPhysicalCard self);

    SitesBlock getSiteBlock();

    int getSiteNumber();

    Set<PossessionClass> getPossessionClasses();

    Direction getSiteDirection();

    String getDisplayableInformation(LotroPhysicalCard self);

    List<? extends ExtraPlayCost> getExtraCostToPlay(DefaultGame game, LotroPhysicalCard self);

    int getPotentialDiscount(DefaultGame game, String playerId, LotroPhysicalCard self);

    void appendPotentialDiscountEffects(DefaultGame game, CostToEffectAction action, String playerId, LotroPhysicalCard self);

    boolean canPayAidCost(DefaultGame game, LotroPhysicalCard self);

    void appendAidCosts(DefaultGame game, CostToEffectAction action, LotroPhysicalCard self);

    List<FilterableSource> getCopiedFilters();

    boolean canPlayOutOfSequence(TribblesGame game, LotroPhysicalCard self);
}
