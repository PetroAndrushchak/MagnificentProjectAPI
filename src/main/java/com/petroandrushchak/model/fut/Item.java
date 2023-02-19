package com.petroandrushchak.model.fut;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Data
public abstract class Item {

    protected ItemLevel level;

    public boolean isLevelSet() {
        return level != null;
    }
}
