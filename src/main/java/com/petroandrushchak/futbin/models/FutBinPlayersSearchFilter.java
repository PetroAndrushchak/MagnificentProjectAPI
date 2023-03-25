package com.petroandrushchak.futbin.models;

import lombok.Data;

@Data
public class FutBinPlayersSearchFilter {

    Version version;
    MinMaxPrice minMaxPrice;

    public boolean isVersionPresent() {
        return version != null;
    }

    public boolean isMinMaxPricePresent() {
        return minMaxPrice != null;
    }

}
