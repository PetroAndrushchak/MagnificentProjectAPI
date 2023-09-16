package com.petroandrushchak.fut.steps;

import com.google.common.collect.Lists;
import com.petroandrushchak.exceptions.fut.CanNotFindPlayerPriceToMonitor;
import com.petroandrushchak.fut.api.AuctionInfoItem;
import com.petroandrushchak.fut.api.TransfermarketSearchResponse;
import com.petroandrushchak.fut.api.helpers.AuctionInfoHelper;
import com.petroandrushchak.fut.helper.FUTPriceHelper;
import com.petroandrushchak.fut.model.statistic.PlayerStatisticItem;
import com.petroandrushchak.fut.model.statistic.TransferMarketAppearance;
import com.petroandrushchak.fut.pages.fut.FUTSearchResultPage;
import com.petroandrushchak.fut.pages.fut.FUTSearchTransferMarketPage;
import com.petroandrushchak.fut.steps.transfer.market.TransferMarketSteps;
import com.petroandrushchak.fut.model.TransferMarketPrices;
import com.petroandrushchak.model.fut.PlayerItem;
import com.petroandrushchak.service.DevToolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toCollection;

@Slf4j
@Component
public class FUTAnalyticsSteps {

    @Autowired TransferMarketSteps transferMarketSteps;
    @Autowired FUTSearchTransferMarketPage searchTransferMarketPage;
    @Autowired FUTSearchResultPage searchResultPage;
    @Autowired DevToolService devToolService;

    private final int TM_ATTEMPTS = 13;

    public TransferMarketAppearance performPlayerAppearanceAnalytic(PlayerStatisticItem playerSearch) {
        log.info("Performing player appearance analytic: " + playerSearch.toString());
        transferMarketSteps.setItemSearchAttributes(playerSearch);
        LocalTime startTime = LocalTime.now();

        List<AuctionInfoItem> firstCallAuctionData;
        List<AuctionInfoItem> allFoundAuctionInfo = Lists.newArrayList();

        // var lowestBinPrice = findLowestBinPriceForPlayer();
        long monitoringPrice = findPriceToMonitorPlayerAppearance(playerSearch);

        TransferMarketPrices searchPrices = FUTPriceHelper.createSearchPricesWithMaxBuyNowPrice(monitoringPrice);

        firstCallAuctionData = performOneSearch(searchPrices);

        log.info("Starting performing item appearance analytic");

        for (int i = 0; i < TM_ATTEMPTS; i++) {
            log.info("Attempt " + i + " out of " + TM_ATTEMPTS);
            searchPrices = FUTPriceHelper.updateMinBidAndMinBuySearchPrices(searchPrices);
            List<AuctionInfoItem> result = performOneSearch(searchPrices);
            allFoundAuctionInfo.addAll(result);
        }

        LocalTime finishTime = LocalTime.now();
        return createTransferMarketStatisticBaseOnSearchData(Duration.between(startTime, finishTime), monitoringPrice, firstCallAuctionData, allFoundAuctionInfo);
    }

    private long findPriceToMonitorPlayerAppearance(PlayerStatisticItem playerStatisticItem) throws CanNotFindPlayerPriceToMonitor {
        //Check if player available on the market
        var isPlayerAvailableOnTheMarket = isPlayerAvailableOnTheMarket(playerStatisticItem);
        if (!isPlayerAvailableOnTheMarket) {
            throw new CanNotFindPlayerPriceToMonitor("Player is not available on the market");
        }

        long searchMaxBuyNowPrice;
        if (playerStatisticItem.isPossibleSellPricePresent()) {
            searchMaxBuyNowPrice = playerStatisticItem.getPossibleSellPrice();
        } else {
            var searchResult = performOneSearch(FUTPriceHelper.createSearchPricesWithMaxBuyNowPrice(0));
            searchMaxBuyNowPrice = AuctionInfoHelper.getLowestBinPriceFromAllAuctionInfo(searchResult);
        }

        List<AuctionInfoItem> previousSearchResult = new ArrayList<>();
        List<AuctionInfoItem> currentSearchResult = new ArrayList<>();
        boolean significantPriceIncrease = false;
        boolean significantPriceDecrease = false;
        boolean previousPriceChangeWasSignificant = false;

        for (int i = 0; i < 20; i++) {
            previousSearchResult = currentSearchResult;
            currentSearchResult = performOneSearch(FUTPriceHelper.createSearchPricesWithMaxBuyNowPrice(searchMaxBuyNowPrice));
            //Case price X 16-20, price X-price_step <10
            if (currentSearchResult.isEmpty()) {
                if (previousSearchResult.isEmpty() && significantPriceIncrease) {
                    currentSearchResult = performOneSearch(FUTPriceHelper.createSearchPricesWithMaxBuyNowPrice(0));
                    searchMaxBuyNowPrice = AuctionInfoHelper.getLowestBinPriceFromAllAuctionInfo(currentSearchResult);
                    continue;
                }
                if (previousSearchResult.isEmpty() && !significantPriceDecrease) {
                    searchMaxBuyNowPrice = FUTPriceHelper.getIncreasedSignificantlyPrice(searchMaxBuyNowPrice);
                    previousPriceChangeWasSignificant = true;
                    significantPriceIncrease = true;
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
                var oneSearch = performOneSearch(FUTPriceHelper.createSearchPricesWithMaxBuyNowPrice(searchMaxBuyNowPrice));
                var oneMoreSearch = performOneSearch(FUTPriceHelper.createSearchPricesWithMaxBuyNowPrice(searchMaxBuyNowPrice));

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

        throw new CanNotFindPlayerPriceToMonitor("Can not find player price to monitor");
    }

    private boolean isPlayerAvailableOnTheMarket(PlayerStatisticItem playerStatisticItem) {
        log.info(" ----- Checking if player available on the market ----- ");
        var searchPrices = FUTPriceHelper.createSearchPrices();
        searchPrices.setMaxBuyNowPrice(0);

        for (int i = 0; i < 3; i++) {
            List<AuctionInfoItem> transferMarketSearchResult = performOneSearch(searchPrices);
            if (!transferMarketSearchResult.isEmpty()) {
                return true;
            }
            searchPrices = FUTPriceHelper.updateMinBidAndMinBuySearchPrices(searchPrices);
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

    public long findLowestBinPriceForPlayer() {
        log.info(" ----- Finding lowest bin price for player  ----- ");
        List<AuctionInfoItem> auctionInfos = findAuctionInfoTillNoItemsFound();
        long lowestBinPrice = AuctionInfoHelper.getLowestBinPriceFromAllAuctionInfo(auctionInfos);
        return lowestBinPrice;
    }

    public long findPriceToMonitorPlayerAppearance(long potentialPriceToMonitor) {
        log.info(" ----- Finding price to monitor player appearance ----- ");

        long minBuyNowPrice = potentialPriceToMonitor;

        TransferMarketPrices searchPrices = FUTPriceHelper.createSearchPricesWithMaxBuyNowPrice(minBuyNowPrice);
        List<AuctionInfoItem> transferMarketSearchResult = performOneSearch(searchPrices);

        Function<Long, Long> actionTakenOnPrice;
        Predicate<List<AuctionInfoItem>> searchUntil;
        if (AuctionInfoHelper.isPlayersNumberEqualToMonitoringNumber(transferMarketSearchResult)) {
            return potentialPriceToMonitor;
        } else if (AuctionInfoHelper.isPlayersNumberLessThanMonitoringNumber(transferMarketSearchResult)) {
            actionTakenOnPrice = FUTPriceHelper::getIncreasedPrice;
            searchUntil = AuctionInfoHelper::isPlayersNumberMoreThanMonitoringNumber;
        } else {
            actionTakenOnPrice = FUTPriceHelper::getReducedPrice;
            searchUntil = AuctionInfoHelper::isPlayersNumberLessThanMonitoringNumber;
        }

        for (int i = 0; i < 20; i++) {
            minBuyNowPrice = actionTakenOnPrice.apply(minBuyNowPrice);
            searchPrices.setMaxBuyNowPrice(minBuyNowPrice);
            List<AuctionInfoItem> result = performOneSearch(searchPrices);

            if (searchUntil.test(result)) {
                break;
            } else {
                potentialPriceToMonitor = minBuyNowPrice;
            }
        }

        log.info(" ----- Price to monitor player found:  " + potentialPriceToMonitor + "   ----- ");

        return potentialPriceToMonitor;
    }

    private List<AuctionInfoItem> findAuctionInfoTillNoItemsFound() {
        var searchPrices = FUTPriceHelper.createSearchPrices();

        List<AuctionInfoItem> allFoundAuctionInfo = new ArrayList<>();

        for (int i = 0; i < 20; i++) {

            List<AuctionInfoItem> transferMarketSearchResult = performOneSearch(searchPrices);
            allFoundAuctionInfo.addAll(transferMarketSearchResult);

            if (AuctionInfoHelper.isPlayersNumbersLessThanMaxSearch(transferMarketSearchResult)) {
                break;
            }

            long minBuyNowPrice = AuctionInfoHelper.getLowestBinPriceFromAllAuctionInfo(transferMarketSearchResult);
            if (minBuyNowPrice == searchPrices.getMaxBuyNowPrice()) {
                minBuyNowPrice = FUTPriceHelper.getReducedPrice(minBuyNowPrice);
            }
            searchPrices.setMaxBuyNowPrice(minBuyNowPrice);
        }

        return allFoundAuctionInfo;
    }

    private List<AuctionInfoItem> performOneSearch(TransferMarketPrices searchPrices) {
        log.info("Performing search, with prices: Min bid: " + searchPrices.getMinBidPrice() +
                ", Max bin: " + searchPrices.getMaxBidPrice() +
                ", Min BuyNow: " + searchPrices.getMinBuyNowPrice() +
                ", Max BuyNow: " + searchPrices.getMaxBuyNowPrice());
        searchTransferMarketPage.setSearchPrices(searchPrices);
        var response = devToolService.getResponseFromTheRequestWithAction("/ut/game/fifa23/transfermarket",
                () -> searchTransferMarketPage.clickSearchButton(),
                TransfermarketSearchResponse.class);

        List<AuctionInfoItem> transferMarketSearchResult = response.getAuctionInfo();
        AuctionInfoHelper.logAuctionInfoData(transferMarketSearchResult);
        searchResultPage.clickBackToSearchPage();
        return transferMarketSearchResult;
    }
}
