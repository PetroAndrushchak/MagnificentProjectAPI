package com.petroandrushchak.futbin.models;

import lombok.Data;

import java.util.List;

@Deprecated //I use python project to parse players from Futbin and send json file. This class is not used anymore
@Data
public class FutBinRawPlayer {

    String id;
    String name;

    String rating;

    String qualityAndRarity;

    String clubId;
    String clubName;

    String nationId;
    String nationName;

    String leagueId;
    String leagueName;

    List<String> positions;

    String priceText;

}
