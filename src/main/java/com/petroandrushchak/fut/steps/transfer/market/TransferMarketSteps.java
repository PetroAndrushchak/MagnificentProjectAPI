package com.petroandrushchak.fut.steps.transfer.market;

import com.petroandrushchak.fut.model.SearchResultState;
import com.petroandrushchak.fut.pages.fut.FUTSearchResultPage;
import com.petroandrushchak.fut.pages.fut.FUTSearchTransferMarketPage;
import com.petroandrushchak.fut.pages.helper.BrowserHelper;
import com.petroandrushchak.model.fut.Item;
import com.petroandrushchak.model.fut.PlayerItem;
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

    public void setItemSearchAttributes(Item item) {
        if (item instanceof PlayerItem playerItem) {
            setPlayerSearchAttributes(playerItem);
        }
    }

    private void setPlayerSearchAttributes(PlayerItem playerItem) {
        if (playerItem.isPlayerNamePresent()) {
            searchTransferMarketPage.setPlayerName(playerItem.getPlayerName(), playerItem.getRating());
        }

        if (playerItem.isQualityPresent()) {
            searchTransferMarketPage.selectQuality(playerItem.getQuality());
        }

        if (playerItem.isRarityPresent()) {
            searchTransferMarketPage.selectRarity(playerItem.getRarity());
        }

        if (playerItem.isPositionPresent()) {
            searchTransferMarketPage.selectPosition(playerItem.getPosition());
        }

        if (playerItem.isChemistryStylePresent()) {
            searchTransferMarketPage.selectChemistryStyle(playerItem.getChemistryStyle());
        }

        if (playerItem.isNationPresent()) {
            searchTransferMarketPage.selectNation(playerItem.getNation());
        }

        if (playerItem.isLeaguePresent()) {
            searchTransferMarketPage.selectLeague(playerItem.getLeague());
        }

        if (playerItem.isClubPresent()) {
            searchTransferMarketPage.selectClub(playerItem.getClub());
        }
    }

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
        log.info("Search result: " + searchResult);
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
