package com.petroandrushchak.fut.model.statistic;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Duration;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class TransferMarketAppearance {

    @ToString.Include
    int quantity;

    Duration duration;

    long lowestBinPrice;
    long monitoringPrice;

    List<Integer> appearedBuyNowPrices;

}
