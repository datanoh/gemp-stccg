package com.gempukku.lotro.logic.vo;

import java.util.ArrayList;
import java.util.List;

public class TribblesDeck {
    private final List<String> _Cards = new ArrayList<>();
    private final String _deckName;
    private String _notes;
    private String _targetFormat = "Anything Goes";
    public TribblesDeck(String deckName) {
        _deckName = deckName;
    }
    public String getDeckName() {
        return _deckName;
    }
    public void addCard(String card) {
        _Cards.add(card);
    }
    public String getTargetFormat() { return _targetFormat; }
    public void setTargetFormat(String value) { _targetFormat = value; }
    public String getNotes() {
        return _notes;
    }
    public void setNotes(String value) {
        _notes = value;
    }
}
