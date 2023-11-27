package com.petroandrushchak.futwiz;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.petroandrushchak.futwiz.steps.FutWizParsePlayersSteps;
import com.petroandrushchak.model.third.party.sites.ThirdPartySitePlayer;
import com.petroandrushchak.registry.FutWizDataRegistry;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ParseAllPlayersFromFutWiz {

    @Autowired FutWizParsePlayersSteps futWizParsePlayersSteps;

    @SneakyThrows
    @Test
    void parseAllPlayersFromFutWiz() {

        var filteredPage = "https://www.futwiz.com/en/fc24/players?page=0";

       var players = futWizParsePlayersSteps.parsePlayersFromFutWiz(filteredPage);

        //Store FutWiz players to json file
        JsonMapper jsonMapper = new JsonMapper();
    //    jsonMapper.writeValue(FutWizDataRegistry.allPlayersFilePath().toFile(), players);

        List<ThirdPartySitePlayer> parsedPlayers = jsonMapper.readValue(FutWizDataRegistry.allPlayersFilePath().toFile(),  new TypeReference<List<ThirdPartySitePlayer>>() { });
        System.out.println("dsfsdf");
    }
}
