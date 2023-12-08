package com.petroandrushchak.unit.tests;

import com.petroandrushchak.service.fut.FutClubService;
import com.petroandrushchak.service.fut.FutLeagueService;
import com.petroandrushchak.service.fut.FutNationService;
import com.petroandrushchak.service.fut.FutPlayerService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ParsePlayersFromFiles {

    @Autowired FutPlayerService futPlayerService;
    @Autowired FutClubService futClubService;
    @Autowired FutLeagueService futLeagueService;
    @Autowired FutNationService futNationService;

    @SneakyThrows
    @Test
    void parsePlayersFromFile() {
        Long playerId = 265272L;
        var start = System.currentTimeMillis();
        var player = futPlayerService.getPlayerById(playerId);
        var end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start));
        System.out.println(player);
    }

    @SneakyThrows
    @Test
    void parseClubFromFile() {
        Long clubId = 241L;
        var start = System.currentTimeMillis();
        var club = futClubService.getClubById(clubId);
        var end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start));
        System.out.println(club);
    }

    @SneakyThrows
    @Test
    void parseLeagueFromFile() {
        Long leagueId = 2179L;
        var start = System.currentTimeMillis();
        var league = futLeagueService.getLeagueById(leagueId);
        var end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start));
        System.out.println(league);
    }

    @SneakyThrows
    @Test
    void parseNationsFromFile() {
        Long nationId = 14L;
        var start = System.currentTimeMillis();
        var nation = futNationService.getNationsByIds(List.of(nationId));
        var end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start));
        System.out.println(nation);
    }
}
