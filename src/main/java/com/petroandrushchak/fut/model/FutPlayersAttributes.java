package com.petroandrushchak.fut.model;

import com.petroandrushchak.model.fut.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FutPlayersAttributes {

    List<Club> clubs;
    List<League> leagues;
    List<Nation> nations;

    List<Position> positions;

    List<Quality> qualities;
    List<Rarity> rarities;

    public FutPlayersAttributes() {
        this.clubs = new ArrayList<>();
        this.leagues = new ArrayList<>();
        this.nations = new ArrayList<>();
        this.positions = new ArrayList<>();
        this.qualities = new ArrayList<>();
        this.rarities = new ArrayList<>();
    }

    public FutPlayersAttributes addClubs(List<Club> clubs) {
        this.clubs.addAll(clubs);
        return this;
    }

    public FutPlayersAttributes addLeagues(List<League> leagues) {
        this.leagues.addAll(leagues);
        return this;
    }

    public FutPlayersAttributes addNations(List<Nation> nations) {
        this.nations.addAll(nations);
        return this;
    }

    public FutPlayersAttributes addPositions(List<Position> positions) {
        this.positions.addAll(positions);
        return this;
    }

    public FutPlayersAttributes addQualities(List<Quality> qualities) {
        this.qualities.addAll(qualities);
        return this;
    }

    public FutPlayersAttributes addRarities(List<Rarity> rarities) {
        this.rarities.addAll(rarities);
        return this;
    }
}
