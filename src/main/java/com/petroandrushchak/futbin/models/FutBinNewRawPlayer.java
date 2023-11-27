package com.petroandrushchak.futbin.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class FutBinNewRawPlayer {

    @JsonProperty("fut_bin_id")
    String internalId;

    @JsonProperty("fut_id")
    String futId;

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
