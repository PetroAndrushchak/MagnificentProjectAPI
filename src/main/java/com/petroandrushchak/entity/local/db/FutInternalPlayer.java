package com.petroandrushchak.entity.local.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FutInternalPlayer {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("r")
    private int rating;

    @JsonProperty("f")
    private String firstName;

    @JsonProperty("l")
    private String lastName;

    @JsonProperty("c")
    private String nickName;

    public boolean isNickNamePresent() {
        return nickName != null && !nickName.isEmpty();
    }

}
