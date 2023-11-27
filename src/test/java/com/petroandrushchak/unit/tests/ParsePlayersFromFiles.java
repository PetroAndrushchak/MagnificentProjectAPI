package com.petroandrushchak.unit.tests;

import com.petroandrushchak.service.fut.FutClubServiceInternal;
import com.petroandrushchak.service.fut.FutLeagueServiceInternal;
import com.petroandrushchak.service.fut.FutPlayerServiceInternal;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ParsePlayersFromFiles {

    @Autowired FutPlayerServiceInternal futPlayerServiceInternal;
    @Autowired FutClubServiceInternal futClubServiceInternal;
    @Autowired FutLeagueServiceInternal futLeagueServiceInternal;

    @SneakyThrows
    @Test
    void parsePlayersFromFile() {
        Long playerId = 265272L;
        var start = System.currentTimeMillis();
        var player = futPlayerServiceInternal.getPlayerById(playerId);
        var end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start));
        System.out.println(player);
    }

    @SneakyThrows
    @Test
    void parseClubFromFile() {
        Long clubId = 241L;
        var start = System.currentTimeMillis();
        var club = futClubServiceInternal.getClubById(clubId);
        var end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start));
        System.out.println(club);
    }

    @SneakyThrows
    @Test
    void parseLeagueFromFile() {
        Long leagueId = 2179L;
        var start = System.currentTimeMillis();
        var league = futLeagueServiceInternal.getLeagueById(leagueId);
        var end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start));
        System.out.println(league);
    }
}
