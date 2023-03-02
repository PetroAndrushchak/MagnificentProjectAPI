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

    Long leagueId;
    String leagueFullName;
    String leagueShortAbbreviation;

    Long clubId;
    String clubShortAbbreviation;
    String clubMediumAbbreviation;

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

    public boolean isLeagueIdPresent() {
        return leagueId != null && leagueId != 0;
    }

    public boolean isLeagueFullNamePresent() {
        return leagueFullName != null && !leagueFullName.isEmpty();
    }

    public boolean isLeagueShortAbbreviationPresent() {
        return leagueShortAbbreviation != null && !leagueShortAbbreviation.isEmpty();
    }

    public boolean isClubIdPresent() {
        return clubId != null && clubId != 0;
    }

    public boolean isClubShortAbbreviationPresent() {
        return clubShortAbbreviation != null && !clubShortAbbreviation.isEmpty();
    }

    public boolean isClubMediumAbbreviationPresent() {
        return clubMediumAbbreviation != null && !clubMediumAbbreviation.isEmpty();
    }


}
