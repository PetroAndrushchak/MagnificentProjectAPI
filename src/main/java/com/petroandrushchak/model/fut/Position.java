package com.petroandrushchak.model.fut;

import com.petroandrushchak.exceptions.ItemMappingException;

public enum Position {

    DEFENDERS("defenders"),
    MIDFIELDERS("midfielders"),
    ATTACKERS("attackers"),

    GK("gk"),
    RWB("rwb"),
    RB("rb"),
    CB("cb"),
    LB("lb"),
    LWB("lwb"),
    CDM("cdm"),
    RM("rm"),
    CM("cm"),
    LM("lm"),
    CAM("cam"),
    CF("cf"),
    RW("rw"),
    ST("st"),
    LW("lw");

    public boolean isDefender() {
        return this == RWB || this == RB || this == CB || this == LB || this == LWB;
    }

    public boolean isMidfielder() {
        return this == CDM || this == RM || this == CM || this == LM || this == CAM;
    }

    public boolean isAttacker() {
        return this == CF || this == RW || this == ST || this == LW;
    }

    private final String apiKey;

    Position(String value) {
        this.apiKey = value;
    }

    public static Position fromKey(String value) {
        for (Position enumValue : values()) {
            if (enumValue.apiKey.equalsIgnoreCase(value)) {
                return enumValue;
            }
        }
        throw new ItemMappingException("Position", "Position is not found for api key: " + value);
    }

    public String getApiKey() {
        return apiKey;
    }



}
