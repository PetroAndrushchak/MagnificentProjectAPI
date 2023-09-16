package com.petroandrushchak.helper;

import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonParser {

    @SneakyThrows
    public static <T> T parseFromString(String json, Class<T> cls) {
        JsonMapper jsonMapper = new JsonMapper();
        return jsonMapper.readValue(json, cls);
    }
}
