package com.petroandrushchak.snipping;

import com.petroandrushchak.futbin.models.FutBinNewRawPlayer;
import com.petroandrushchak.futwiz.steps.FutWizParsePlayersSteps;
import com.petroandrushchak.helper.ListHelper;
import com.petroandrushchak.model.third.party.sites.ThirdPartySitePlayer;
import com.petroandrushchak.service.FutBinService;
import com.petroandrushchak.steps.FutBinMappingSteps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
public class CompareFutBinAndFutWiz {

    @Autowired FutBinService futBinService;
    @Autowired FutBinMappingSteps futBinMappingSteps;


    @Autowired FutWizParsePlayersSteps futWizParsePlayersSteps;

    @Test
    void compareFutBinAndFutWiz() {
        //English EPL players

        var playersFileName = "english_players_data.json";
        List<FutBinNewRawPlayer> futBinPlayers = futBinService.parsePlayersFromJsonFile(playersFileName);
        List<ThirdPartySitePlayer> futBinRawPlayers = futBinMappingSteps.mapNewRawPlayersToPlayers(futBinPlayers);

        String futWizPlayersLink = "https://www.futwiz.com/en/fc24/players?page=0&release[]=commongold&leagues[]=13";
        List<ThirdPartySitePlayer> futWizRawPlayers = futWizParsePlayersSteps.parsePlayersFromFutWiz(futWizPlayersLink);

        List<ThirdPartySitePlayer> playersPresentOnlyInFutBin = new ArrayList<>(futBinRawPlayers);
        playersPresentOnlyInFutBin.removeAll(futWizRawPlayers);

        List<ThirdPartySitePlayer> playersPresentOnlyInFutWiz = new ArrayList<>(futWizRawPlayers);
        playersPresentOnlyInFutWiz.removeAll(futBinRawPlayers);

        //unique players in futbin
        Set<ThirdPartySitePlayer> uniquePlayersInFutBin = new HashSet<>(futBinRawPlayers);

        //unique players in futwiz
        Set<ThirdPartySitePlayer> uniquePlayersInFutWiz = new HashSet<>(futWizRawPlayers);

        List<ThirdPartySitePlayer> duplicatedPlayersInFutBin = ListHelper.findDuplicates(futBinRawPlayers);
        List<ThirdPartySitePlayer> duplicatedPlayersInFutWiz = ListHelper.findDuplicates(futWizRawPlayers);

        System.out.println("dsfsdf");

    }



}
