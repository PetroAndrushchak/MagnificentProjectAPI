package com.petroandrushchak.fut.steps;

import com.petroandrushchak.fut.model.SearchResultState;
import com.petroandrushchak.fut.pages.fut.FUTSearchResultPage;
import com.petroandrushchak.fut.pages.fut.FUTSearchTransferMarketPage;
import com.petroandrushchak.fut.steps.transfer.market.TransferMarketSteps;
import com.petroandrushchak.helper.Waiter;
import com.petroandrushchak.model.domain.SnippingModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

import static com.petroandrushchak.fut.model.SearchResultState.ITEM_BUYING_BOUGHT_SUCCESSFULLY;

@Slf4j
@Component
public class FUTSnippingSteps {

    @Autowired TransferMarketSteps transferMarketSteps;
    @Autowired FUTSearchTransferMarketPage searchTransferMarketPage;
    @Autowired FUTSearchResultPage searchResultPage;

    public void performSnipping(SnippingModel snippingModel) {
        final int NUMBER_OF_SNIPES = 40;

        transferMarketSteps.setItemSearchAttributes(snippingModel.getItemsSearch());

        IntStream.range(1, NUMBER_OF_SNIPES).forEach($ -> {
            log.info("Snipping attempt " + $ + " ouf of " + NUMBER_OF_SNIPES);
            searchTransferMarketPage.setSearchPrices(snippingModel.getSearchPrices());
            searchTransferMarketPage.clickSearchButton();
            snippingModel.snippingResult().incrementNumberOfSearches();

            if (transferMarketSteps.isItemsFoundAfterSearch()) {
                snippingModel.snippingResult().incrementNumberOfSearchesItemFound();
                SearchResultState buyingStatus = searchResultPage.buyDefaultSelectedItem();
                log.info("Buying status: " + buyingStatus);
                if (buyingStatus == ITEM_BUYING_BOUGHT_SUCCESSFULLY) {
                    snippingModel.snippingResult().incrementNumberOfSearchesItemBought();
                    long boughtPrice = searchResultPage.getItemBoughtPrice();
                    log.info("Item is bought for " + boughtPrice + " , selling item for: " + snippingModel.getSellPrices()
                                                                                                           .getBuyNowPrice());
                    searchResultPage.sellItem(snippingModel.getSellPrices());
                }
            } else {
                Waiter.waitForOneSecond();
            }
            transferMarketSteps.goBackFromSearchResultToSearchPage();
            snippingModel.updateSearchPrices();
        });
    }





}
