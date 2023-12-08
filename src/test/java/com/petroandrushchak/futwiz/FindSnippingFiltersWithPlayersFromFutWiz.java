package com.petroandrushchak.futwiz;

import com.petroandrushchak.fut.model.FutPlayersAttributes;
import com.petroandrushchak.fut.model.filters.Attribute;
import com.petroandrushchak.fut.model.filters.AttributeType;
import com.petroandrushchak.futwiz.steps.FutWizParsePlayersSteps;
import com.petroandrushchak.model.third.party.sites.ThirdPartySitePlayersAttributes;
import com.petroandrushchak.steps.FutBinSnippingFiltersSteps;
import com.petroandrushchak.steps.FutWizMappingSteps;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.petroandrushchak.fut.model.filters.AttributeType.*;

@SpringBootTest
public class FindSnippingFiltersWithPlayersFromFutWiz {

    @Autowired FutWizParsePlayersSteps futWizParsePlayersSteps;
    @Autowired FutWizMappingSteps futWizMappingSteps;
    @Autowired FutBinSnippingFiltersSteps futBinSnippingFiltersSteps;

    @SneakyThrows
    @Test
    void parsePlayersFromFutWiz() {

     //   var filteredPage = "https://www.futwiz.com/en/fc24/players?page=0&release[]=commongold&release[]=raregold&leagues[]=13&teams[]=5";
    //    var filteredPage = "https://www.futwiz.com/en/fc24/players?page=0&release[]=raregold&release[]=commongold&leagues[]=19";
        var filteredPage = "https://www.futwiz.com/en/fc24/players?page=0&release[]=raregold&release[]=commongold&leagues[]=13&leagues[]=16&leagues[]=31&leagues[]=19&leagues[]=53&leagues[]=13";
      //  var filteredPage = "https://www.futwiz.com/en/fc24/players?page=0&release[]=raregold&release[]=commongold&leagues[]=13&leagues[]=16&leagues[]=31&leagues[]=19&leagues[]=53";

        var players = futWizParsePlayersSteps.parsePlayersFromFutWiz(filteredPage);

        ThirdPartySitePlayersAttributes futWizPlayersUniqueAttributes = futBinSnippingFiltersSteps.getUniquePlayersAttributes(players);
        FutPlayersAttributes futPlayersAttributes = futWizMappingSteps.mapThirdPartyPlayersAttributesToPlayersAttributes(futWizPlayersUniqueAttributes);
        List<AttributeType> attributeTypes = List.of(POSITION, NATION, CLUB, LEAGUE);
        List<List<Attribute>> allPossibleCombinationsForAttributes = futBinSnippingFiltersSteps.getAllPossibleCombinationsForAttributes(attributeTypes, futPlayersAttributes);
        var foundPlayersToSnipe = futBinSnippingFiltersSteps.findAllPlayersMatchingAttributes(allPossibleCombinationsForAttributes, players);

        //read from file with path /Users/pandrushchak.appwell/Workspace/MagnificentProjectAPI/futwizPlayersPage.html

        //  var pageContent = Files.readString(Path.of("/Users/pandrushchak.appwell/Workspace/MagnificentProjectAPI/futwizPlayersPage.html"));

        System.out.println("dsfsdf");
    }



    //        connectToAlreadyOpenedBrowser();
//        Selenide.open("https://www.futwiz.com/en/fc24/players");

}
