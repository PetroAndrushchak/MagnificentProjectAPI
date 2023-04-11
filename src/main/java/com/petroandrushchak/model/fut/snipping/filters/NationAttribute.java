package com.petroandrushchak.model.fut.snipping.filters;

import lombok.Data;

@Data
public class NationAttribute implements Attribute {

    Long nationId;
    String nationName;

    @Override
    public String getStringRepresentation() {
        return "Nation -> " + nationName + "-" + nationId;
    }

    @Override
    public String getShortStringRepresentation() {
        return nationName;
    }
}