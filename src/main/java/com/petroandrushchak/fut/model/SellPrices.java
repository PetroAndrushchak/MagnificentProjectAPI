package com.petroandrushchak.fut.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SellPrices {

    private final long startPrice;
    private final long buyNowPrice;

    private SellPrices(){
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "SellPrices { " +
                " startPrice= " + startPrice +
                ", buyNowPrice= " + buyNowPrice +
                " }";
    }
}
