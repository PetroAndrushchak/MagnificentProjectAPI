package com.petroandrushchak.fut.model.filters;

import lombok.Data;

@Data
public class LeagueAttribute implements Attribute {

    Long leagueId;
    String leagueName;

    @Override
    public String getStringRepresentation() {
        return " League -> " + leagueName + "-" + leagueId;
    }

    @Override
    public String getShortStringRepresentation() {
        return leagueName;
    }
}
