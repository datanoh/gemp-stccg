package com.gempukku.lotro.db;

import com.gempukku.lotro.db.vo.League;

import java.sql.SQLException;
import java.util.List;

public interface LeagueDAO {
    void addLeague(int cost, String name, String type, String clazz, String parameters, int start, int endTime) throws SQLException;

    List<League> loadActiveLeagues(int currentTime) throws SQLException;

    void setStatus(League league, int newStatus);
}
