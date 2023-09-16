package com.petroandrushchak.fut.model.statistic;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PUBLIC)
@JsonPropertyOrder({"league_name", "player_name", "player_rating", "player_id", "possible_sell_price", "player_appearance_rating"})
public class PlayerCsvStatisticItem {

    @JsonProperty("league_name")
    String leagueName;

    @JsonProperty("player_name")
    String playerName;

    @JsonProperty("player_id")
    Long playerId;

    @JsonProperty("player_rating")
    Integer playerRating;

    @JsonProperty("possible_sell_price")
    Long possibleSellPrice;

    @JsonProperty("player_appearance_rating")
    Integer playerAppearanceRating;


}
