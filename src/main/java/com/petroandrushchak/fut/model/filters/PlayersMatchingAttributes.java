package com.petroandrushchak.fut.model.filters;

import com.petroandrushchak.model.third.party.sites.ThirdPartySitePlayer;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PlayersMatchingAttributes {
    List<Attribute> attributes;
    List<ThirdPartySitePlayer> players;
}