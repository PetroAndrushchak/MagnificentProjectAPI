package com.petroandrushchak.snipping;

import com.petroandrushchak.fut.helper.FUTPriceHelper;
import com.petroandrushchak.fut.pages.fut.FUTAppMainPage;
import com.petroandrushchak.fut.pages.helper.Page;
import com.petroandrushchak.fut.steps.FUTSnippingSteps;
import com.petroandrushchak.fut.steps.FUTWebAppNavigationSteps;
import com.petroandrushchak.fut.model.snipping.SnippingModel;
import com.petroandrushchak.model.fut.*;
import com.petroandrushchak.service.FutLeagueService;
import com.petroandrushchak.service.FutNationService;
import com.petroandrushchak.service.fut.FutClubServiceInternal;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.petroandrushchak.fut.helper.FUTPriceHelper.createSellPrices;
import static com.petroandrushchak.service.LocalBrowserHelper.connectToAlreadyOpenedBrowser;
import static com.petroandrushchak.service.LocalBrowserHelper.openTabWithUrlIfNotPresent;

@Slf4j
@SpringBootTest
public class Snipping {

    @Autowired FUTAppMainPage mainPage;
    @Autowired FUTWebAppNavigationSteps navigationSteps;
    @Autowired FUTSnippingSteps snippingSteps;

    @Autowired FutNationService futNationService;
    @Autowired FutLeagueService futLeagueService;
    @Autowired FutClubServiceInternal clubService;

    @Test
    void snipping() {
        connectToAlreadyOpenedBrowser();
        openTabWithUrlIfNotPresent("https://www.ea.com/ea-sports-fc/ultimate-team/web-app/");

        PlayerItem playerItem = new PlayerItem();
//
//        playerItem.setPlayerName("Nuno Mendes");
//        playerItem.setRating(82);
////
//        playerItem.setPlayerName("Nuno Mendes");
//        playerItem.setRating(82);

//        Nation nation = futNationService.getNationById(21);
//        playerItem.setNation(nation);
////
//        League league = futLeagueService.getLeagueById(13);
//        playerItem.setLeague(league);
//
//        Club team = clubService.getClubById(13);
//        playerItem.setClub(team);
//
//        playerItem.setNation(futNationService.getNationById(54));

        League league = futLeagueService.getLeagueById(13);
        playerItem.setLeague(league);
//
        Club team = clubService.getClubById(5L);
        playerItem.setClub(team);

        Position position = Position.DEFENDERS;

//        playerItem.setQuality(Quality.GOLD);

        long sellPrice = 1400;

        var searchPrices = FUTPriceHelper.createSearchPricesWithMaxBuyNowPrice(FUTPriceHelper.createPriceForSnippingFromSellPrice(sellPrice));
       // searchPrices.setMaxBuyNowPrice(3800);

        var sellPrices = createSellPrices(sellPrice);
      //  sellPrices.setBuyNowPrice(0);
        var snippingModel = SnippingModel.anSnippingModel()
                                         .withItemsSearch(playerItem)
                                         .withSearchPrices(searchPrices)
                                         .withSellPrices(sellPrices)
                                         .build();

        mainPage.waitUntilLoaded();
        navigationSteps.navigateToPage(Page.SEARCH_TRANSFER_MARKET_PAGE);
        var result = snippingSteps.performSnipping(snippingModel);

        var possibleProfit = result.getPossibleProfit();
        System.out.println("Snipping result: " + possibleProfit + " coins");

    }


}
