package com.petroandrushchak.fut.steps;

import com.petroandrushchak.exceptions.fut.CanNotFindPlayerPriceToSnipe;
import com.petroandrushchak.fut.api.AuctionInfoItem;
import com.petroandrushchak.fut.api.TransfermarketSearchResponse;
import com.petroandrushchak.fut.api.helpers.AuctionInfoHelper;
import com.petroandrushchak.fut.helper.FUTPriceHelper;
import com.petroandrushchak.fut.model.SearchResultState;
import com.petroandrushchak.fut.model.SellPrices;
import com.petroandrushchak.fut.model.TransferMarketPrices;
import com.petroandrushchak.fut.model.snipping.SnippingResult;
import com.petroandrushchak.fut.pages.fut.FUTSearchResultPage;
import com.petroandrushchak.fut.pages.fut.FUTSearchTransferMarketPage;
import com.petroandrushchak.fut.steps.transfer.market.TransferMarketSteps;
import com.petroandrushchak.helper.Waiter;
import com.petroandrushchak.fut.model.snipping.SnippingModel;
import com.petroandrushchak.service.DevToolService;
import com.petroandrushchak.service.fut.FutApiService;
import com.petroandrushchak.service.helpers.FutApiHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.petroandrushchak.fut.configs.SnippingConfigs.NUMBER_OF_SNIPES;
import static com.petroandrushchak.fut.model.SearchResultState.ITEM_BUYING_BOUGHT_SUCCESSFULLY;

@Slf4j
@Component
public class FUTSnippingSteps {

    @Autowired FUTAnalyticsSteps futAnalyticsSteps;

    @Autowired DevToolService devToolService;

    @Autowired TransferMarketSteps transferMarketSteps;
    @Autowired FUTSearchTransferMarketPage searchTransferMarketPage;
    @Autowired FUTSearchResultPage searchResultPage;
    @Autowired FutApiService futApiService;

    SnippingModel snippingModel;
    SnippingResult snippingResult = new SnippingResult();

    public SnippingResult performSnipping(SnippingModel initSnippingModel) {
        this.snippingModel = initSnippingModel;

        transferMarketSteps.setItemSearchAttributes(snippingModel.getItemsSearch());

      //  var priceToSell = findPriceToSellItem(snippingModel.getSearchPrices(), snippingModel.getSellPrices());

        IntStream.range(1, NUMBER_OF_SNIPES).forEach($ -> {
            log.info("Snipping attempt " + $ + " ouf of " + NUMBER_OF_SNIPES);
            performOneSnipe(snippingModel);
        });

        return snippingResult;
    }

    private boolean wasBidByUIAlready;

    private void performOneSnipe(SnippingModel snippingModel) {
        var oneSnippingResult = SnippingResult.createOneSnippingResult();
        var searchPrices = snippingModel.getSearchPrices();

        log.info("Snipe: Min bid: " + searchPrices.getMinBidPrice() + ", Min BuyNow: " + searchPrices.getMinBuyNowPrice() + ", Max bin: " + searchPrices.getMaxBidPrice() + ", Max BuyNow: " + searchPrices.getMaxBuyNowPrice());
        searchTransferMarketPage.setSearchPrices(searchPrices);
        var transferMarketResponse = devToolService.getResponseFromTheRequestWithAction("/ut/game/fc24/transfermarket",
                () -> searchTransferMarketPage.clickSearchButton(),
                TransfermarketSearchResponse.class);

        if (wasBidByUIAlready) {
            if (transferMarketResponse.isAnyItemsFound()) {
                var cheapestItem = transferMarketResponse.getTheMostCheapestItem();
                var wasBought = futApiService.buyItem(cheapestItem.getTradeIdStr(), cheapestItem.getBuyNowPrice());
                if (wasBought) {
                    oneSnippingResult.setBoughtItem(cheapestItem);

                    var wasAllOkay = futApiService.sellItem(cheapestItem.getItemData().getId().toString());
                    if (wasAllOkay) {
                        futApiService.sellItemInAuctionHouse(cheapestItem.getItemData().getId().toString(),
                                snippingModel.getSellPrices().getBuyNowPrice(),
                                snippingModel.getSellPrices().getStartPrice());
                    }
                }
            }
        } else {
            if (transferMarketResponse.isAnyItemsFound()) {

                var requestResponseWrapper = devToolService.getRequestResponseFromTheRequestWithAction("/ut/game/fc24/trade/", DevToolService.RequestMethod.PUT, () -> searchResultPage.buyDefaultSelectedItem());
                futApiService.saveBuyItemRequestInfo(requestResponseWrapper.getRequest());
                wasBidByUIAlready = true;

                SearchResultState buyingStatus = searchResultPage.getBuyingItemResult();

                log.info("Buying status: " + buyingStatus);
                if (buyingStatus == ITEM_BUYING_BOUGHT_SUCCESSFULLY) {
                    long boughtPrice = searchResultPage.getItemBoughtPrice();

                    var tradeId = FutApiHelper.getTradeIdFromBuyItemRequestUrl(requestResponseWrapper.getRequest().getUrl());
                    var boughtItem = transferMarketResponse.getAuctionInfoItemByTradeId(tradeId);
                    oneSnippingResult.setBoughtItem(boughtItem);

                    log.info("Item is bought for " + boughtPrice + " , selling item for: " + snippingModel.getSellPrices()
                                                                                                          .getBuyNowPrice());
                    searchResultPage.sellItem(snippingModel.getSellPrices());
                }
            }
        }
        Waiter.waitForOneSecond();

        List<AuctionInfoItem> transferMarketSearchResult = transferMarketResponse.getAuctionInfo();
        AuctionInfoHelper.logAuctionInfoData(transferMarketSearchResult);

        //If safe to buy passed, can buy now
        oneSnippingResult.setSearchPrices(searchPrices);
        oneSnippingResult.setSellPrices(snippingModel.getSellPrices());
        oneSnippingResult.setFoundItems(transferMarketSearchResult);
        snippingResult.addOneSnippingResult(oneSnippingResult);


        transferMarketSteps.goBackFromSearchResultToSearchPage();
        snippingModel.updateSearchPrices();

    }

    private TransferMarketPrices updateIfNeededSearchAndSellPrices() {
        log.info("Checking if need to update search prices");
        TransferMarketPrices newSearchPrices = snippingModel.getSearchPrices().copy();
        newSearchPrices = FUTPriceHelper.updatePrices(newSearchPrices);
        newSearchPrices.setMaxBuyNowPrice(snippingModel.getSellPrices().getBuyNowPrice());
        var searchResult = transferMarketSteps.performOneSearch(newSearchPrices);

        if (searchResult.size() >= 20 || !AuctionInfoHelper.areItemsOkayToSnipe(searchResult)) {
            log.info("Search Result is with size: " + searchResult.size() + " Is Search Result with Max expire time: " + AuctionInfoHelper.areItemsOkayToSnipe(searchResult));
            searchResult = repeatSearch(newSearchPrices);
            if (searchResult.size() >= 20 || !AuctionInfoHelper.areItemsOkayToSnipe(searchResult)) {
                log.info("Repeated Search Result is with size: " + searchResult.size() + " Is Search Result with Max expire time: " + AuctionInfoHelper.areItemsOkayToSnipe(searchResult));


            }

        }

        //Items number is more than 20, and expiration time is less than 57 minutes


        //Items not found
        //Items found with 59 minutes expiration time and next search result with lower price is less than 5 items and expiration time is more than 57 minutes
        //Items found with less than 57 minutes expiration time, and next search result with lower price is not found or less than 5 items.
        return null;
    }

    private List<AuctionInfoItem> repeatSearch(TransferMarketPrices searchPrices) {
        Waiter.waitRandomTimeFromTwoToThreeMinutes();
        var newSearchPrices = FUTPriceHelper.updatePrices(searchPrices);
        return transferMarketSteps.performOneSearch(newSearchPrices);

    }

    private long findPriceToSellItem(TransferMarketPrices currentTransferMarketPrices, SellPrices currentSellPrices) {

        //Check if player available on the market
//        var isPlayerAvailableOnTheMarket = isPlayerAvailableOnTheMarket(currentTransferMarketPrices);
//        if (!isPlayerAvailableOnTheMarket) {
//            throw new CanNotFindPlayerPriceToMonitor("Player is not available on the market");
//        }

        long possibleSellPrice = 0;
        if (currentSellPrices.getBuyNowPrice() != 0) {
            possibleSellPrice = currentSellPrices.getBuyNowPrice();
        } else if (currentTransferMarketPrices.getMaxBuyNowPrice() != 0) {
            possibleSellPrice = currentTransferMarketPrices.getMaxBuyNowPrice();
        } else {
            log.info("Player possible sell price is not present");
            log.info("Finding Max Buy Now price for player with Max Buy Now price: 0");
            var searchResult = transferMarketSteps.performOneSearch(FUTPriceHelper.createSearchPricesWithMaxBuyNowPrice(0));
            possibleSellPrice = AuctionInfoHelper.getLowestBinPriceFromAllAuctionInfo(searchResult);
            log.info("Found Max Buy Now price: " + possibleSellPrice);
        }

        var priceWithItemsOnOnePage = findPriceWhereItemsAreOnlyOnOnePage(possibleSellPrice);


        var newSellPrice = possibleSellPrice;

        List<AuctionInfoItem> previousSearchResult;
        List<AuctionInfoItem> currentSearchResult = new ArrayList<>();

        int noItemsFoundCounterWithNextFullSearchResult = 0;
        int noItemsFoundCounterWithNextLess20AndNotSnipe = 0;

        var priceToSellItem = priceWithItemsOnOnePage;

        var itemIsNotPopularOnTMQuantity = 0;

        //Find price to Sell item
        for (int i = 1; i <= 20; i++) {

            previousSearchResult = currentSearchResult;
            currentSearchResult = transferMarketSteps.performOneSearch(FUTPriceHelper.createSearchPricesWithMaxBuyNowPrice(priceToSellItem));

            if (currentSearchResult.size() < 20 && (!currentSearchResult.isEmpty() && AuctionInfoHelper.areItemsOkayToSnipe(currentSearchResult))) {
                log.info("Found price to sell item: " + priceToSellItem);
                break;
            }

            if (currentSearchResult.isEmpty()) {

            }

            if (currentSearchResult.size() < 20 && (!currentSearchResult.isEmpty() && !AuctionInfoHelper.areItemsOkayToSnipe(currentSearchResult))) {
                priceToSellItem = FUTPriceHelper.getReducedPrice(priceToSellItem);
                itemIsNotPopularOnTMQuantity++;
                if (itemIsNotPopularOnTMQuantity == 3) {
                    log.info("Found price to sell item: " + priceToSellItem);
                    break;
                }
                continue;
            }

            if (currentSearchResult.size() >= 20) {
                priceToSellItem = FUTPriceHelper.getReducedPrice(priceToSellItem);
                continue;
            }

            if (currentSearchResult.size() < 20 && AuctionInfoHelper.areBuyNowPriceForItemsLessThan(currentSearchResult, priceToSellItem)) {

            }


            //Price to sell decreased

            //Price to sell increased

        }


        for (int i = 1; i <= 20; i++) {
            log.info("Find price to sell player. Attempt: " + (i) + " out of 20");
            previousSearchResult = currentSearchResult;
            currentSearchResult = transferMarketSteps.performOneSearch(FUTPriceHelper.createSearchPricesWithMaxBuyNowPrice(newSellPrice));

            //current < 20, previous >= 20
            //current < 20, previous < 20
            //current > 20, previous > 20
            //current > 20, previous < 20


            if (currentSearchResult.isEmpty()) {

                if (previousSearchResult.isEmpty()) {
                    currentSearchResult = transferMarketSteps.performOneSearch(FUTPriceHelper.createSearchPricesWithMaxBuyNowPrice(0));
                    newSellPrice = AuctionInfoHelper.getLowestBinPriceFromAllAuctionInfo(currentSearchResult);
                    continue;
                }

                if (previousSearchResult.size() >= 20) {
                    noItemsFoundCounterWithNextFullSearchResult++;
                    if (noItemsFoundCounterWithNextFullSearchResult == 3) {
                        log.info("Found price to sell player: " + newSellPrice);
                        break;
                    }
                    newSellPrice = FUTPriceHelper.getIncreasedPrice(newSellPrice);
                    continue;
                }

                if (previousSearchResult.size() < 20 && !AuctionInfoHelper.areItemsOkayToSnipe(previousSearchResult)) {
                    noItemsFoundCounterWithNextLess20AndNotSnipe++;
                    if (noItemsFoundCounterWithNextLess20AndNotSnipe == 3) {
                        log.info("Found price to sell player: " + newSellPrice);
                        break;
                    }
                } else {
                    newSellPrice = FUTPriceHelper.getIncreasedPrice(newSellPrice);
                }
                continue;
            }

            if (currentSearchResult.size() < 13 && AuctionInfoHelper.areItemsOkayToSnipe(currentSearchResult)) {
                log.info("Current search result is less than 13 and all items are okay to snipe");
                break;
            }

            // current >= 20, previous > 20
            // current >= 20, previous < 20
            // previous <= 20 are okay to snipe
            // previous <= 20 are not okay to snipe

            if (currentSearchResult.size() >= 20 && (previousSearchResult.isEmpty() || previousSearchResult.size() <= 3)) {
                newSellPrice = FUTPriceHelper.getReducedPrice(newSellPrice);
                continue;
            }

            if (currentSearchResult.size() >= 20) {
                var optionalNewSellPrice = AuctionInfoHelper.getBuyNowPriceWhichIsNotPlacedRecently(currentSearchResult);
                if (optionalNewSellPrice.isPresent() && optionalNewSellPrice.get() < newSellPrice) {
                    newSellPrice = optionalNewSellPrice.get();
                } else {
                    newSellPrice = FUTPriceHelper.getReducedPrice(newSellPrice);
                }
                continue;
            }

            if (currentSearchResult.size() <= 17 && AuctionInfoHelper.areItemsOkayToSnipe(currentSearchResult)) {
                log.info("We found the price which potentially can be used to sell player: " + newSellPrice + " but lets check if the increased price is okay to sell");
                break;
            } else {
                newSellPrice = FUTPriceHelper.getReducedPrice(newSellPrice);
            }

            //Need to add if size <=17 and are not okay to snipe. We reduce, but until then ???

        }

        log.info("Checking if increased price is okay to sell");
        for (int i = 0; i <= 20; i++) {
            var newIncreasedSellPrice = FUTPriceHelper.getIncreasedPrice(newSellPrice);
            currentSearchResult = transferMarketSteps.performOneSearch(FUTPriceHelper.createSearchPricesWithMaxBuyNowPrice(newIncreasedSellPrice));
            if (currentSearchResult.size() < 20 && AuctionInfoHelper.areItemsOkayToSnipe(currentSearchResult)) {
                log.info("Next increased price is okay to sell: " + newIncreasedSellPrice);
                newSellPrice = newIncreasedSellPrice;
            } else {
                log.info("Next increased price is not okay to sell: " + newIncreasedSellPrice);
                log.info("Returning price to sell player: " + newSellPrice);
                return newSellPrice;
            }

            if (i == 19) {
                throw new CanNotFindPlayerPriceToSnipe("Price were increased 20 times, but still can not find price to sell player");
            }
        }

        //Check if increase price is okay to snipe


        //Search result is more than 20 or equal to 20
        //Search result is less than 20
        // Search result is 0-5
        // Search result is 6-10
        // Search result is 11-15
        // Search result is 16-19
        // Search result is 3, but previous search result is 16-20 -> return price where search result is 3
        // Search result is 3, but previous search result is 16-20 ->
        // Search result is more than 20, but next search result is 0, and with 3 attempts -> return price where search result is 0. This can be where min buy now price is set for player

        throw new

                CanNotFindPlayerPriceToSnipe("Can not find player price to sell");

    }


    private long findPriceWhereItemsAreOnlyOnOnePage(long possiblePrice) {

        List<AuctionInfoItem> previousSearchResult;
        List<AuctionInfoItem> currentSearchResult = new ArrayList<>();

        var moreThan20PlusItemsDuringSearchCounter = 0;

        boolean firstSearch = true;
        var sellPriceWhereItemsLessThan20 = possiblePrice;

        //Find a sell price where item quantity is less than 20.
        for (int i = 1; i <= 20; i++) {
            previousSearchResult = currentSearchResult;
            if (i > 1) {
                firstSearch = false;
            }

            if (i == 19) {
                throw new CanNotFindPlayerPriceToSnipe("Price were increased 20 times, but still can not find price to sell player");
            }
            currentSearchResult = transferMarketSteps.performOneSearch(FUTPriceHelper.createSearchPricesWithMaxBuyNowPrice(sellPriceWhereItemsLessThan20));

            if (currentSearchResult.size() >= 20) {

                if (previousSearchResult.size() < 20 && !firstSearch) {
                    //We take the previous search price
                    sellPriceWhereItemsLessThan20 = FUTPriceHelper.getReducedPrice(sellPriceWhereItemsLessThan20);
                    break;
                } else {
                    var buyNowPriceWhichIsNotPlacedRecently = AuctionInfoHelper.getBuyNowPriceWhichIsNotPlacedRecently(currentSearchResult);
                    if (buyNowPriceWhichIsNotPlacedRecently.isPresent() && buyNowPriceWhichIsNotPlacedRecently.get() < sellPriceWhereItemsLessThan20) {
                        sellPriceWhereItemsLessThan20 = buyNowPriceWhichIsNotPlacedRecently.get();
                    } else {
                        sellPriceWhereItemsLessThan20 = FUTPriceHelper.getReducedPrice(sellPriceWhereItemsLessThan20);
                    }
                    continue;
                }
            } else {

                if ((currentSearchResult.isEmpty() || AuctionInfoHelper.areBuyNowPriceForItemsLessThan(currentSearchResult, sellPriceWhereItemsLessThan20))
                        && !firstSearch
                        && (previousSearchResult.isEmpty() || AuctionInfoHelper.areBuyNowPriceForItemsLessThan(previousSearchResult, sellPriceWhereItemsLessThan20))) {
                    moreThan20PlusItemsDuringSearchCounter++;
                    if (moreThan20PlusItemsDuringSearchCounter == 5) {
                        currentSearchResult = transferMarketSteps.performOneSearch(FUTPriceHelper.createSearchPricesWithMaxBuyNowPrice(0));
                        sellPriceWhereItemsLessThan20 = AuctionInfoHelper.getLowestBinPriceFromAllAuctionInfo(currentSearchResult);
                        continue;
                    } else {
                        sellPriceWhereItemsLessThan20 = FUTPriceHelper.getIncreasedPrice(sellPriceWhereItemsLessThan20);
                        continue;
                    }
                }

                if (currentSearchResult.size() < 20 && previousSearchResult.size() >= 20) {
                    break;
                }

                if (currentSearchResult.size() < 20 && previousSearchResult.size() < 20) {
                    sellPriceWhereItemsLessThan20 = FUTPriceHelper.getIncreasedPrice(sellPriceWhereItemsLessThan20);
                    continue;
                }

                throw new CanNotFindPlayerPriceToSnipe("Can not find player price to sell");
            }
        }

        return sellPriceWhereItemsLessThan20;

    }


    private boolean isPlayerAvailableOnTheMarket(TransferMarketPrices currentTransferMarketPrices) {
        log.info(" ----- Checking if player available on the market ----- ");
        var updatePrices = currentTransferMarketPrices.copy();
        updatePrices.setMaxBuyNowPrice(15000000);

        for (int i = 0; i < 3; i++) {
            List<AuctionInfoItem> transferMarketSearchResult = transferMarketSteps.performOneSearch(updatePrices);
            if (!transferMarketSearchResult.isEmpty()) {
                return true;
            }
            updatePrices = FUTPriceHelper.updatePrices(updatePrices);
        }

        return false;
    }

    private boolean isItemOkayToSnipe(List<AuctionInfoItem> result) {
        return AuctionInfoHelper.areItemsOkayToSnipe(result);
        // if search result is more than 20 and all time is 59 minutes
        // if search result is less than 20, and all items with set price are 59 minutes

    }


}
