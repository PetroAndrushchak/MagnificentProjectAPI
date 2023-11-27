package com.petroandrushchak.model.third.party.sites;

import com.petroandrushchak.model.fut.Position;
import com.petroandrushchak.model.fut.Quality;
import com.petroandrushchak.model.fut.Rarity;
import lombok.Data;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class ThirdPartySitePlayersAttributes {

    Set<Long> clubIds;
    Set<Long> leagueIds;
    Set<Long> nationIds;

    Set<Position> positions;

    Set<Quality> qualities;
    Set<Rarity> rarities;

    public ThirdPartySitePlayersAttributes() {

        this.clubIds = new HashSet<>();
        this.leagueIds = new HashSet<>();
        this.nationIds = new HashSet<>();

        this.positions = new HashSet<>();

        this.qualities = new HashSet<>();
        this.rarities = new HashSet<>();
    }

    public ThirdPartySitePlayersAttributes addClubId(Long clubId) {
        this.clubIds.add(clubId);
        return this;
    }

    public ThirdPartySitePlayersAttributes addLeagueId(Long leagueId) {
        this.leagueIds.add(leagueId);
        return this;
    }

    public ThirdPartySitePlayersAttributes addNationId(Long nationId) {
        this.nationIds.add(nationId);
        return this;
    }

    public ThirdPartySitePlayersAttributes addPosition(Position position) {
        this.positions.add(position);
        return this;
    }

    public ThirdPartySitePlayersAttributes addPositions(List<Position> positions) {
        this.positions.addAll(positions);
        return this;
    }

    public ThirdPartySitePlayersAttributes addQuality(Quality quality) {
        this.qualities.add(quality);
        return this;
    }

    public ThirdPartySitePlayersAttributes addRarity(Rarity rarity) {
        this.rarities.add(rarity);
        return this;
    }

}
