package com.petroandrushchak.entity.local.db;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FuInternalPlayers {

    @JsonProperty("LegendsPlayers")
    private List<FutInternalPlayer> legendsPlayers;

    @JsonProperty("Players")
    private List<FutInternalPlayer> players;
}