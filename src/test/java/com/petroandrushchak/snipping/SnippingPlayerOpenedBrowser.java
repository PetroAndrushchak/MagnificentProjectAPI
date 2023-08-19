package com.petroandrushchak.snipping;

import com.codeborne.selenide.WebDriverRunner;
import com.petroandrushchak.fut.helper.FUTPriceHelper;
import com.petroandrushchak.fut.pages.fut.FUTAppMainPage;
import com.petroandrushchak.fut.pages.helper.Page;
import com.petroandrushchak.fut.steps.FUTSnippingSteps;
import com.petroandrushchak.fut.steps.FUTWebAppNavigationSteps;
import com.petroandrushchak.model.domain.SnippingModel;
import com.petroandrushchak.model.domain.SnippingResult;
import com.petroandrushchak.model.fut.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.petroandrushchak.fut.helper.FUTPriceHelper.createSellPrices;
import static com.petroandrushchak.service.LocalBrowserHelper.connectToAlreadyOpenedBrowser;

@Slf4j
@SpringBootTest
public class SnippingPlayerOpenedBrowser {

    @Autowired FUTAppMainPage mainPage;
    @Autowired FUTWebAppNavigationSteps navigationSteps;
    @Autowired FUTSnippingSteps snippingSteps;

    @Test
    void test() {
        connectToAlreadyOpenedBrowser();

        var currentUrl = WebDriverRunner.getWebDriver().getCurrentUrl();
        if (currentUrl.contains("fifa/ultimate-team/web-app/")) {
            log.info("Current URL is {}", currentUrl);
            WebDriverRunner.getWebDriver().navigate().refresh();
        } else {
            //TODO Implement Opening FutWeb App and log in if needed
        }

        PlayerItem playerItem = new PlayerItem();

        playerItem.setPlayerName("Aymeric Laporte");
        playerItem.setRating(86);

        long sellPrice = 8100;

        var searchPrices = FUTPriceHelper.createSearchPricesWithMaxBuyNowPrice(FUTPriceHelper.createPriceForSnippingFromSellPrice(sellPrice));

        var snippingModel = SnippingModel.anSnippingModel()
                                         .withSnippingResult(new SnippingResult())
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
