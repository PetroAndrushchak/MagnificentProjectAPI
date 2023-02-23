package com.petroandrushchak;

import com.petroandrushchak.fut.helper.FUTPriceHelper;
import com.petroandrushchak.fut.pages.webdriver.WebDriverManager;
import com.petroandrushchak.fut.steps.FUTWebAppSteps;
import com.petroandrushchak.fut.steps.LogInSteps;
import com.petroandrushchak.model.fut.Item;
import com.petroandrushchak.model.fut.PlayerItem;
import com.petroandrushchak.model.fut.SnippingModel;
import com.petroandrushchak.repository.mongo.FutWebCookiesItemRepository;

import com.petroandrushchak.view.FutEaAccountView;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.petroandrushchak.fut.helper.FUTPriceHelper.createSellPrices;


@SpringBootTest
class MagnificentProjectWebServiceApplicationTests {

    @Autowired FUTWebAppSteps futWebAppSteps;
    @Autowired LogInSteps logInSteps;

    @Autowired FutWebCookiesItemRepository futWebCookiesItemRepository;

    @Test
    void testSnippingPlayer() {

        WebDriverManager.init();

        FutEaAccountView futEaAccountUiModel = FutEaAccountView.anFutEaAccount()
                                                                  .withEaLogin("petrosebas@yahoo.com")
                                                                  .withEaPassword("Sebas0025")

                                                                  .withEaEmailEmail("petrosebas@yahoo.com")
                                                                  .withEaEmailPassword("cwyrptvvthdqgojs")
                                                                  .build();

        PlayerItem playerItem = new PlayerItem();

        playerItem.setName("Lucas Digne");
        playerItem.setRating("82");

        long sellPrice = 1500;

        var searchPrices = FUTPriceHelper.createSearchPricesWithMaxBuyNowPrice(FUTPriceHelper.createPriceForSnippingFromSellPrice(sellPrice));

        var snippingModel = SnippingModel.anSnippingModel()
                                         .withItemsSearch(playerItem)
                                         .withSearchPrices(searchPrices)
                                         .withSellPrices(createSellPrices(sellPrice))
                                         .build();

        futWebAppSteps.performSnipping(futEaAccountUiModel, snippingModel);


        System.out.println("dsfsdf");

    }

}
