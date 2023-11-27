package com.petroandrushchak.fut.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ItemData {

    @JsonProperty("itemType")
    private String itemType;

    @JsonProperty("resourceId")
    private Integer resourceId;

    @JsonProperty("rareflag")
    private Integer rareflag;

    @JsonProperty("nation")
    private Integer nation;

    @JsonProperty("skillmoves")
    private Integer skillmoves;

    @JsonProperty("pile")
    private Integer pile;

    @JsonProperty("injuryType")
    private String injuryType;

    @JsonProperty("weakfootabilitytypecode")
    private Integer weakfootabilitytypecode;

    @JsonProperty("rating")
    private Integer rating;

    @JsonProperty("itemState")
    private String itemState;

    @JsonProperty("owners")
    private Integer owners;

    @JsonProperty("formation")
    private String formation;

    @JsonProperty("possiblePositions")
    private List<String> possiblePositions;

    @JsonProperty("baseTraits")
    List<Long> baseTraits;

    @JsonProperty("iconTraits")
    List<Long> iconTraits;

    @JsonProperty("gender")
    private Long gender;

    @JsonProperty("statsArray")
    private List<Integer> statsArray;

    @JsonProperty("defensiveworkrate")
    private Integer defensiveworkrate;

    @JsonProperty("assists")
    private Integer assists;

    @JsonProperty("assetId")
    private Integer assetId;

    @JsonProperty("leagueId")
    private Integer leagueId;

    @JsonProperty("teamid")
    private Integer teamid;

    @JsonProperty("attackingworkrate")
    private Integer attackingworkrate;

    @JsonProperty("cardsubtypeid")
    private Integer cardsubtypeid;

    @JsonProperty("id")
    private Long id;

    @JsonProperty("loyaltyBonus")
    private Integer loyaltyBonus;

    @JsonProperty("lifetimeStatsArray")
    private List<Integer> lifetimeStatsArray;

    @JsonProperty("timestamp")
    private Integer timestamp;

    @JsonProperty("contract")
    private Integer contract;

    @JsonProperty("lifetimeAssists")
    private Integer lifetimeAssists;

    @JsonProperty("groups")
    private List<Integer> groups;

    @JsonProperty("attributeArray")
    private List<Integer> attributeArray;

    @JsonProperty("preferredfoot")
    private Integer preferredfoot;

    @JsonProperty("guidAssetId")
    private String guidAssetId;

    @JsonProperty("preferredPosition")
    private String preferredPosition;

    @JsonProperty("resourceGameYear")
    private Integer resourceGameYear;

    @JsonProperty("playStyle")
    private Integer playStyle;

    @JsonProperty("injuryGames")
    private Integer injuryGames;

    @JsonProperty("discardValue")
    private Integer discardValue;

    @JsonProperty("untradeable")
    private Boolean untradeable;

    @JsonProperty("lastSalePrice")
    private Integer lastSalePrice;
}