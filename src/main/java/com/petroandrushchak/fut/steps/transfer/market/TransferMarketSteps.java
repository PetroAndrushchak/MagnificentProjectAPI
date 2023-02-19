package com.petroandrushchak.fut.steps.transfer.market;

import com.petroandrushchak.fut.model.SearchResultState;
import com.petroandrushchak.fut.pages.fut.FUTSearchResultPage;
import com.petroandrushchak.fut.pages.fut.FUTSearchTransferMarketPage;
import com.petroandrushchak.fut.pages.helper.BrowserHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Optional;

@Slf4j
@Component
public class TransferMarketSteps {

    @Autowired FUTSearchTransferMarketPage searchTransferMarketPage;
    @Autowired FUTSearchResultPage searchResultPage;

    public boolean isItemsFoundAfterSearch() {
        log.info("Checking search result .. ");
        Optional<SearchResultState> searchResult = Optional.empty();
        LocalTime finishTime = LocalTime.now().plusSeconds(10);
        while (LocalTime.now().isBefore(finishTime)) {
            if (searchResultPage.isBuyNowButtonVisibleWithOutWaiting()) {
                searchResult = Optional.of(SearchResultState.ITEM_TO_BUY_FOUND);
                break;
            }
            if (searchResultPage.isNoResultFoundLabelCurrentlyPresent()) {
                searchResult = Optional.of(SearchResultState.ITEM_NOT_FOUND);
                break;
            }
        }
        if (searchResult.isPresent()) {
            return searchResult.get() == SearchResultState.ITEM_TO_BUY_FOUND;
        } else {
            throw new UnsupportedOperationException("Something happen while searching ittem");
        }
    }

    public void goBackFromSearchResultToSearchPage() {
        BrowserHelper.doActionUntilCondition(searchResultPage::clickBackToSearchPage, searchTransferMarketPage::isOpened);
    }

}
