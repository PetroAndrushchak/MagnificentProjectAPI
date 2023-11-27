package com.petroandrushchak.fut.pages.fut;

import com.codeborne.selenide.ClickOptions;
import com.codeborne.selenide.SelenideElement;
import com.petroandrushchak.aop.RealPerson;
import com.petroandrushchak.fut.helper.FUTPriceHelper;
import com.petroandrushchak.fut.model.SearchResultState;
import com.petroandrushchak.fut.pages.BasePage;
import com.petroandrushchak.fut.pages.helper.BrowserHelper;
import com.petroandrushchak.helper.Waiter;
import com.petroandrushchak.fut.model.SellPrices;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.NoSuchElementException;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static com.petroandrushchak.fut.model.SearchResultState.ITEM_BUYING_BOUGHT_SUCCESSFULLY;

@Slf4j
@Component
public class FUTSearchResultPage extends BasePage<FUTSearchResultPage> {

    SelenideElement searchResultsPageTitle = $x("//*[contains(text(), 'Search Results')]");
    SelenideElement noResultFoundLabel = $(".no-results-icon");

    SelenideElement buyNowButton = $(".buyButton");
    SelenideElement listOnTransferMarketButton = $(".accordian");
    SelenideElement listForTransferItemButton = $(".DetailView .panelActions > .btn-standard");

    SelenideElement buyNowPriceInputField = $(".DetailView .panelActions .panelActionRow:nth-of-type(3) input");
    SelenideElement startPriceInputField = $(".DetailView .panelActions .panelActionRow:nth-of-type(2) input");

    SelenideElement bidPriceForBoughtItem = $(".DetailView .ut-item-details--metadata .currency-coins");

    SelenideElement buyNowConfirmationModal = $(".ea-dialog-view-type--message .ea-dialog-view--body");
    SelenideElement buyNowConfirmationModalOKButton = $(".ea-dialog-view-type--message .ea-dialog-view--body button:nth-of-type(1) span:nth-of-type(1)");

    SelenideElement defaultSelectedItemExpired = $x("//div[contains(@class, 'DetailView')]//span[contains(., 'Expired')]");

    SelenideElement notificationAlert = $("#NotificationLayer .Notification");
    SelenideElement notificationAlertCloseButton = $("#NotificationLayer .Notification .icon_close");

    SelenideElement goBackToSearchPageButton = $x("//*[contains(text(), 'Search Results')]/../button[@class = 'ut-navigation-button-control']");

    public boolean isNoResultFoundLabelCurrentlyPresent() {
        return noResultFoundLabel.is(visible);
    }

    public boolean isBuyNowButtonVisibleWithOutWaiting() {
        return buyNowButton.is(visible);
    }

    public void clickBuyNowButton() {
        log.info("Clicking buy now button");
        buyNowButton.click();
        log.info("Buy now button clicked");
    }

    public void buyDefaultSelectedItem() {
        BrowserHelper.doActionUntilConditionWithNoWait(this::clickBuyNowButton, this::buyNowConfirmationModalWindowsPresent);
        BrowserHelper.doActionUntilConditionWithNoWait(this::clickOnButtonBuyNowModalWindow, () -> !buyNowConfirmationModalWindowsPresent());
    }

    public boolean buyNowConfirmationModalWindowsPresent() {
        log.info("Checking if buy now confirmation modal window is present");
        var isPresent = buyNowConfirmationModal.is(visible);
        log.info("Buy now confirmation modal window is present: {}", isPresent);
        return isPresent;
    }

    public void clickOnButtonBuyNowModalWindow() {
        log.info("Clicking on OK button on the buy now confirmation modal window");
        buyNowConfirmationModalOKButton.click();
    }

    public SearchResultState getBuyingItemResult() {
        LocalTime timeToFinish = LocalTime.now().plusSeconds(5);
        while (LocalTime.now().isBefore(timeToFinish)) {
            if (isErrorNotificationMessagePresent()) {
                closeErrorNotificationMessage();
                return SearchResultState.ITEM_BUYING_BOUGHT_NOT_SUCCESSFULLY;
            } else if (isDefaultSelectedItemCurrentlyExpired()) {
                return SearchResultState.ITEM_BUYING_BOUGHT_NOT_SUCCESSFULLY;
            } else if (isListOnTransferMarketButtonVisibleWithOutWaiting()) {
                return ITEM_BUYING_BOUGHT_SUCCESSFULLY;
            }
        }
        return SearchResultState.ITEM_BUYING_BOUGHT_NOT_SUCCESSFULLY;
    }

    public boolean isErrorNotificationMessagePresent() {
        return notificationAlert.is(visible);
    }

    public boolean isDefaultSelectedItemCurrentlyExpired() {
        return defaultSelectedItemExpired.is(visible);
    }

    public boolean isListOnTransferMarketButtonVisibleWithOutWaiting() {
        return listOnTransferMarketButton.is(visible);
    }

    @RealPerson
    public void closeErrorNotificationMessage() {
        try {
            BrowserHelper.doActionUntilConditionIgnoringElementNotFound(notificationAlertCloseButton::click,
                    () -> !isErrorNotificationMessagePresent());
        } catch (NoSuchElementException e) {
            log.error("Error while closing notification message");
            if (isErrorNotificationMessagePresent()) {
                log.error("Notification message is present");
                throw e;
            } else {
                log.info("Notification message is not present");
            }
        }
    }

    public long getItemBoughtPrice() {
        String priceString = getBidPriceForBoughtItem();
        return FUTPriceHelper.removeCommaFromStringPrice(priceString);
    }

    public String getBidPriceForBoughtItem() {
        return bidPriceForBoughtItem.getText();
    }

    @RealPerson
    public void sellItem(SellPrices sellPrices) {
        BrowserHelper.doActionUntilCondition(this::clickListOnTransferMarketButton, this::isListItemButtonPresent);
        Waiter.waitForOneSecond();
        setMaxBuyNowPrice(sellPrices.getBuyNowPrice());
        clickListItemButton();
        Waiter.waitFor(Duration.ofSeconds(2));
    }

    @RealPerson
    public void clickListOnTransferMarketButton() {
        listOnTransferMarketButton.click();
    }

    public boolean isListItemButtonPresent() {
        return listForTransferItemButton.is(visible);
    }

    @RealPerson
    public void setMaxBuyNowPrice(long price) {
        for (int i = 0; i < 5; i++) {
            buyNowPriceInputField.click();
            Waiter.waitFor(Duration.ofMillis(500));
            buyNowPriceInputField.sendKeys(String.valueOf(price));
            Waiter.waitFor(Duration.ofMillis(500));
            startPriceInputField.click();
            Waiter.waitFor(Duration.ofMillis(500));
            if (isMaxBuyNowPriceSetCorrect(price)) {
                return;
            }
        }
        throw new RuntimeException("Max buy now price can not be set");
    }

    private boolean isMaxBuyNowPriceSetCorrect(long targetBuyNowPrice) {
        long currentBuyNowMaxPrice = FUTPriceHelper.removeCommaFromStringPrice(getMaxBuyNowPrice());
        return currentBuyNowMaxPrice == targetBuyNowPrice;
    }

    private String getMaxBuyNowPrice() {
        buyNowPriceInputField.click();
        return buyNowPriceInputField.getValue();
    }

    @RealPerson
    public void clickListItemButton() {
        listForTransferItemButton.click();
    }

    @RealPerson
    public void clickBackToSearchPage() {
        try {
            goBackToSearchPageButton.click();
        } catch (Exception e) {
            log.error("Error while clicking back button on the search page");
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public boolean isOpened() {
        return searchResultsPageTitle.is(visible);

    }
}
