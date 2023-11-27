package com.petroandrushchak.model.third.party.sites;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.petroandrushchak.model.fut.Position;
import com.petroandrushchak.model.fut.Quality;
import com.petroandrushchak.model.fut.Rarity;
import lombok.Data;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Data
public class ThirdPartySitePlayer {

    Long thirdPartySiteId;
    Long futId;

    String playerName;
    Integer rating;

    Long clubId;
    Long nationId;
    Long leagueId;

    Quality quality;
    Rarity rarity;

    List<Position> mainPositions;
    List<Position> otherPositions;

    long price;

    @JsonIgnore
    public boolean isPlayerHasPosition(Position position) {
        return getAllPositions().contains(position);
    }

    @JsonIgnore
    public List<Position> getAllPositions() {
        return Stream.of(mainPositions, otherPositions).flatMap(List::stream).distinct().toList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ThirdPartySitePlayer that = (ThirdPartySitePlayer) o;
        return Objects.equals(futId, that.futId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(futId);
    }
}
