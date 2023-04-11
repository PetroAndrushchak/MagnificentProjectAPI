package com.petroandrushchak;

import com.petroandrushchak.futbin.models.FutBinPlayer;
import com.petroandrushchak.futbin.models.FutBinPlayersAttributes;
import com.petroandrushchak.futbin.models.FutBinRawPlayer;
import com.petroandrushchak.futbin.steps.FutBinSteps;
import com.petroandrushchak.model.fut.FutPlayersAttributes;
import com.petroandrushchak.model.fut.snipping.filters.Attribute;
import com.petroandrushchak.model.fut.snipping.filters.AttributeType;
import com.petroandrushchak.model.fut.snipping.filters.PlayersMatchingAttributes;
import com.petroandrushchak.service.FutBinService;
import com.petroandrushchak.steps.FutBinMappingSteps;
import com.petroandrushchak.steps.FutBinSnippingFiltersSteps;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static com.petroandrushchak.model.fut.snipping.filters.AttributeType.*;
import static com.petroandrushchak.model.fut.snipping.filters.AttributeType.CLUB;

@SpringBootTest
public class FindSnippingFiltersTest {

    @Autowired FutBinSteps futBinSteps;
    @Autowired FutBinService futBinService;
    @Autowired FutBinSnippingFiltersSteps futBinSnippingFiltersSteps;
    @Autowired FutBinMappingSteps futBinMappingSteps;

    @Test
    void findSnippingFiltersBaseOnPlayersResults() {

        List<AttributeType> attributeTypes = List.of(POSITION, NATION, LEAGUE, CLUB);

        List<FutBinRawPlayer> futBinRawPlayers = futBinService.parsePlayersFromFile();
        List<FutBinPlayer> futBinPlayers = futBinMappingSteps.mapRawPlayersToPlayers(futBinRawPlayers);

        FutBinPlayersAttributes futBinPlayersUniqueAttributes = futBinSnippingFiltersSteps.getUniquePlayersAttributes(futBinPlayers);
        FutPlayersAttributes futPlayersAttributes = futBinMappingSteps.mapFutBinPlayersAttributesToPlayersAttributes(futBinPlayersUniqueAttributes);

        List<List<Attribute>> allPossibleCombinationsForAttributes = futBinSnippingFiltersSteps.getAllPossibleCombinationsForAttributes(attributeTypes, futPlayersAttributes);

        var foundPlayersToSnipe = futBinSnippingFiltersSteps.findAllPlayersMatchingAttributes(allPossibleCombinationsForAttributes, futBinPlayers);

        System.out.println("dsfsdf");

    }

}
