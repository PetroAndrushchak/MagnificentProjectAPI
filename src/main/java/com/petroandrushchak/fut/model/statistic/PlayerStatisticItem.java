package com.petroandrushchak.fut.model.statistic;

import com.petroandrushchak.model.fut.PlayerItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class PlayerStatisticItem extends PlayerItem {

    long possibleSellPrice;

    public boolean isPossibleSellPricePresent() {
        return possibleSellPrice != 0;
    }
}
