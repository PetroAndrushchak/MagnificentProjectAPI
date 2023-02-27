package com.petroandrushchak.model.fut;

import lombok.Data;

@Data
public abstract class Item {

    protected Quality level;

    public boolean isLevelSet() {
        return level != null;
    }
}
