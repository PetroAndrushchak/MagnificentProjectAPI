package com.petroandrushchak.model.fut;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class PlayerItem extends Item {

    String playerName;

    //This should be value from FUT Web App data
    String playerFirstName;
    String playerLastName;
    String nickName;

    Integer rating;
    Long id;

    Rarity rarity;
    Position position;
    ChemistryStyle chemistryStyle;

    Nation nation;
    League league;
    Club club;

    public boolean isPlayerNamePresent() {
        return playerName != null;
    }

    public boolean isPlayerNickNamePresent() {
        return nickName != null && !nickName.isEmpty();
    }

    public boolean isRatingPresent() {
        return rating != null;
    }

    public boolean isRarityPresent() {
        return rarity != null;
    }

    public boolean isPositionPresent() {
        return position != null;
    }

    public boolean isChemistryStylePresent() {
        return chemistryStyle != null;
    }

    public boolean isNationPresent() {
        return nation != null;
    }

    public boolean isLeaguePresent() {
        return league != null;
    }

    public boolean isClubPresent() {
        return club != null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\n{\n");

        if (quality != null) {
            sb.append("  \"level\": \"").append(quality).append("\",\n");
        }

        if (playerName != null) {
            sb.append("  \"playerName\": \"").append(playerName).append("\",\n");
        }

        if (rating != null) {
            sb.append("  \"rating\": ").append(rating).append(",\n");
        }

        if (id != null) {
            sb.append("  \"id\": ").append(id).append(",\n");
        }

        if (rarity != null) {
            sb.append("  \"rarity\": \"").append(rarity).append("\",\n");
        }

        if (position != null) {
            sb.append("  \"position\": \"").append(position).append("\",\n");
        }

        if (chemistryStyle != null) {
            sb.append("  \"chemistryStyle\": \"").append(chemistryStyle).append("\",\n");
        }

        if (nation != null) {
            sb.append("  \"nationality\": \"").append(nation).append("\",\n");
        }

        if (league != null) {
            sb.append("  \"league\": \"").append(league).append("\",\n");
        }

        if (club != null) {
            sb.append("  \"club\": \"").append(club).append("\"\n");
        }

        sb.append("}");

        return sb.toString();
    }
}
