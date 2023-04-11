package com.petroandrushchak.futbin.models;

import com.petroandrushchak.model.fut.Position;
import com.petroandrushchak.model.fut.Quality;
import com.petroandrushchak.model.fut.Rarity;
import lombok.Data;

import java.util.List;

@Data
public class FutBinPlayer {

    Long id;
    String playerName;
    Integer rating;

    Long clubId;
    Long nationId;
    Long leagueId;

    Quality quality;
    Rarity rarity;

    List<Position> positions;

    public boolean isPlayerHasPosition(Position position) {
        return positions.contains(position);
    }

    long price;

}
