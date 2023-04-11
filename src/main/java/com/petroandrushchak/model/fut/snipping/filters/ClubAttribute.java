package com.petroandrushchak.model.fut.snipping.filters;

import lombok.Data;

@Data
public class ClubAttribute implements Attribute {

    Long clubId;
    String clubName;

    @Override
    public String getStringRepresentation() {
        return " Club -> " + clubName + "-" + clubId;
    }

    @Override
    public String getShortStringRepresentation() {
        return clubName;
    }
}
