package com.petroandrushchak.futwiz;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.petroandrushchak.fut.model.FutPlayersAttributes;
import com.petroandrushchak.fut.model.filters.Attribute;
import com.petroandrushchak.fut.model.filters.AttributeType;
import com.petroandrushchak.futwiz.steps.FutWizParsePlayersSteps;
import com.petroandrushchak.model.third.party.sites.ThirdPartySitePlayer;
import com.petroandrushchak.model.third.party.sites.ThirdPartySitePlayersAttributes;
import com.petroandrushchak.registry.FutWizDataRegistry;
import com.petroandrushchak.steps.FutBinSnippingFiltersSteps;
import com.petroandrushchak.steps.FutWizMappingSteps;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.petroandrushchak.fut.model.filters.AttributeType.*;
import static com.petroandrushchak.fut.model.filters.AttributeType.LEAGUE;

@SpringBootTest
public class FindPlayersFilterWithBinPriceMoreThanSpecific {

    @Autowired FutWizParsePlayersSteps futWizParsePlayersSteps;
    @Autowired FutBinSnippingFiltersSteps futBinSnippingFiltersSteps;
    @Autowired FutWizMappingSteps futWizMappingSteps;

    JsonMapper jsonMapper = new JsonMapper();

    @SneakyThrows
    @Test
    void findPlayersFiltersWithBinPriceMoreThanSpecific() {

//        var filteredPage = "https://www.futwiz.com/en/fc24/players?page=0&release[]=commongold";
//        var players = futWizParsePlayersSteps.parsePlayersFromFutWiz(filteredPage);
//        //Store FutWiz players to json file
//        jsonMapper.writeValue(FutWizDataRegistry.futWizPlayersWithSpecificFilterFilePath().toFile(), players);

        List<ThirdPartySitePlayer> thirdPartySitePlayers = jsonMapper.readValue(FutWizDataRegistry.futWizPlayersWithSpecificFilterFilePath().toFile(),  new TypeReference<List<ThirdPartySitePlayer>>() { });

        ThirdPartySitePlayersAttributes futWizPlayersUniqueAttributes = futBinSnippingFiltersSteps.getUniquePlayersAttributes(thirdPartySitePlayers);
        FutPlayersAttributes futPlayersAttributes = futWizMappingSteps.mapThirdPartyPlayersAttributesToPlayersAttributes(futWizPlayersUniqueAttributes);
        List<AttributeType> attributeToFilter = List.of(NATION, CLUB, LEAGUE);
        List<List<Attribute>> allPossibleCombinationsForAttributes = futBinSnippingFiltersSteps.getAllPossibleCombinationsForAttributes(attributeToFilter, futPlayersAttributes);

        var foundPlayersToSnipe = futBinSnippingFiltersSteps.findAllPlayersMatchingAttributes(allPossibleCombinationsForAttributes, thirdPartySitePlayers);

        System.out.println("sdfsdf");

    }
}
