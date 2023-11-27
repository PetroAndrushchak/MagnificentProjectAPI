package com.petroandrushchak.fut.api;

import java.util.Comparator;
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

    public boolean isAnyItemsFound() {
        return !(auctionInfo == null || auctionInfo.isEmpty());
    }

    public AuctionInfoItem getFirstItem() {
        return auctionInfo.get(0);
    }

    public List<AuctionInfoItem> getAuctionsInfo() {
        return auctionInfo;
    }

    public AuctionInfoItem getTheMostCheapestItem() {
        return auctionInfo.stream()
                          .min(Comparator.comparing(AuctionInfoItem::getBuyNowPrice))
                          .get();
    }

    public boolean isNoItemsFound() {
        return (auctionInfo == null || auctionInfo.isEmpty());
    }

    public AuctionInfoItem getAuctionInfoItemByTradeId(Long tradeId) {
        return auctionInfo.stream()
                          .filter(auctionInfoItem -> auctionInfoItem.getTradeId().equals(tradeId))
                          .findFirst()
                          .orElseThrow(() -> new RuntimeException("No auction info item found for tradeId: " + tradeId));
    }

}