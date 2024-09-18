package com.gempukku.stccg.gamestate;

import com.gempukku.stccg.cards.CardDeck;
import com.gempukku.stccg.cards.physicalcard.PhysicalCard;
import com.gempukku.stccg.common.filterable.CardType;
import com.gempukku.stccg.common.filterable.Zone;
import com.gempukku.stccg.decisions.AwaitingDecision;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public class GameEvent {
    public enum Type {
        PARTICIPANTS("P"), GAME_PHASE_CHANGE("GPC"), TURN_CHANGE("TC"), PLAYER_POSITION("PP"),
        PUT_SHARED_MISSION_INTO_PLAY("PUT_SHARED_MISSION_INTO_PLAY"),
        TWILIGHT_POOL_UPDATE("TP"),
        TRIBBLE_SEQUENCE_UPDATE("TSEQ"),
        PLAYER_DECKED("PLAYER_DECKED"), // TODO: Not implemented in JavaScript
        PLAYER_SCORE("PLAYER_SCORE"), // TODO: Not implemented in JavaScript
        PUT_CARD_INTO_PLAY("PCIP"),
        PUT_CARD_INTO_PLAY_WITHOUT_ANIMATING("PCIPAR"),
        MOVE_CARD_IN_PLAY("MCIP"), REMOVE_CARD_FROM_PLAY("RCFP"),
        ADD_ASSIGNMENT("AA"), REMOVE_ASSIGNMENT("RA"),
        START_SKIRMISH("SS"), REMOVE_FROM_SKIRMISH("RFS"), ADD_TO_SKIRMISH("ATS"), END_SKIRMISH("ES"),
        SEND_MESSAGE("M"), SEND_WARNING("W"),
        GAME_STATS("GS"),
        CHAT_MESSAGE("CM"),
        GAME_ENDED("EG"),
        UPDATE_CARD_IMAGE("UPDATE_CARD_IMAGE"),
        CARD_AFFECTED_BY_CARD("CAC"), SHOW_CARD_ON_SCREEN("EP"), FLASH_CARD_IN_PLAY("CA"), DECISION("D");

        private final String code;

        Type(String code) {
            this.code = code;
        }

        String getCode() {
            return code;
        }
    }

    private String _message;
    private String _side;
    private final Type _type;
    private Zone _zone;
    private String _participantId;
    private Integer _locationIndex;
    private String _quadrant;
    private String _controllerId;
    private List<String> _allParticipantIds;
    private Integer _index;
    private String _blueprintId;
    private Integer _cardId;
    private String _imageUrl;
    private Integer _targetCardId;
    private String _phase;
    private Integer _count;
    private Boolean _bool;
    private int[] _otherCardIds;
    private Map<String, CardDeck> _decks;
    private GameStats _gameStats;
    private AwaitingDecision _awaitingDecision;
    private final ZonedDateTime _timestamp;
    private Integer _version;
    private boolean _discardIsPublic;
    private Integer _score;

    public GameEvent(Type type) {
        _type = type;
        _timestamp = ZonedDateTime.now(ZoneOffset.UTC);
    }

    public ZonedDateTime getTimestamp() { return _timestamp; }
    public Integer getVersion() { return _version; }
    public GameEvent version(int version) {
        _version = version;
        return this;
    }

    public Integer getIndex() {
        return _index;
    }

    public GameEvent index(int index) {
        _index = index;
        return this;
    }

    public Type getType() {
        return _type;
    }

    public GameStats getGameStats() {
        return _gameStats;
    }

    public GameEvent gameStats(GameStats gameStats) {
        _gameStats = gameStats;
        return this;
    }

    public AwaitingDecision getAwaitingDecision() {
        return _awaitingDecision;
    }

    public GameEvent awaitingDecision(AwaitingDecision awaitingDecision) {
        _awaitingDecision = awaitingDecision;
        return this;
    }

    public Zone getZone() {
        return _zone;
    }

    public GameEvent zone(Zone zone) {
        _zone = zone;
        return this;
    }

    public String getMessage() {
        return _message;
    }

    public GameEvent message(String message) {
        _message = message;
        return this;
    }

    public Integer getCount() {
        return _count;
    }

    public GameEvent count(int count) {
        _count = count;
        return this;
    }

    public Boolean getBool() {
        return _bool;
    }

    public GameEvent bool(boolean bool) {
        _bool = bool;
        return this;
    }

    public int[] getOtherCardIds() {
        return _otherCardIds;
    }

    public GameEvent otherCardIds(int[] otherCardIds) {
        _otherCardIds = otherCardIds;
        return this;
    }

    public Map<String, CardDeck> getDecks() {
        return _decks;
    }

    public GameEvent decks(Map<String, CardDeck> decks) {
        _decks = decks;
        return this;
    }

    public String getParticipantId() {
        return _participantId;
    }

    public GameEvent participantId(String participantId) {
        _participantId = participantId;
        return this;
    }

    public List<String> getAllParticipantIds() {
        return _allParticipantIds;
    }

    public GameEvent allParticipantIds(List<String> allParticipantIds) {
        _allParticipantIds = allParticipantIds;
        return this;
    }

    public String getSide() {
        return _side;
    }

    public GameEvent side(String side) {
        _side = side;
        return this;
    }

    public String getControllerId() {
        return _controllerId;
    }

    public GameEvent controllerId(String controllerId) {
        _controllerId = controllerId;
        return this;
    }

    public GameEvent card(PhysicalCard physicalCard) {
        GameEvent gameEvent = cardId(physicalCard.getCardId()).blueprintId(physicalCard.getBlueprintId()).participantId(physicalCard.getOwnerName()).zone(physicalCard.getZone()).imageUrl(physicalCard.getImageUrl());
        if (physicalCard.getCardControllerPlayerId() != null)
            gameEvent = gameEvent.controllerId(physicalCard.getCardControllerPlayerId());

        PhysicalCard attachedTo = physicalCard.getAttachedTo();
        if (attachedTo != null)
            gameEvent = gameEvent.targetCardId(attachedTo.getCardId());

        PhysicalCard stackedOn = physicalCard.getStackedOn();
        if (stackedOn != null)
            gameEvent = gameEvent.targetCardId(stackedOn.getCardId());

        gameEvent = gameEvent.locationIndex(physicalCard.getLocationZoneIndex());
        if (physicalCard.getCardType() == CardType.MISSION) {
            gameEvent = gameEvent.quadrant(physicalCard.getQuadrant().name());
        }
        return gameEvent;
    }

    public String getBlueprintId() {
        return _blueprintId;
    }

    public GameEvent blueprintId(String blueprintId) {
        _blueprintId = blueprintId;
        return this;
    }

    public Integer getCardId() {
        return _cardId;
    }

    public GameEvent cardId(int cardId) {
        _cardId = cardId;
        return this;
    }

    public String getImageUrl() { return _imageUrl; }
    public GameEvent imageUrl(String imageUrl) {
        _imageUrl = imageUrl;
        return this;
    }

    public Integer getTargetCardId() {
        return _targetCardId;
    }

    public GameEvent targetCardId(int targetCardId) {
        _targetCardId = targetCardId;
        return this;
    }

    public String getPhase() {
        return _phase;
    }

    public GameEvent phase(String phase) {
        _phase = phase;
        return this;
    }

    public boolean isDiscardPublic() {
        return _discardIsPublic;
    }

    public GameEvent discardPublic(boolean discardPublic) {
        _discardIsPublic = discardPublic;
        return this;
    }

    public Integer getScore() {
        return _score;
    }

    public GameEvent score(Integer score) {
        _score = score;
        return this;
    }

    public String getQuadrant() { return _quadrant; }
    public GameEvent quadrant(String quadrant) {
        _quadrant = quadrant;
        return this;
    }

    public Integer getLocationIndex() { return _locationIndex; }
    public GameEvent locationIndex(int locationIndex) {
        _locationIndex = locationIndex;
        return this;
    }

}