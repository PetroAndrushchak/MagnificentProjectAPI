package com.petroandrushchak.fut.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TransferMarketPrices {

    private long minBidPrice = 0L;
    private long maxBidPrice = 0L;
    private long minBuyNowPrice = 0L;
    private long maxBuyNowPrice = 0L;

    private TransferMarketPrices() {
    }

    public static TransferMarketPrices emptyPrices() {
        return new TransferMarketPrices();
    }

    public boolean isMinBidPricePresent() {
        return minBidPrice != 0;
    }

    public boolean isMaxBidPricePresent() {
        return maxBidPrice != 0;
    }

    public boolean isMinBuyNowPricePresent() {
        return minBuyNowPrice != 0;
    }

    public boolean isMaxBuyNowPricePresent() {
        return maxBuyNowPrice != 0;
    }

    @Override
    public String toString() {
        return "TransferMarketPrices { " +
                " minBidPrice=" + minBidPrice +
                ", maxBidPrice= " + maxBidPrice +
                ", minBuyNowPrice= " + minBuyNowPrice +
                ", maxBuyNowPrice= " + maxBuyNowPrice +
                " }";
    }
}

