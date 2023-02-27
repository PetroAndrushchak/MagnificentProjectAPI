package com.petroandrushchak.model.fut;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class PlayerItem extends Item {

    Quality level;

    String name;
    int rating;
    long id;

    Rarity rarity;
    Position position;
    ChemistryStyle chemistryStyle;

    String nationality;
    String league;
    String club;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\n{\n");

        if (level != null) {
            sb.append("  \"level\": \"").append(level).append("\",\n");
        }

        if (name != null) {
            sb.append("  \"name\": \"").append(name).append("\",\n");
        }

        if (rating != 0) {
            sb.append("  \"rating\": ").append(rating).append(",\n");
        }

        if (id != 0L) {
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

        if (nationality != null) {
            sb.append("  \"nationality\": \"").append(nationality).append("\",\n");
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
