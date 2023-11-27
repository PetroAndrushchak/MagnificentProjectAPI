package com.petroandrushchak.analitic;

import com.petroandrushchak.fut.model.statistic.PlayerCsvStatisticItem;
import com.petroandrushchak.fut.model.statistic.PlayerStatisticItem;
import com.petroandrushchak.fut.pages.helper.Page;
import com.petroandrushchak.fut.steps.FUTAnalyticsSteps;
import com.petroandrushchak.fut.steps.FUTWebAppNavigationSteps;
import com.petroandrushchak.service.FutAppearanceStatisticService;
import com.petroandrushchak.steps.FutPlayersMapperSteps;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.petroandrushchak.service.LocalBrowserHelper.connectToAlreadyOpenedBrowser;
import static com.petroandrushchak.service.LocalBrowserHelper.openTabWithUrlIfNotPresent;

@SpringBootTest
public class FindPlayerAppearanceOnTM {

    @Autowired FUTWebAppNavigationSteps navigationSteps;
    @Autowired FUTAnalyticsSteps analyticsSteps;
    @Autowired FutAppearanceStatisticService futAppearanceStatisticService;
    @Autowired FutPlayersMapperSteps futPlayersMapperSteps;

    @Test
    void findPlayerAppearanceOnTM() {

        connectToAlreadyOpenedBrowser();
        openTabWithUrlIfNotPresent("https://www.ea.com/ea-sports-fc/ultimate-team/web-app/");

        //Get last 10 players from CSV file (without appearance analysis) and start appearance statistics analysis
        List<PlayerCsvStatisticItem> playersToAnaliseCsvFile = futAppearanceStatisticService.getPlayersWithoutAppearanceRating(10, 13_000);

        List<PlayerStatisticItem> playersToAnalise = futPlayersMapperSteps.mapPlayers(playersToAnaliseCsvFile);

        playersToAnalise.forEach(player -> {
            navigationSteps.navigateToPage(Page.SEARCH_TRANSFER_MARKET_PAGE);
            var statistic = analyticsSteps.performPlayerAppearanceAnalytic(player);
            System.out.println("dsfsdfsdf");
        });

        //Manually update appearance rating for each player

        //Repeat previous step until all players will have appearance rating


        System.out.println("dsfsdf");

    }
}
