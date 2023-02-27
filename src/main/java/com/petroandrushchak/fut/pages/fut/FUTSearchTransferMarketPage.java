package com.petroandrushchak.fut.pages.fut;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.petroandrushchak.aop.RealPerson;
import com.petroandrushchak.fut.exeptions.ItemCanNotBeFoundOnTheTransferMarket;
import com.petroandrushchak.fut.pages.BasePage;
import com.petroandrushchak.helper.Waiter;
import com.petroandrushchak.model.fut.TransferMarketPrices;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.springframework.stereotype.Component;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@Slf4j
@Component
public class FUTSearchTransferMarketPage extends BasePage<FUTSearchTransferMarketPage> {

    SelenideElement playerNameInputField = $(".ut-item-search-view .ut-player-search-control input");
    SelenideElement playerDropDownContainer = $(".inline-list");

    ElementsCollection playerDropDownItems = $$(".playerResultsList button");
    By playerDropDownItemNameLabel = By.cssSelector(".btn-text");
    By playerDropDownItemRatingLabel = By.cssSelector(".btn-subtext");

    SelenideElement maxBuyNowPriceInputField = $(".search-prices .price-filter:nth-of-type(6) .ut-number-input-control");
    SelenideElement minBuyNowPriceInputField = $(".search-prices .price-filter:nth-of-type(5) .ut-number-input-control");
    SelenideElement maxBidPriceInputField = $(".search-prices .price-filter:nth-of-type(3) .ut-number-input-control");
    SelenideElement minBidPriceInputField = $(".search-prices .price-filter:nth-of-type(2) .ut-number-input-control");

    SelenideElement searchButton = $(".call-to-action");

    public void setPlayerName(String playerName, int playerRating) {
        enterPlayerName(playerName);
        selectPlayerFromTheList(playerName, String.valueOf(playerRating));
    }

    @RealPerson
    private void enterPlayerName(String playerName) {
        playerNameInputField.click();
        playerNameInputField.clear();
        playerNameInputField.sendKeys(playerName);
    }

    @RealPerson
    private void selectPlayerFromTheList(String playerName, String playerRating) {
        log.info("Selecting player: " + playerName + " ,with rating: " + playerRating + " from the dropdown list");

        playerDropDownContainer.shouldBe(visible);

        log.info("Found --- " + playerDropDownItems.size() + " --- players in the dropdown list");

        var matchedPlayersLabels = playerDropDownItems.asDynamicIterable().stream()
                                                      .filter(playerLabel -> {
                                                          String name = playerLabel.find(playerDropDownItemNameLabel)
                                                                                   .getText();
                                                          String rating = playerLabel.find(playerDropDownItemRatingLabel)
                                                                                     .getText();

                                                          log.info("Found player with name: " + name + " , rating: " + rating);
                                                          if (name.equals(playerName) && rating.equals(String.valueOf(playerRating))) {
                                                              log.info("Found right player !!!");
                                                              return true;
                                                          } else {
                                                              return false;
                                                          }
                                                      }).toList();

        if (matchedPlayersLabels.size() != 1) {
            throw new ItemCanNotBeFoundOnTheTransferMarket();
        }
        matchedPlayersLabels.get(0).click();

        playerDropDownContainer.shouldBe(Condition.not(visible));
    }

    public void setSearchPrices(TransferMarketPrices prices) {
        if (prices.getMaxBuyNowPrice() == 0L) {
            clearMaxBuyNowPrice();
        } else {
            setMaxBuyNowPrice(prices.getMaxBuyNowPrice());
        }
        setMaxBidPrice(prices.getMaxBidPrice());

        setMinBuyNowPrice(prices.getMinBuyNowPrice());
        setMinBidPrice(prices.getMinBidPrice());

    }

    public void clearMaxBuyNowPrice() {
        maxBuyNowPriceInputField.click();
        Waiter.waitForOneSecond();
        maxBuyNowPriceInputField.clear();
    }

    @RealPerson
    public void setMaxBuyNowPrice(long price) {
        for (int i = 0; i < 5; i++) {
            maxBuyNowPriceInputField.click();
            maxBuyNowPriceInputField.clear();
            maxBuyNowPriceInputField.sendKeys(String.valueOf(price));
            minBidPriceInputField.click();
            Waiter.waitForOneSecond();
            if (isMaxBuyNowPriceSetCorrect(price)) {
                return;
            }
        }
        throw new RuntimeException("Max buy now price can not be set");
    }

    private boolean isMaxBuyNowPriceSetCorrect(long targetBuyNowPrice) {
        log.info("Checking if max buy now price is set correctly");
        String currentValue = getMaxBuyNowPrice();

        if (currentValue.isEmpty()) {
            log.info("Max buy now price is empty");
            return false;
        } else {
            int currentBuyNowMaxPrice = Integer.parseInt(getMaxBuyNowPrice().replace(",", ""));
            var result = currentBuyNowMaxPrice == targetBuyNowPrice;
            log.info("Max buy now price is set correctly: " + result);
            return result;
        }
    }

    private String getMaxBuyNowPrice() {
        maxBuyNowPriceInputField.click();
        return maxBuyNowPriceInputField.getValue();
    }

    @RealPerson
    public void setMinBuyNowPrice(long price) {
        minBuyNowPriceInputField.click();
        minBuyNowPriceInputField.clear();
        minBuyNowPriceInputField.sendKeys(String.valueOf(price));
    }

    @RealPerson
    public void setMaxBidPrice(long price) {
        maxBidPriceInputField.click();
        maxBidPriceInputField.clear();
        maxBidPriceInputField.sendKeys(String.valueOf(price));
    }

    @RealPerson
    public void setMinBidPrice(long price) {
        minBidPriceInputField.click();
        minBidPriceInputField.clear();
        minBidPriceInputField.sendKeys(String.valueOf(price));
    }

    public void clickSearchButton() {
        searchButton.click();
    }


    @Override
    public boolean isOpened() {
        return $(".ut-market-search-filters-view").is(visible);
    }


}
