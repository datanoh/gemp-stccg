package com.gempukku.stccg.draft;

import com.gempukku.stccg.collection.CardCollection;

public interface DraftChannelVisitor {
    void channelNumber(int channelNumber);
    void timeLeft(long timeLeft);
    void cardChoice(CardCollection cardCollection);
    void noCardChoice();
    void chosenCards(CardCollection cardCollection);
}
