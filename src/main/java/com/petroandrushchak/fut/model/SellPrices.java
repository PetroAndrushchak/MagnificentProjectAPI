package com.petroandrushchak.fut.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class SellPrices {

    private long startPrice;
    private long buyNowPrice;

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
