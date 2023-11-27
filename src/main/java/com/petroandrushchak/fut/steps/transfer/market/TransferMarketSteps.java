package com.petroandrushchak.fut.steps.transfer.market;

import com.petroandrushchak.fut.api.AuctionInfoItem;
import com.petroandrushchak.fut.api.TransfermarketSearchResponse;
import com.petroandrushchak.fut.api.helpers.AuctionInfoHelper;
import com.petroandrushchak.fut.model.SearchResultState;
import com.petroandrushchak.fut.model.TransferMarketPrices;
import com.petroandrushchak.fut.pages.fut.FUTSearchResultPage;
import com.petroandrushchak.fut.pages.fut.FUTSearchTransferMarketPage;
import com.petroandrushchak.fut.pages.helper.BrowserHelper;
import com.petroandrushchak.model.fut.Item;
import com.petroandrushchak.model.fut.PlayerItem;
import com.petroandrushchak.service.DevToolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class TransferMarketSteps {

    @Autowired FUTSearchTransferMarketPage searchTransferMarketPage;
    @Autowired FUTSearchResultPage searchResultPage;

    @Autowired DevToolService devToolService;

    public void setItemSearchAttributes(Item item) {
        if (item instanceof PlayerItem playerItem) {
            setPlayerSearchAttributes(playerItem);
        }
    }

    private void setPlayerSearchAttributes(PlayerItem playerItem) {
        if (playerItem.isPlayerNamePresent()) {
            if (playerItem.isPlayerNickNamePresent()) {
                searchTransferMarketPage.setPlayerName(playerItem.getNickName(), playerItem.getRating());
            } else {
                searchTransferMarketPage.setPlayerName(playerItem.getPlayerName(), playerItem.getRating());
            }
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

    public List<AuctionInfoItem> performOneSearch(TransferMarketPrices searchPrices) {
        log.info("Performing search, with prices: Min bid: " + searchPrices.getMinBidPrice() +
                ", Max bin: " + searchPrices.getMaxBidPrice() +
                ", Min BuyNow: " + searchPrices.getMinBuyNowPrice() +
                ", Max BuyNow: " + searchPrices.getMaxBuyNowPrice());
        searchTransferMarketPage.setSearchPrices(searchPrices);
        var response = devToolService.getResponseFromTheRequestWithAction("/ut/game/fc24/transfermarket",
                () -> searchTransferMarketPage.clickSearchButton(),
                TransfermarketSearchResponse.class);

        List<AuctionInfoItem> transferMarketSearchResult = response.getAuctionInfo();
        AuctionInfoHelper.logAuctionInfoData(transferMarketSearchResult);
        searchResultPage.clickBackToSearchPage();
        return transferMarketSearchResult;
    }


    public void goBackFromSearchResultToSearchPage() {
        BrowserHelper.doActionUntilCondition(searchResultPage::clickBackToSearchPage, searchTransferMarketPage::isOpened);
    }

}
