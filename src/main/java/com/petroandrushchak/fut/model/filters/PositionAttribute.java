package com.petroandrushchak.fut.model.filters;

import com.petroandrushchak.model.fut.Position;
import lombok.Data;

@Data
public class PositionAttribute implements Attribute {
    Position position;

    @Override
    public String getStringRepresentation() {
        return " Position -> " + position.name();
    }

    @Override
    public String getShortStringRepresentation() {
        return position.toString();
    }
}