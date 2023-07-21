package com.petroandrushchak;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.petroandrushchak.fut.helper.FUTPriceHelper;
import com.petroandrushchak.fut.pages.fut.FUTAppMainPage;
import com.petroandrushchak.fut.pages.helper.Page;
import com.petroandrushchak.fut.steps.FUTSnippingSteps;
import com.petroandrushchak.fut.steps.FUTWebAppNavigationSteps;
import com.petroandrushchak.model.fut.*;
import com.petroandrushchak.service.FutClubService;
import com.petroandrushchak.service.FutLeagueService;
import com.petroandrushchak.service.FutNationService;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.petroandrushchak.fut.helper.FUTPriceHelper.createSellPrices;
import static com.petroandrushchak.service.LocalBrowserHelper.connectToAlreadyOpenedBrowser;

@SpringBootTest
public class FutSnippingPlayerTest {

    @Autowired FUTAppMainPage mainPage;
    @Autowired FUTWebAppNavigationSteps navigationSteps;
    @Autowired FUTSnippingSteps snippingSteps;

    @Autowired FutLeagueService futLeagueService;
    @Autowired FutClubService futClubService;
    @Autowired FutNationService futNationService;

    @Test
    void test() {
        connectToAlreadyOpenedBrowser();

        var currentUrl = WebDriverRunner.getWebDriver().getCurrentUrl();
        if (currentUrl.contains("fifa/ultimate-team/web-app/")) {
            WebDriverRunner.getWebDriver().navigate().refresh();
        } else {
            //TODO Implement Opening FutWeb App and log in if needed
        }

        PlayerItem playerItem = new PlayerItem();
//        playerItem.setPlayerName("Kyle Walker");
//        playerItem.setRating(85);
//        playerItem.setLevel(Quality.SPECIAL);

        //GER 1
//        playerItem.setQuality(Quality.GOLD);
//        playerItem.setRarity(Rarity.COMMON);
//        playerItem.setPosition(Position.CF);
//        playerItem.setChemistryStyle(ChemistryStyle.ARCHITECT);
//
        playerItem.setQuality(Quality.GOLD);
        playerItem.setLeague(futLeagueService.getLeagueById(13));
        playerItem.setClub(futClubService.getClubById(10));

//        //Union Berlin
//        playerItem.setClub(futClubService.getClubById(1831L));

        long sellPrice = 3100;

        var searchPrices = FUTPriceHelper.createSearchPricesWithMaxBuyNowPrice(FUTPriceHelper.createPriceForSnippingFromSellPrice(sellPrice));

        var snippingModel = SnippingModel.anSnippingModel()
                                         .withItemsSearch(playerItem)
                                         .withSearchPrices(searchPrices)
                                         .withSellPrices(createSellPrices(sellPrice))
                                         .build();


        mainPage.waitUntilLoaded();
        navigationSteps.navigateToPage(Page.SEARCH_TRANSFER_MARKET_PAGE);
        snippingSteps.performSnipping(snippingModel);

        System.out.println("dfgdgdfg");



    }


}
