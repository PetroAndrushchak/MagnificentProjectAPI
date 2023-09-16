package com.petroandrushchak.analitic;

import com.petroandrushchak.fut.model.statistic.PlayerStatisticItem;
import com.petroandrushchak.fut.pages.helper.Page;
import com.petroandrushchak.fut.steps.FUTAnalyticsSteps;
import com.petroandrushchak.fut.steps.FUTWebAppNavigationSteps;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.petroandrushchak.service.LocalBrowserHelper.connectToAlreadyOpenedBrowser;
import static com.petroandrushchak.service.LocalBrowserHelper.openTabWithUrlIfNotPresent;

@SpringBootTest
public class FindPlayerAppearanceOnTM {

    @Autowired FUTWebAppNavigationSteps navigationSteps;
    @Autowired FUTAnalyticsSteps analyticsSteps;

    @Test
    void findPlayerAppearanceOnTM() {

        connectToAlreadyOpenedBrowser();
        openTabWithUrlIfNotPresent("https://www.ea.com/fifa/ultimate-team/web-app/");

        PlayerStatisticItem player = new PlayerStatisticItem();
        player.setPlayerName("Virgil van Dijk");
        player.setRating(90);
        player.setPossibleSellPrice(3000);

        navigationSteps.navigateToPage(Page.SEARCH_TRANSFER_MARKET_PAGE);
        var statistic  = analyticsSteps.performPlayerAppearanceAnalytic(player);

        System.out.println("dsfsdf");

    }
}
