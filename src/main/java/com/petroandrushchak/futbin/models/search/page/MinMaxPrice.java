package com.petroandrushchak.futbin.models.search.page;

import lombok.Data;

@Data
public class MinMaxPrice {

    long minPrice = 200;
    long maxPrice = 15_000_000;

    public static MinMaxPrice of(long minPrice, long maxPrice) {
        MinMaxPrice minMaxPrice = new MinMaxPrice();
        minMaxPrice.setMinPrice(minPrice);
        minMaxPrice.setMaxPrice(maxPrice);
        return minMaxPrice;
    }

}
