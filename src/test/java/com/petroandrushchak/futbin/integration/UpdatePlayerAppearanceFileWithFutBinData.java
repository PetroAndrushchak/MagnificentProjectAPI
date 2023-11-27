package com.petroandrushchak.futbin.integration;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.petroandrushchak.fut.model.statistic.PlayerCsvStatisticItem;
import com.petroandrushchak.futbin.models.FutBinNewRawPlayer;
import com.petroandrushchak.model.third.party.sites.ThirdPartySitePlayer;
import com.petroandrushchak.service.FutAppearanceStatisticService;
import com.petroandrushchak.steps.FutBinMappingSteps;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Paths;
import java.util.List;

@SpringBootTest
public class UpdatePlayerAppearanceFileWithFutBinData {

    @Autowired FutBinMappingSteps futBinMappingSteps;
    @Autowired FutAppearanceStatisticService futAppearanceStatisticService;

    @SneakyThrows
    @Test
    void updatePlayersAppearanceFileWithFutBinData() {

        JsonMapper jsonMapper = new JsonMapper();
        TypeFactory typeFactory = jsonMapper.getTypeFactory();

        List<FutBinNewRawPlayer> parsedPlayers = jsonMapper.readValue(Paths.get("/Users/pandrushchak.appwell/Workspace/MagnificentProjectAPI/database/futbin_players/english_players_data.json")
                                                                           .toFile(), typeFactory.constructCollectionType(List.class, FutBinNewRawPlayer.class));

        List<ThirdPartySitePlayer> thirdPartySitePlayers = futBinMappingSteps.mapNewRawPlayersToPlayers(parsedPlayers);
        //Filter players with price more that 1000 coins
        thirdPartySitePlayers.removeIf(futBinPlayer -> futBinPlayer.getPrice() <= 1000);

        //Store it to the CSV file
        List<PlayerCsvStatisticItem> playerAppearanceStatisticItems = futBinMappingSteps.mapFutBinPlayersToCsvStatisticItems(thirdPartySitePlayers);
        futAppearanceStatisticService.updateAppearanceStatistic(playerAppearanceStatisticItems);

        System.out.println("dsfdsf");
    }
}
