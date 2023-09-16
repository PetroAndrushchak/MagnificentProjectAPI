package com.petroandrushchak.snipping.filter;

import com.petroandrushchak.futbin.models.FutBinNewRawPlayer;
import com.petroandrushchak.futbin.models.FutBinPlayer;
import com.petroandrushchak.futbin.models.FutBinPlayersAttributes;
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

        List<FutBinNewRawPlayer> futBinRawPlayers = futBinService.parsePlayersFromJsonFile();
        List<FutBinPlayer> futBinPlayers = futBinMappingSteps.mapNewRawPlayersToPlayers(futBinRawPlayers);

        FutBinPlayersAttributes futBinPlayersUniqueAttributes = futBinSnippingFiltersSteps.getUniquePlayersAttributes(futBinPlayers);

        FutPlayersAttributes futPlayersAttributes = futBinMappingSteps.mapFutBinPlayersAttributesToPlayersAttributes(futBinPlayersUniqueAttributes);

        List<List<Attribute>> allPossibleCombinationsForAttributes = futBinSnippingFiltersSteps.getAllPossibleCombinationsForAttributes(attributeTypes, futPlayersAttributes);

        var foundPlayersToSnipe = futBinSnippingFiltersSteps.findAllPlayersMatchingAttributes(allPossibleCombinationsForAttributes, futBinPlayers);

        System.out.println("dsfsdf");

    }

}
