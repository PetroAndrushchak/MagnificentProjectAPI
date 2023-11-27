package com.petroandrushchak.snipping.filter;

import com.petroandrushchak.futbin.models.FutBinNewRawPlayer;
import com.petroandrushchak.model.third.party.sites.ThirdPartySitePlayer;
import com.petroandrushchak.model.third.party.sites.ThirdPartySitePlayersAttributes;
import com.petroandrushchak.futbin.steps.FutBinSteps;
import com.petroandrushchak.fut.model.FutPlayersAttributes;
import com.petroandrushchak.fut.model.filters.Attribute;
import com.petroandrushchak.fut.model.filters.AttributeType;
import com.petroandrushchak.service.FutBinService;
import com.petroandrushchak.steps.FutBinMappingSteps;
import com.petroandrushchak.steps.FutBinSnippingFiltersSteps;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.petroandrushchak.fut.model.filters.AttributeType.*;
import static com.petroandrushchak.fut.model.filters.AttributeType.CLUB;

@SpringBootTest
public class FindSnippingFiltersTest {

    @Autowired FutBinSteps futBinSteps;
    @Autowired FutBinService futBinService;
    @Autowired FutBinSnippingFiltersSteps futBinSnippingFiltersSteps;
    @Autowired FutBinMappingSteps futBinMappingSteps;

    @Test
    void findSnippingFiltersBaseOnPlayersResults() {

        List<AttributeType> attributeTypes = List.of(POSITION, NATION, CLUB);

        var playersFileName = "english_players_data.json";
     //   var playersFileName = "spanish_players_data.json";
    //    var playersFileName = "french_players_data.json";
     //   var playersFileName = "german_players_data.json";
    //   var playersFileName = "italian_players_data.json";

     //    var playersFileName = "w_english_players_data.json";


        List<FutBinNewRawPlayer> futBinRawPlayers = futBinService.parsePlayersFromJsonFile(playersFileName);
        List<ThirdPartySitePlayer> thirdPartySitePlayers = futBinMappingSteps.mapNewRawPlayersToPlayers(futBinRawPlayers);

        ThirdPartySitePlayersAttributes futBinPlayersUniqueAttributes = futBinSnippingFiltersSteps.getUniquePlayersAttributes(thirdPartySitePlayers);

        FutPlayersAttributes futPlayersAttributes = futBinMappingSteps.mapFutBinPlayersAttributesToPlayersAttributes(futBinPlayersUniqueAttributes);

        List<List<Attribute>> allPossibleCombinationsForAttributes = futBinSnippingFiltersSteps.getAllPossibleCombinationsForAttributes(attributeTypes, futPlayersAttributes);

        var foundPlayersToSnipe = futBinSnippingFiltersSteps.findAllPlayersMatchingAttributes(allPossibleCombinationsForAttributes, thirdPartySitePlayers);

        System.out.println("dsfsdf");

    }

}
