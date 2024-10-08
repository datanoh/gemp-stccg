package com.gempukku.stccg.league;

public enum SealedLeagueType {
    FOTR_BLOCK("fotr_block", "limited_fotr"),
    TTT_BLOCK("ttt_block", "limited_ttt"),
    MOVIE_BLOCK("movie", "limited_king"),
    WAR_BLOCK("war_block", "limited_shadows"),
    HUNTERS_BLOCK("hunters_block", "limited_hunters"),
    MOVIE_SPECIAL_BLOCK("movie_special", "limited_king"),
    TS_SPECIAL_BLOCK("ts_special", "limited_ttt");

    public static SealedLeagueType getLeagueType(String sealedCode) {
        for (SealedLeagueType sealedLeagueType : SealedLeagueType.values()) {
            if (sealedLeagueType.getSealedCode().equals(sealedCode))
                return sealedLeagueType;
        }
        return null;
    }

    private final String _sealedCode;
    private final String _format;

    SealedLeagueType(String sealedCode, String format) {
        _sealedCode = sealedCode;
        _format = format;
    }

    public String getSealedCode() {
        return _sealedCode;
    }

    public String getFormat() {
        return _format;
    }
}
