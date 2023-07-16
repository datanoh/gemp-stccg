package com.gempukku.lotro.at;

import com.gempukku.lotro.cards.lotronly.LotroPhysicalCardImpl;
import com.gempukku.lotro.common.Phase;
import com.gempukku.lotro.common.Token;
import com.gempukku.lotro.common.Zone;
import com.gempukku.lotro.cards.CardNotFoundException;
import com.gempukku.lotro.cards.PhysicalCard;
import com.gempukku.lotro.game.decisions.AwaitingDecisionType;
import com.gempukku.lotro.game.decisions.DecisionResultInvalidException;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

public class NewCardsAtTest extends AbstractAtTest {
    @Test
    public void exertAsCost() throws DecisionResultInvalidException, CardNotFoundException {
        initializeSimplestGame();

        LotroPhysicalCardImpl gimli = new LotroPhysicalCardImpl(100, "1_13", P1, _cardLibrary.getLotroCardBlueprint("1_13"));
        LotroPhysicalCardImpl inquisitor = new LotroPhysicalCardImpl(100, "1_268", P2, _cardLibrary.getLotroCardBlueprint("1_268"));

        _game.getGameState().addCardToZone(_game, gimli, Zone.FREE_CHARACTERS);
        _game.getGameState().addCardToZone(_game, inquisitor, Zone.SHADOW_CHARACTERS);

        _game.getGameState().addTokens(gimli, Token.WOUND, 2);

        skipMulligans();

        // Pass in fellowship
        playerDecided(P1, "");

        // Play inquisitor
        playerDecided(P2, "0");
        playerDecided(P2, "");

        // Pass in maneuver
        playerDecided(P1, "");
        playerDecided(P2, "");

        // Pass in archery
        playerDecided(P1, "");
        playerDecided(P2, "");

        // Pass in assignment
        playerDecided(P1, "");
        playerDecided(P2, "");

        // Assign minion to Frodo
        playerDecided(P1, gimli.getCardId() + " " + inquisitor.getCardId());

        // Start skirmish
        playerDecided(P1, String.valueOf(gimli.getCardId()));

        assertEquals(0, _userFeedback.getAwaitingDecision(P1).getDecisionParameters().get("actionId").length);
    }

    @Test
    public void reduceArcheryTotal() throws DecisionResultInvalidException, CardNotFoundException {
        initializeSimplestGame();

        LotroPhysicalCardImpl legolas = new LotroPhysicalCardImpl(100, "40_52", P1, _cardLibrary.getLotroCardBlueprint("40_52"));
        LotroPhysicalCardImpl arrowsOfLight = new LotroPhysicalCardImpl(100, "40_33", P1, _cardLibrary.getLotroCardBlueprint("40_33"));
        LotroPhysicalCardImpl inquisitor = new LotroPhysicalCardImpl(100, "1_268", P2, _cardLibrary.getLotroCardBlueprint("1_268"));

        _game.getGameState().addCardToZone(_game, inquisitor, Zone.SHADOW_CHARACTERS);

        _game.getGameState().addCardToZone(_game, legolas, Zone.FREE_CHARACTERS);
        _game.getGameState().addCardToZone(_game, arrowsOfLight, Zone.HAND);

        skipMulligans();

        // Pass in fellowship
        playerDecided(P1, "");

        // Pass in shadow
        playerDecided(P2, "");

        // Pass in maneuver
        playerDecided(P1, "");
        playerDecided(P2, "");

        // Play ArrowsOfLight
        playerDecided(P1, "0");
        playerDecided(P1, "0");
    }

    @Test
    public void playedTrigger() throws DecisionResultInvalidException, CardNotFoundException {
        initializeSimplestGame();

        LotroPhysicalCardImpl bruinenUnleashed = new LotroPhysicalCardImpl(100, "40_37", P1, _cardLibrary.getLotroCardBlueprint("40_37"));
        LotroPhysicalCardImpl legolas = new LotroPhysicalCardImpl(100, "40_52", P1, _cardLibrary.getLotroCardBlueprint("40_52"));
        LotroPhysicalCardImpl nazgul = new LotroPhysicalCardImpl(100, "40_211", P2, _cardLibrary.getLotroCardBlueprint("40_211"));

        _game.getGameState().addCardToZone(_game, legolas, Zone.FREE_CHARACTERS);
        _game.getGameState().addCardToZone(_game, bruinenUnleashed, Zone.SUPPORT);
        _game.getGameState().addCardToZone(_game, nazgul, Zone.HAND);

        skipMulligans();

        _game.getGameState().setTwilight(10);

        // Pass in fellowship
        playerDecided(P1, "");

        // Pass in shadow
        playerDecided(P2, "0");

        assertEquals(1, _game.getGameState().getWounds(nazgul));
    }

    @Test
    public void choiceEffect() throws DecisionResultInvalidException, CardNotFoundException {
        initializeSimplestGame();

        LotroPhysicalCardImpl getOutOfTheShire = createCard(P1, "40_320");
        LotroPhysicalCardImpl merry = createCard(P1, "40_256");
        LotroPhysicalCardImpl nazgul = createCard(P2, "40_211");

        _game.getGameState().addCardToZone(_game, getOutOfTheShire, Zone.HAND);
        _game.getGameState().addCardToZone(_game, merry, Zone.FREE_CHARACTERS);
        _game.getGameState().addCardToZone(_game, nazgul, Zone.SHADOW_CHARACTERS);

        skipMulligans();

        // Pass in fellowship
        playerDecided(P1, "");

        // Pass in shadow
        playerDecided(P2, "");

        // Pass in maneuver
        playerDecided(P1, "");
        playerDecided(P2, "");

        // Pass in archery
        playerDecided(P1, "");
        playerDecided(P2, "");

        // Pass in assignment
        playerDecided(P1, "");
        playerDecided(P2, "");

        playerDecided(P1, merry.getCardId() + " " + nazgul.getCardId());

        playerDecided(P1, "" + merry.getCardId());

        // Play Get Out of the Shire
        playerDecided(P1, "0");

        // Choose to cancel skirmish
        playerDecided(P1, "1");

        // We're in Fierce skirmishes
        assertEquals(Phase.ASSIGNMENT, _game.getGameState().getCurrentPhase());
        assertEquals(0, _game.getGameState().getWounds(merry));
        assertEquals(Zone.FREE_CHARACTERS, merry.getZone());
    }

    @Test
    public void conditionalEffect() throws DecisionResultInvalidException, CardNotFoundException {
        initializeSimplestGame();

        LotroPhysicalCardImpl getOutOfTheShire = createCard(P1, "40_320");
        LotroPhysicalCardImpl nazgul = createCard(P2, "40_211");

        _game.getGameState().addCardToZone(_game, getOutOfTheShire, Zone.HAND);
        _game.getGameState().addCardToZone(_game, nazgul, Zone.SHADOW_CHARACTERS);

        final PhysicalCard frodo = _game.getGameState().getRingBearer(P1);

        skipMulligans();

        // Pass in fellowship
        playerDecided(P1, "");

        // Pass in shadow
        playerDecided(P2, "");

        // Pass in maneuver
        playerDecided(P1, "");
        playerDecided(P2, "");

        // Pass in archery
        playerDecided(P1, "");
        playerDecided(P2, "");

        // Pass in assignment
        playerDecided(P1, "");
        playerDecided(P2, "");

        playerDecided(P1, frodo.getCardId() + " " + nazgul.getCardId());

        playerDecided(P1, "" + frodo.getCardId());

        // Play Get Out of the Shire
        playerDecided(P1, "0");

        // No choice given
        assertNull(_userFeedback.getAwaitingDecision(P1));
    }

    @Test
    public void preventableEffect() throws DecisionResultInvalidException, CardNotFoundException {
        initializeSimplestGame();

        LotroPhysicalCardImpl boromir = createCard(P1, "40_101");
        LotroPhysicalCardImpl gauntlets = createCard(P1, "40_103");
        LotroPhysicalCardImpl nazgul = createCard(P2, "40_211");

        _game.getGameState().addCardToZone(_game, boromir, Zone.SUPPORT);
        _game.getGameState().attachCard(_game, gauntlets, boromir);
        _game.getGameState().addCardToZone(_game, nazgul, Zone.SHADOW_CHARACTERS);

        skipMulligans();

        // Pass in fellowship
        playerDecided(P1, "");

        // Pass in shadow
        playerDecided(P2, "");

        // Pass in maneuver
        playerDecided(P1, "");
        playerDecided(P2, "");

        // Pass in archery
        playerDecided(P1, "");
        playerDecided(P2, "");

        // Pass in assignment
        playerDecided(P1, "");
        playerDecided(P2, "");

        playerDecided(P1, boromir.getCardId() + " " + nazgul.getCardId());

        playerDecided(P1, "" + boromir.getCardId());

        final int twilightPool = _game.getGameState().getTwilightPool();

        // Use Gauntlets
        playerDecided(P1, "0");

        playerDecided(P2, "0");

        assertEquals(twilightPool - 1, _game.getGameState().getTwilightPool());
    }

    @Test
    public void costToEffect() throws DecisionResultInvalidException, CardNotFoundException {
        initializeSimplestGame();

        LotroPhysicalCardImpl celeborn = createCard(P1, "40_38");
        LotroPhysicalCardImpl celebornInDeck = createCard(P1, "40_38");
        LotroPhysicalCardImpl nazgul = createCard(P2, "40_211");

        _game.getGameState().addCardToZone(_game, celeborn, Zone.SUPPORT);
        _game.getGameState().putCardOnTopOfDeck(celebornInDeck);
        _game.getGameState().addCardToZone(_game, nazgul, Zone.SHADOW_CHARACTERS);

        skipMulligans();

        // Pass in fellowship
        playerDecided(P1, "");

        // Pass in shadow
        playerDecided(P2, "");

        // Use Celeborn
        playerDecided(P1, "0");
        // Pass on reveal
        playerDecided(P1, "");
        playerDecided(P2, "");

        // Choose to discard
        playerDecided(P1, "0");

        assertEquals(Zone.DISCARD, celebornInDeck.getZone());
        assertEquals(1, _game.getGameState().getWounds(nazgul));
    }

    @Test
    public void costToEffectPass() throws DecisionResultInvalidException, CardNotFoundException {
        initializeSimplestGame();

        LotroPhysicalCardImpl celeborn = createCard(P1, "40_38");
        LotroPhysicalCardImpl celebornInDeck = createCard(P1, "40_38");
        LotroPhysicalCardImpl nazgul = createCard(P2, "40_211");

        _game.getGameState().addCardToZone(_game, celeborn, Zone.SUPPORT);
        _game.getGameState().putCardOnTopOfDeck(celebornInDeck);
        _game.getGameState().addCardToZone(_game, nazgul, Zone.SHADOW_CHARACTERS);

        skipMulligans();

        // Pass in fellowship
        playerDecided(P1, "");

        // Pass in shadow
        playerDecided(P2, "");

        // Use Celeborn
        playerDecided(P1, "0");
        // Pass on reveal
        playerDecided(P1, "");
        playerDecided(P2, "");

        // Choose not to discard
        playerDecided(P1, "1");

        assertEquals(Zone.DECK, celebornInDeck.getZone());
        assertEquals(0, _game.getGameState().getWounds(nazgul));
    }

    @Test
    public void checkingEventCostsAsRequirements() throws DecisionResultInvalidException, CardNotFoundException {
        initializeSimplestGame();

        final LotroPhysicalCardImpl battleFever = createCard(P1, "40_5");
        final LotroPhysicalCardImpl gimli = createCard(P1, "40_18");
        LotroPhysicalCardImpl nazgul = createCard(P2, "40_211");


        _game.getGameState().addCardToZone(_game, gimli, Zone.FREE_CHARACTERS);
        _game.getGameState().addCardToZone(_game, battleFever, Zone.HAND);
        _game.getGameState().addCardToZone(_game, nazgul, Zone.SHADOW_CHARACTERS);

        skipMulligans();

        // Pass in fellowship
        playerDecided(P1, "");

        // Pass in shadow
        playerDecided(P2, "");

//Pass in maneuver
        playerDecided(P1, "");
        playerDecided(P2, "");

//Pass in archery
        playerDecided(P1, "");
        playerDecided(P2, "");

//Pass in assignment
        playerDecided(P1, "");
        playerDecided(P2, "");

        playerDecided(P1, gimli.getCardId() + " " + nazgul.getCardId());
        playerDecided(P1, "" + gimli.getCardId());

        assertNull(getCardActionId(P1, "Play Battle Fever"));
    }

    @Test
    public void discardCardEffect() throws DecisionResultInvalidException, CardNotFoundException {
        initializeSimplestGame();

        final LotroPhysicalCardImpl blackBreath = createCard(P2, "40_183");
        final LotroPhysicalCardImpl athelas = createCard(P1, "40_313");
        final LotroPhysicalCardImpl athelasInHand = createCard(P1, "40_313");
        LotroPhysicalCardImpl aragorn = createCard(P1, "40_94");

        _game.getGameState().addCardToZone(_game, aragorn, Zone.FREE_CHARACTERS);
        _game.getGameState().attachCard(_game, blackBreath, aragorn);
        _game.getGameState().addCardToZone(_game, athelas, Zone.HAND);
        _game.getGameState().addCardToZone(_game, athelasInHand, Zone.HAND);

        skipMulligans();

        // Play athelas
        playerDecided(P1, "0");
        // Attach to Aragorn
        playerDecided(P1, "" + aragorn.getCardId());

        playerDecided(P1, "0");

        playerDecided(P1, "0");

        assertEquals(Zone.DISCARD, blackBreath.getZone());
    }

    @Test
    public void strengthBonusDependingOnCharacterPlayedOn() throws DecisionResultInvalidException, CardNotFoundException {
        initializeSimplestGame();

        final LotroPhysicalCardImpl gandalf = createCard(P1, "40_70");
        final LotroPhysicalCardImpl bolsteredSpirits = createCard(P1, "40_67");
        LotroPhysicalCardImpl nazgul = createCard(P2, "40_211");

        _game.getGameState().addCardToZone(_game, gandalf, Zone.FREE_CHARACTERS);
        _game.getGameState().addCardToZone(_game, bolsteredSpirits, Zone.HAND);
        _game.getGameState().addCardToZone(_game, nazgul, Zone.SHADOW_CHARACTERS);

        skipMulligans();

        // Pass in fellowship
        playerDecided(P1, "");

        // Pass in shadow
        playerDecided(P2, "");

//Pass in maneuver
        playerDecided(P1, "");
        playerDecided(P2, "");

//Pass in archery
        playerDecided(P1, "");
        playerDecided(P2, "");

//Pass in assignment
        playerDecided(P1, "");
        playerDecided(P2, "");

        playerDecided(P1, gandalf.getCardId() + " " + nazgul.getCardId());
        playerDecided(P1, "" + gandalf.getCardId());

        playerDecided(P1, "0");
        playerDecided(P1, "");

        assertEquals(7 + 3, _game.getModifiersQuerying().getStrength(_game, gandalf));
    }

    @Test
    public void strengthBonusDependingOnCharacterPlayedOn2() throws DecisionResultInvalidException, CardNotFoundException {
        initializeSimplestGame();

        final LotroPhysicalCardImpl gandalf = createCard(P1, "40_70");
        final LotroPhysicalCardImpl boromir = createCard(P1, "40_101");
        final LotroPhysicalCardImpl goBackToTheShadows = createCard(P1, "40_312");
        final LotroPhysicalCardImpl nazgul = createCard(P2, "40_211");

        _game.getGameState().addCardToZone(_game, gandalf, Zone.FREE_CHARACTERS);
        _game.getGameState().addCardToZone(_game, boromir, Zone.FREE_CHARACTERS);
        _game.getGameState().addCardToZone(_game, goBackToTheShadows, Zone.HAND);
        _game.getGameState().addCardToZone(_game, nazgul, Zone.SHADOW_CHARACTERS);
        _game.getGameState().removeBurdens(_game.getGameState().getBurdens());

        skipMulligans();

        // Pass in fellowship
        playerDecided(P1, "");

        // Pass in shadow
        playerDecided(P2, "");

//Pass in maneuver
        playerDecided(P1, "");
        playerDecided(P2, "");

//Pass in archery
        playerDecided(P1, "");
        playerDecided(P2, "");

//Pass in assignment
        playerDecided(P1, "");
        playerDecided(P2, "");

        playerDecided(P1, boromir.getCardId() + " " + nazgul.getCardId());
        playerDecided(P1, "" + boromir.getCardId());

        playerDecided(P1, "0");

        assertEquals(14 - 3, _game.getModifiersQuerying().getStrength(_game, nazgul));
    }

    @Test
    public void mathProgrammingDiscardBoth() throws DecisionResultInvalidException, CardNotFoundException {
        initializeSimplestGame();

        final LotroPhysicalCardImpl gandalf = createCard(P1, "40_70");
        final LotroPhysicalCardImpl discerment = createCard(P1, "40_68");
        final LotroPhysicalCardImpl blackBreath = createCard(P2, "40_183");
        final LotroPhysicalCardImpl blackBreath2 = createCard(P2, "40_183");

        _game.getGameState().addCardToZone(_game, gandalf, Zone.FREE_CHARACTERS);
        _game.getGameState().addCardToZone(_game, discerment, Zone.HAND);
        _game.getGameState().attachCard(_game, blackBreath, gandalf);
        _game.getGameState().attachCard(_game, blackBreath2, gandalf);

        skipMulligans();

        playerDecided(P1, "0");
        playerDecided(P1, "2");
        playerDecided(P1, "");

        playerDecided(P1, "" + blackBreath.getCardId());
        playerDecided(P1, "" + blackBreath2.getCardId());

        assertEquals(Zone.DISCARD, blackBreath.getZone());
        assertEquals(Zone.DISCARD, blackBreath2.getZone());
    }

    @Test
    public void mathProgrammingDiscardOne() throws DecisionResultInvalidException, CardNotFoundException {
        initializeSimplestGame();

        final LotroPhysicalCardImpl gandalf = createCard(P1, "40_70");
        final LotroPhysicalCardImpl discerment = createCard(P1, "40_68");
        final LotroPhysicalCardImpl blackBreath = createCard(P2, "40_183");
        final LotroPhysicalCardImpl blackBreath2 = createCard(P2, "40_183");

        _game.getGameState().addCardToZone(_game, gandalf, Zone.FREE_CHARACTERS);
        _game.getGameState().addCardToZone(_game, discerment, Zone.HAND);
        _game.getGameState().attachCard(_game, blackBreath, gandalf);
        _game.getGameState().attachCard(_game, blackBreath2, gandalf);

        skipMulligans();

        playerDecided(P1, "0");
        playerDecided(P1, "1");
        playerDecided(P1, "");

        playerDecided(P1, "" + blackBreath.getCardId());
        assertEquals(AwaitingDecisionType.CARD_ACTION_CHOICE, _userFeedback.getAwaitingDecision(P1).getDecisionType());

        assertEquals(Zone.DISCARD, blackBreath.getZone());
        assertEquals(Zone.ATTACHED, blackBreath2.getZone());
    }

    @Test
    public void strengthBonus() throws DecisionResultInvalidException, CardNotFoundException {
        initializeSimplestGame();

        final LotroPhysicalCardImpl grimbeorn = createCard(P1, "14_6");
        final LotroPhysicalCardImpl nazgulInHand = createCard(P1, "40_211");
        final LotroPhysicalCardImpl nazgul = createCard(P2, "40_211");

        _game.getGameState().addCardToZone(_game, grimbeorn, Zone.FREE_CHARACTERS);
        _game.getGameState().addCardToZone(_game, nazgul, Zone.SHADOW_CHARACTERS);
        _game.getGameState().addCardToZone(_game, nazgulInHand, Zone.HAND);

        skipMulligans();

        playerDecided(P1, "");
        playerDecided(P2, "");

        playerDecided(P1, "0");
        playerDecided(P1, "");
    }

    @Test
    public void roamingDiscount() throws DecisionResultInvalidException, CardNotFoundException {
        initializeSimplestGame();

        final LotroPhysicalCardImpl orcAssassin1 = createCard(P2, "1_262");
        final LotroPhysicalCardImpl orcAssassin2 = createCard(P2, "1_262");

        _game.getGameState().addCardToZone(_game, orcAssassin1, Zone.HAND);
        _game.getGameState().addCardToZone(_game, orcAssassin2, Zone.HAND);

        skipMulligans();

        _game.getGameState().setTwilight(10);

        playerDecided(P1, "");

        int twilight = _game.getGameState().getTwilightPool();
        playerDecided(P2, getCardActionId(P2, "Play Orc Assassin"));
        assertEquals(twilight - 4, _game.getGameState().getTwilightPool());

        playerDecided(P2, getCardActionId(P2, "Play Orc Assassin"));
        assertEquals(twilight - 4 - 3, _game.getGameState().getTwilightPool());

        assertEquals(Zone.SHADOW_CHARACTERS, orcAssassin1.getZone());
        assertEquals(Zone.SHADOW_CHARACTERS, orcAssassin2.getZone());
    }

    @Test
    public void spotCountChange() throws DecisionResultInvalidException, CardNotFoundException {
        initializeSimplestGame();

        final LotroPhysicalCardImpl merry = createCard(P1, "40_257");
        final LotroPhysicalCardImpl bilbosPipe = createCard(P1, "40_244");
        final LotroPhysicalCardImpl pipeweed = createCard(P1, "40_255");

        _game.getGameState().addCardToZone(_game, merry, Zone.HAND);
        _game.getGameState().addCardToZone(_game, pipeweed, Zone.SUPPORT);
        _game.getGameState().addCardToZone(_game, bilbosPipe, Zone.HAND);

        skipMulligans();

        playerDecided(P1, getCardActionId(P1, "Play Merry"));
        playerDecided(P1, getCardActionId(P1, "Play Bilbo's Pipe"));

        playerDecided(P1, "" + merry.getCardId());
        playerDecided(P1, getCardActionId(P1, "Use Bilbo's Pipe"));
        assertEquals("2", _userFeedback.getAwaitingDecision(P1).getDecisionParameters().get("max")[0]);
    }

    @Test
    public void removeBurdens() throws DecisionResultInvalidException, CardNotFoundException {
        initializeSimplestGame();

        final LotroPhysicalCardImpl samsPipe = createCard(P1, "40_269");
        final LotroPhysicalCardImpl pipeweed = createCard(P1, "40_255");

        _game.getGameState().addCardToZone(_game, pipeweed, Zone.SUPPORT);
        _game.getGameState().attachCard(_game, samsPipe, _game.getGameState().getRingBearer(P1));

        skipMulligans();

        playerDecided(P1, getCardActionId(P1, "Use Sam's Pipe"));
        playerDecided(P1, "1");

        assertEquals(0, _game.getGameState().getBurdens());
    }
}
