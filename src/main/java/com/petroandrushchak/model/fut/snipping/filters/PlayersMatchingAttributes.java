package com.petroandrushchak.model.fut.snipping.filters;

import com.petroandrushchak.futbin.models.FutBinPlayer;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PlayersMatchingAttributes {
    List<Attribute> attributes;
    List<FutBinPlayer> players;
}