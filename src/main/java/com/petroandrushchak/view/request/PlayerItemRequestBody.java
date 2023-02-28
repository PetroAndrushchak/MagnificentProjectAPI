package com.petroandrushchak.view.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PlayerItemRequestBody {

    String playerName;
    Long playerId;
    Integer playerRating;

    String quality;
    String rarity;
    String position;
    String chemistryStyle;

    // Mapping From MongoDB
    Long nationId;
    String nationAbbreviation;
    String nationName;

    String league;
    String club;

    public boolean isPlayerNamePresent() {
        return playerName != null && !playerName.isEmpty();
    }

    public boolean isPlayerRatingPresent() {
        return playerRating != null && playerRating != 0;
    }

    public boolean isPlayerIdPresent() {
        return playerId != null && playerId != 0;
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

    public boolean isNationIdPresent() {
        return nationId != null && nationId != 0;
    }

    public boolean isNationAbbreviationPresent() {
        return nationAbbreviation != null && !nationAbbreviation.isEmpty();
    }

    public boolean isNationNamePresent() {
        return nationName != null && !nationName.isEmpty();
    }


    public boolean isLeaguePresent() {
        return league != null && !league.isEmpty();
    }

    public boolean isClubPresent() {
        return club != null && !club.isEmpty();
    }

}
