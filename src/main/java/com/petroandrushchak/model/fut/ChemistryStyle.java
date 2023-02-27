package com.petroandrushchak.model.fut;

import com.petroandrushchak.exceptions.ItemMappingException;

public enum ChemistryStyle {
    BASIC("basic"),
    SNIPER("sniper"),
    FINISHER("finisher"),
    DEADEYE("deadeye"),
    MARKSMAN("marksman"),
    HAWK("hawk"),
    ARTIST("artist"),
    ARCHITECT("architect"),
    POWERHOUSE("powerhouse"),
    MAESTRO("maestro"),
    ENGINE("engine"),
    SENTINEL("sentinel"),
    GUARDIAN("guardian"),
    GLADIATOR("gladiator"),
    BACKBONE("backbone"),
    ANCHOR("anchor"),
    HUNTER("hunter"),
    CATALYST("catalyst"),
    SHADOW("shadow"),
    WALL("wall"),
    SHIELD("shield"),
    CAT("cat"),
    GLOVE("glove"),
    GK_BASIC("gk_basic");

    private final String apiKey;

    ChemistryStyle(String apiKey) {
        this.apiKey = apiKey;
    }

    public static ChemistryStyle fromApiKey(String value) {
        for (ChemistryStyle enumValue : values()) {
            if (enumValue.apiKey.equalsIgnoreCase(value)) {
                return enumValue;
            }
        }
        throw new ItemMappingException("ChemistryStyle", "ChemistryStyle is not found for api key: " + value);
    }
}
