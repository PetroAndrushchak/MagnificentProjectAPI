package com.petroandrushchak.model.fut;

import lombok.Data;

@Data
public abstract class Item {

    protected Quality quality;

    public boolean isQualityPresent() {
        return quality != null;
    }

}
