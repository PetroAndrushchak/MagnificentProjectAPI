package com.petroandrushchak.futbin.models;

import com.petroandrushchak.model.fut.Position;
import com.petroandrushchak.model.fut.Quality;
import com.petroandrushchak.model.fut.Rarity;
import lombok.Data;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class FutBinPlayersAttributes {

    Set<Long> clubIds;
    Set<Long> leagueIds;
    Set<Long> nationIds;

    Set<Position> positions;

    Set<Quality> qualities;
    Set<Rarity> rarities;

    public FutBinPlayersAttributes() {

        this.clubIds = new HashSet<>();
        this.leagueIds = new HashSet<>();
        this.nationIds = new HashSet<>();

        this.positions = new HashSet<>();

        this.qualities = new HashSet<>();
        this.rarities = new HashSet<>();
    }

    public FutBinPlayersAttributes addClubId(Long clubId) {
        this.clubIds.add(clubId);
        return this;
    }

    public FutBinPlayersAttributes addLeagueId(Long leagueId) {
        this.leagueIds.add(leagueId);
        return this;
    }

    public FutBinPlayersAttributes addNationId(Long nationId) {
        this.nationIds.add(nationId);
        return this;
    }

    public FutBinPlayersAttributes addPosition(Position position) {
        this.positions.add(position);
        return this;
    }

    public FutBinPlayersAttributes addPositions(List<Position> positions) {
        this.positions.addAll(positions);
        return this;
    }

    public FutBinPlayersAttributes addQuality(Quality quality) {
        this.qualities.add(quality);
        return this;
    }

    public FutBinPlayersAttributes addRarity(Rarity rarity) {
        this.rarities.add(rarity);
        return this;
    }

}
