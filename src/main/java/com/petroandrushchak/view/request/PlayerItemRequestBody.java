package com.petroandrushchak.view.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PlayerItemRequestBody {

    String playerName;
    long playerId;
    int playerRating;

    String quality;
    String rarity;
    String position;
    String chemistryStyle;


    // Mapping From MongoDB
    String nation;
    String league;
    String club;

    public boolean isPlayerNamePresent() {
        return playerName != null && !playerName.isEmpty();
    }

    public boolean isPlayerRatingPresent() {
        return playerRating != 0;
    }

    public boolean isPlayerIdPresent() {
        return playerId != 0;
    }

    public boolean isQualityPresent() {
        return quality != null && !quality.isEmpty();
    }


    public boolean isRarityPresent() {
        return rarity != null && !rarity.isEmpty();
    }

    public boolean isPositionPresent() {
        return position != null && !position.isEmpty();
    }

    public boolean isChemistryStylePresent() {
        return chemistryStyle != null && !chemistryStyle.isEmpty();
    }

    public boolean isNationPresent() {
        return nation != null && !nation.isEmpty();
    }

    public boolean isLeaguePresent() {
        return league != null && !league.isEmpty();
    }

    public boolean isClubPresent() {
        return club != null && !club.isEmpty();
    }

}
