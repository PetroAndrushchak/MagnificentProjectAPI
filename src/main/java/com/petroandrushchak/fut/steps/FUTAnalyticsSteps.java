package com.petroandrushchak.fut.steps;

import com.google.common.collect.Lists;
import com.petroandrushchak.exceptions.fut.CanNotFindPlayerPriceToMonitor;
import com.petroandrushchak.fut.api.AuctionInfoItem;
import com.petroandrushchak.fut.api.helpers.AuctionInfoHelper;
import com.petroandrushchak.fut.helper.FUTPriceHelper;
import com.petroandrushchak.fut.model.TransferMarketPrices;
import com.petroandrushchak.fut.model.statistic.PlayerStatisticItem;
import com.petroandrushchak.fut.model.statistic.TransferMarketAppearance;
import com.petroandrushchak.fut.pages.fut.FUTSearchResultPage;
import com.petroandrushchak.fut.pages.fut.FUTSearchTransferMarketPage;
import com.petroandrushchak.fut.steps.transfer.market.TransferMarketSteps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toCollection;

@Slf4j
@Component
public class FUTAnalyticsSteps {

    @Autowired TransferMarketSteps transferMarketSteps;
    @Autowired FUTSearchTransferMarketPage searchTransferMarketPage;
    @Autowired FUTSearchResultPage searchResultPage;

    private final int TM_ATTEMPTS = 13;

    public TransferMarketAppearance performPlayerAppearanceAnalytic(PlayerStatisticItem playerSearch) {
        log.info("Performing player appearance analytic: " + playerSearch.toString());
        transferMarketSteps.setItemSearchAttributes(playerSearch);
        LocalTime startTime = LocalTime.now();

        List<AuctionInfoItem> firstCallAuctionData;
        List<AuctionInfoItem> allFoundAuctionInfo = Lists.newArrayList();

        long monitoringPrice = findPriceToMonitorPlayerAppearance(playerSearch);

        TransferMarketPrices searchPrices = FUTPriceHelper.createSearchPricesWithMaxBuyNowPrice(monitoringPrice);

        firstCallAuctionData = transferMarketSteps.performOneSearch(searchPrices);

        log.info("Starting performing item appearance analytic");

        for (int i = 1; i <= TM_ATTEMPTS; i++) {
            log.info("Attempt " + i + " out of " + TM_ATTEMPTS);
            searchPrices = FUTPriceHelper.updatePrices(searchPrices);
            List<AuctionInfoItem> result = transferMarketSteps.performOneSearch(searchPrices);
            allFoundAuctionInfo.addAll(result);
        }

        LocalTime finishTime = LocalTime.now();
        return createTransferMarketStatisticBaseOnSearchData(Duration.between(startTime, finishTime), monitoringPrice, firstCallAuctionData, allFoundAuctionInfo);
    }

    public long findPriceToMonitorPlayerAppearance(PlayerStatisticItem playerStatisticItem) throws CanNotFindPlayerPriceToMonitor {
        //Check if player available on the market
        var isPlayerAvailableOnTheMarket = isPlayerAvailableOnTheMarket(playerStatisticItem);
        if (!isPlayerAvailableOnTheMarket) {
            throw new CanNotFindPlayerPriceToMonitor("Player is not available on the market");
        }

        long searchMaxBuyNowPrice;
        if (playerStatisticItem.isPossibleSellPricePresent()) {
            log.info("Player possible sell price is present: " + playerStatisticItem.getPossibleSellPrice());
            log.info("Checking if players are present on TM with price: " + playerStatisticItem.getPossibleSellPrice());
            var searchResult = transferMarketSteps.performOneSearch(FUTPriceHelper.createSearchPricesWithMaxBuyNowPrice(playerStatisticItem.getPossibleSellPrice()));
            if (searchResult.isEmpty()) {
                log.info("Players are not present on TM with price: " + playerStatisticItem.getPossibleSellPrice());
                log.info("Finding Max Buy Now price for player with Max Buy Now price: 0");
                searchResult = transferMarketSteps.performOneSearch(FUTPriceHelper.createSearchPricesWithMaxBuyNowPrice(0));
                searchMaxBuyNowPrice = AuctionInfoHelper.getLowestBinPriceFromAllAuctionInfo(searchResult);
                log.info("Found Max Buy Now price: " + searchMaxBuyNowPrice);
            } else {
                log.info("Players are present on TM with price: " + playerStatisticItem.getPossibleSellPrice());
                searchMaxBuyNowPrice = playerStatisticItem.getPossibleSellPrice();
            }
        } else {
            log.info("Player possible sell price is not present");
            log.info("Finding Max Buy Now price for player with Max Buy Now price: 0");
            var searchResult = transferMarketSteps.performOneSearch(FUTPriceHelper.createSearchPricesWithMaxBuyNowPrice(0));
            searchMaxBuyNowPrice = AuctionInfoHelper.getLowestBinPriceFromAllAuctionInfo(searchResult);
            log.info("Found Max Buy Now price: " + searchMaxBuyNowPrice);
        }

        List<AuctionInfoItem> previousSearchResult;
        List<AuctionInfoItem> currentSearchResult = new ArrayList<>();
        boolean significantPriceIncrease = false;
        boolean significantPriceDecrease = false;
        boolean previousPriceChangeWasSignificant = false;

        int noItemsFoundCounterWithNextFullSearchResult = 0;

        for (int i = 1; i <= 20; i++) {
            log.info("Find price to monitor player appearance. Attempt: " + (i) + " out of 20");
            previousSearchResult = currentSearchResult;
            currentSearchResult = transferMarketSteps.performOneSearch(FUTPriceHelper.createSearchPricesWithMaxBuyNowPrice(searchMaxBuyNowPrice));

            if (currentSearchResult.isEmpty()) {
                if (previousSearchResult.isEmpty() && significantPriceIncrease) {
                    currentSearchResult = transferMarketSteps.performOneSearch(FUTPriceHelper.createSearchPricesWithMaxBuyNowPrice(0));
                    searchMaxBuyNowPrice = AuctionInfoHelper.getLowestBinPriceFromAllAuctionInfo(currentSearchResult);
                    continue;
                }
                if (previousSearchResult.isEmpty() && !significantPriceDecrease) {
                    searchMaxBuyNowPrice = FUTPriceHelper.getIncreasedSignificantlyPrice(searchMaxBuyNowPrice);
                    previousPriceChangeWasSignificant = true;
                    significantPriceIncrease = true;
                }

                if (previousSearchResult.size() >= 20) {
                    noItemsFoundCounterWithNextFullSearchResult++;
                    if (noItemsFoundCounterWithNextFullSearchResult == 3) {
                        return searchMaxBuyNowPrice;
                    }
                    searchMaxBuyNowPrice = FUTPriceHelper.getIncreasedPrice(searchMaxBuyNowPrice);
                } else {
                    searchMaxBuyNowPrice = FUTPriceHelper.getIncreasedPrice(searchMaxBuyNowPrice);
                    previousPriceChangeWasSignificant = false;
                }
                continue;
            }

            if (currentSearchResult.size() >= 20) {
                if (previousSearchResult.size() >= 20 && !significantPriceIncrease) {
                    searchMaxBuyNowPrice = FUTPriceHelper.getReducedSignificantlyPrice(searchMaxBuyNowPrice);
                    significantPriceDecrease = true;
                    previousPriceChangeWasSignificant = true;
                } else {
                    searchMaxBuyNowPrice = FUTPriceHelper.getReducedPrice(searchMaxBuyNowPrice);
                    previousPriceChangeWasSignificant = false;
                }
                continue;
            }

            if (currentSearchResult.size() < 20 && currentSearchResult.size() >= 15) {
                searchMaxBuyNowPrice = FUTPriceHelper.getReducedPrice(searchMaxBuyNowPrice);
                previousPriceChangeWasSignificant = false;
                continue;
            }

            if (currentSearchResult.size() < 10) {
                if (previousSearchResult.size() >= 15 && !previousPriceChangeWasSignificant) {
                    return searchMaxBuyNowPrice;
                }
                searchMaxBuyNowPrice = FUTPriceHelper.getIncreasedPrice(searchMaxBuyNowPrice);
                previousPriceChangeWasSignificant = false;
                continue;
            }

            if (currentSearchResult.size() >= 11 && currentSearchResult.size() < 15) {
                var oneSearch = transferMarketSteps.performOneSearch(FUTPriceHelper.createSearchPricesWithMaxBuyNowPrice(searchMaxBuyNowPrice));
                var oneMoreSearch = transferMarketSteps.performOneSearch(FUTPriceHelper.createSearchPricesWithMaxBuyNowPrice(searchMaxBuyNowPrice));

                if ((oneSearch.size() >= 11 && oneSearch.size() < 15) && (oneMoreSearch.size() >= 11 && oneMoreSearch.size() < 15)) {
                    return searchMaxBuyNowPrice;
                } else {
                    continue;
                }
            }
        }

        //Search result is more than 20 or equal to 20
        //Search result is less than 20
        // Search result is 0-5
        // Search result is 6-10
        // Search result is 11-15
        // Search result is 16-19
        // Search result is 3, but previous search result is 16-20 -> return price where search result is 3
        // Search result is 3, but previous search result is 16-20 ->
        // Search result is more than 20, but next search result is 0, and with 3 attempts -> return price where search result is 0. This can be where min buy now price is set for player

        throw new CanNotFindPlayerPriceToMonitor("Can not find player price to monitor");
    }

    private boolean isPlayerAvailableOnTheMarket(PlayerStatisticItem playerStatisticItem) {
        log.info(" ----- Checking if player available on the market ----- ");
        var searchPrices = FUTPriceHelper.createSearchPrices();
        searchPrices.setMaxBuyNowPrice(0);

        for (int i = 0; i < 3; i++) {
            List<AuctionInfoItem> transferMarketSearchResult = transferMarketSteps.performOneSearch(searchPrices);
            if (!transferMarketSearchResult.isEmpty()) {
                return true;
            }
            searchPrices = FUTPriceHelper.updatePrices(searchPrices);
        }

        return false;
    }

    public TransferMarketAppearance createTransferMarketStatisticBaseOnSearchData(Duration duration, long monitoringPrice, List<AuctionInfoItem> firstCallAuctionData, List<AuctionInfoItem> allFoundAuctionInfo) {
        List<AuctionInfoItem> uniqueFoundAuctionData = allFoundAuctionInfo.stream()
                                                                          .collect(Collectors.collectingAndThen(toCollection(() -> new TreeSet<>(Comparator.comparingLong(AuctionInfoItem::getTradeId))), ArrayList::new));

        List<AuctionInfoItem> appearedAuctionData = AuctionInfoHelper.subtractBaseOnTradeId(uniqueFoundAuctionData, firstCallAuctionData);

        List<Integer> appearedBuyNowPrices = appearedAuctionData.stream().map(AuctionInfoItem::getBuyNowPrice).sorted().collect(Collectors.toList());

        return TransferMarketAppearance.builder()
                                       .duration(duration)
                                       .monitoringPrice(monitoringPrice)
                                       .quantity(appearedAuctionData.size())
                                       .appearedBuyNowPrices(appearedBuyNowPrices)
                                       .build();
    }
    
}
