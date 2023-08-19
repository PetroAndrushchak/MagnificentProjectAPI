package com.petroandrushchak.snipping;

import com.petroandrushchak.fut.helper.FUTPriceHelper;
import com.petroandrushchak.fut.pages.webdriver.WebDriverManager;
import com.petroandrushchak.fut.steps.FUTWebAppSteps;
import com.petroandrushchak.fut.steps.LogInSteps;
import com.petroandrushchak.model.domain.SnippingResult;
import com.petroandrushchak.model.fut.PlayerItem;
import com.petroandrushchak.model.domain.SnippingModel;
import com.petroandrushchak.repository.mongo.FutWebCookiesItemRepository;

import com.petroandrushchak.view.FutEaAccountView;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.petroandrushchak.fut.helper.FUTPriceHelper.createSellPrices;
import static com.petroandrushchak.service.LocalBrowserHelper.connectToAlreadyOpenedBrowser;


@Slf4j
@SpringBootTest
class SnippingPlayerNewBrowser {

    @Autowired FUTWebAppSteps futWebAppSteps;
    @Autowired LogInSteps logInSteps;

    @Autowired FutWebCookiesItemRepository futWebCookiesItemRepository;

    @Test
    void testSnippingPlayer() {

        connectToAlreadyOpenedBrowser();


        FutEaAccountView futEaAccountUiModel = FutEaAccountView.anFutEaAccount()
                                                               .withEaLogin("petrosebas@yahoo.com")
                                                               .withEaPassword("Sebas0025")

                                                               .withEaEmailEmail("petrosebas@yahoo.com")
                                                               .withEaEmailPassword("cwyrptvvthdqgojs")
                                                               .build();

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

        futWebAppSteps.performSnipping(futEaAccountUiModel, snippingModel);
        System.out.println("dsfsdf");


    }

}
