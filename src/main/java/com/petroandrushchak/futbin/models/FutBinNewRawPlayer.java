package com.petroandrushchak.futbin.models;

import lombok.Data;

import java.util.List;

@Data
public class FutBinNewRawPlayer {

    String id;
    String name;

    String rating;

    List<String> qualityAndRarity;

    String clubId;
    String clubName;

    String nationId;
    String nationName;

    String leagueId;
    String leagueName;

    String mainPosition;
    List<String> otherPositions;

    String priceText;

}
