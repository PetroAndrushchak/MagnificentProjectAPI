package com.petroandrushchak.futbin.models;

import lombok.Data;

import java.util.List;

@Data
public class FutBinPlayer {

    String id;
    String name;

    String clubId;
    String clubName;

    String nationId;
    String nationName;

    String leagueId;
    String leagueName;

    String qualityAndRarity;

    String rating;

    List<String> positions;

    String priceText;


}
