package com.petroandrushchak.model.fut;

import com.petroandrushchak.exceptions.ItemMappingException;

public enum Quality {

    BRONZE("bronze"),
    SILVER("silver"),
    GOLD("gold"),
    SPECIAL("special");

    private final String apiKey;

    Quality(String apiKey) {
        this.apiKey = apiKey;
    }

    public static Quality fromApiKey(String value) {
        for (Quality enumValue : values()) {
            if (enumValue.apiKey.equalsIgnoreCase(value)) {
                return enumValue;
            }
        }
        throw new ItemMappingException("Quality", "Quality is not found for api key: " + value);
    }

    public String getValue() {
        return apiKey;
    }

}
