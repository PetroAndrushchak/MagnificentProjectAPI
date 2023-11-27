package com.petroandrushchak.futwiz.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FutWizRawPlayer {

    String internalId;

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

    public boolean isFUTIdPresent() {
        return futId != null;
    }

}
