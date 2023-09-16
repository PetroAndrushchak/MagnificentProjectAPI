package com.petroandrushchak.futbin.models;

import com.petroandrushchak.model.fut.Position;
import com.petroandrushchak.model.fut.Quality;
import com.petroandrushchak.model.fut.Rarity;
import lombok.Data;

import java.util.List;
import java.util.stream.Stream;

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

    List<Position> mainPositions;
    List<Position> otherPositions;

    public boolean isPlayerHasPosition(Position position) {
        return getAllPositions().contains(position);
    }

    public List<Position> getAllPositions() {
        return Stream.of(mainPositions, otherPositions).flatMap(List::stream).distinct().toList();
    }

    long price;

}
