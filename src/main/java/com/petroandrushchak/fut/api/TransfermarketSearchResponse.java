package com.petroandrushchak.fut.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransfermarketSearchResponse {

    @JsonProperty("auctionInfo")
    private List<AuctionInfoItem> auctionInfo;

    @JsonProperty("bidTokens")
    private BidTokens bidTokens;
}