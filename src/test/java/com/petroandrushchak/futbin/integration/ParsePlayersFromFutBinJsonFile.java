package com.petroandrushchak.futbin.integration;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.petroandrushchak.fut.model.statistic.PlayerCsvStatisticItem;
import com.petroandrushchak.futbin.models.FutBinNewRawPlayer;
import com.petroandrushchak.futbin.models.FutBinPlayer;
import com.petroandrushchak.service.FutAppearanceStatisticService;
import com.petroandrushchak.steps.FutBinMappingSteps;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class ParsePlayersFromFutBinJsonFile {

    @Autowired FutBinMappingSteps futBinMappingSteps;
    @Autowired FutAppearanceStatisticService futAppearanceStatisticService;

    @SneakyThrows
    @Test
    void parsePlayersFromFutBinJsonFile() {

        JsonMapper jsonMapper = new JsonMapper();
        TypeFactory typeFactory = jsonMapper.getTypeFactory();

        List<FutBinNewRawPlayer> parsedPlayers = jsonMapper.readValue(Paths.get("/Users/pandrushchak.appwell/Workspace/MagnificentProjectAPI/database/futbin_players/spanish_players_data.json")
                                                                           .toFile(), typeFactory.constructCollectionType(List.class, FutBinNewRawPlayer.class));

        List<FutBinPlayer> futBinPlayers = futBinMappingSteps.mapNewRawPlayersToPlayers(parsedPlayers);
        //Filter players with price more that 1000 coins
        futBinPlayers.removeIf(futBinPlayer -> futBinPlayer.getPrice() < 1000);

        List<PlayerCsvStatisticItem> playerAppearanceStatisticItems = futBinMappingSteps.mapFutBinPlayersToCsvStatisticItems(futBinPlayers);
        futAppearanceStatisticService.updateAppearanceStatistic(playerAppearanceStatisticItems);




        System.out.println("dsfdsf");
        //Store it to the CSV file


        //Get last 10 players from CSV file (without appearance analysis) and start appearance statistics analysis

        //Manually update appearance rating for each player

        //Repeat previous step until all players will have appearance rating

        System.out.println("sdfsdf");


    }
}
