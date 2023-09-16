package com.petroandrushchak.fut.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class AuctionInfoItem {

    @JsonProperty("offers")
    private Integer offers;

    @JsonProperty("expires")
    private Integer expires;

    @JsonProperty("tradeOwner")
    private Boolean tradeOwner;

    @JsonProperty("confidenceValue")
    private Integer confidenceValue;

    @JsonProperty("startingBid")
    private Integer startingBid;

    @JsonProperty("sellerName")
    private String sellerName;

    @JsonProperty("buyNowPrice")
    private Integer buyNowPrice;

    @JsonProperty("tradeState")
    private String tradeState;

    @JsonProperty("watched")
    private Boolean watched;

    @JsonProperty("currentBid")
    private Integer currentBid;

    @JsonProperty("sellerId")
    private Integer sellerId;

    @JsonProperty("itemData")
    private ItemData itemData;

    @JsonProperty("sellerEstablished")
    private Integer sellerEstablished;

    @JsonProperty("bidState")
    private String bidState;

    @JsonProperty("tradeIdStr")
    private String tradeIdStr;

    @JsonProperty("tradeId")
    private Long tradeId;
}