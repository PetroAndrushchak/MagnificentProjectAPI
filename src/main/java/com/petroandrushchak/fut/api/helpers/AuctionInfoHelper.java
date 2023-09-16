package com.petroandrushchak.fut.api.helpers;

import com.petroandrushchak.fut.api.AuctionInfoItem;
import com.petroandrushchak.fut.api.TransfermarketSearchResponse;
import com.petroandrushchak.fut.helper.FUTPriceHelper;
import com.petroandrushchak.helper.JsonParser;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@UtilityClass
@Slf4j
public class AuctionInfoHelper {

    private final int ITEMS_ON_SEARCH_RESULT_PAGE = 17;
    private final int ITEMS_ON_SEARCH_RESULT_PAGE_OKAY_TO_MONITOR = ITEMS_ON_SEARCH_RESULT_PAGE - 3;
    private final int ITEM_NUMBER_ON_TM_OKAY_TO_SNIPE = 6;
    private final int ITEM_NUMBER_MAX_EXPIRE_TIME_OKAY_TO_SELL = 5;

    public static boolean isPlayersNumberEqualToMonitoringNumber(List<AuctionInfoItem> auctionInfos) {
        return auctionInfos.size() == ITEMS_ON_SEARCH_RESULT_PAGE_OKAY_TO_MONITOR;
    }

    public static boolean isPlayersNumberLessThanMonitoringNumber(List<AuctionInfoItem> auctionInfos) {
        return auctionInfos.size() < ITEMS_ON_SEARCH_RESULT_PAGE_OKAY_TO_MONITOR;
    }

    public static boolean isPlayersNumberMoreThanMonitoringNumber(List<AuctionInfoItem> auctionInfos) {
        return !isPlayersNumberLessThanMonitoringNumber(auctionInfos);
    }

    public static boolean isPlayersNumberMoreThan(int number, List<AuctionInfoItem> auctionInfos) {
        return auctionInfos.size() > number;
    }


    public static boolean isPlayersNumbersLessThanMaxSearch(List<AuctionInfoItem> auctionInfos) {
        boolean result =  auctionInfos.size() <= ITEMS_ON_SEARCH_RESULT_PAGE;
        if(result){
            log.info("Players number is less than Max Search: " + auctionInfos.size() + " allowed: " + ITEMS_ON_SEARCH_RESULT_PAGE);
        }
        return result;
    }

    public static long calculateSellPrice(List<AuctionInfoItem> auctionInfos) {
        long minBuyNowPriceOnTheMarket = getLowestBinPriceFromAllAuctionInfo(auctionInfos);
        return FUTPriceHelper.getReducedPrice(minBuyNowPriceOnTheMarket);
    }

    public static long getLowestBinPriceFromAllAuctionInfo(List<AuctionInfoItem> auctionInfos) {

        log.info("Trying to get lowest min buy now price from the list of provided auction info ");
        logAuctionInfoData(auctionInfos);

        List<AuctionInfoItem> notExpiredTimeMaxAuctionInfo = auctionInfos.stream()
            .filter(auctionInfo -> isNotExpireTimeMax(auctionInfo.getExpires()))
            .collect(Collectors.toList());

        Optional<Integer> optionalLowestBinPrice;

        if (notExpiredTimeMaxAuctionInfo.isEmpty()) {

            //Grouping prices with 59 minutes expire time
            Map<Integer, Long> groupedPrices = auctionInfos.stream()
                .filter(auctionInfo -> !isNotExpireTimeMax(auctionInfo.getExpires()))
                .collect(Collectors.groupingBy(AuctionInfoItem::getBuyNowPrice, Collectors.counting()));

            Map<Integer, Long> sortedByPrice = groupedPrices.entrySet().stream()
                .sorted(Entry.comparingByKey(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

            optionalLowestBinPrice = sortedByPrice.entrySet().stream()
                .filter((entry) -> entry.getValue() < ITEM_NUMBER_MAX_EXPIRE_TIME_OKAY_TO_SELL)
                .map(Entry::getKey)
                .findFirst();
        } else {
            optionalLowestBinPrice = auctionInfos.stream()
                .filter(auctionInfo -> isNotExpireTimeMax(auctionInfo.getExpires()))
                .map(AuctionInfoItem::getBuyNowPrice)
                .min(Comparator.comparing(Long::valueOf));
        }

        if (optionalLowestBinPrice.isPresent()) {
            log.info("Lowest bin price: " + optionalLowestBinPrice.get());
            return optionalLowestBinPrice.get();
        } else {
            throw new RuntimeException("Can not find min buy now price");
        }
    }

    public static boolean isAuctionInfoWithMaxOnMarketTime(List<AuctionInfoItem> auctionInfos) {
        log.info("Trying to check if the auction info has max expire time");
        logAuctionInfoData(auctionInfos);
        boolean allMatchExpireTime = auctionInfos.stream().allMatch(auctionInfo -> isExpireTimeMax(auctionInfo.getExpires()));
        log.info("All match expire time " + allMatchExpireTime);
        boolean isNumberOfItemsOkayToSnipe = auctionInfos.size() <= ITEM_NUMBER_ON_TM_OKAY_TO_SNIPE;
        return allMatchExpireTime && isNumberOfItemsOkayToSnipe;
    }

    public static List<AuctionInfoItem> subtractBaseOnTradeId(List<AuctionInfoItem> x, List<AuctionInfoItem> y) {
        return x.stream()
                .filter(auctionInfo -> y.stream().noneMatch(auctionInfo1 -> auctionInfo1.getTradeId() == auctionInfo.getTradeId()))
                .collect(Collectors.toList());
    }

    private static boolean isExpireTimeMax(long expireTime) {
        return expireTime >= 3420;
    }

    private static boolean isNotExpireTimeMax(long expireTime) {
        return !isExpireTimeMax(expireTime);
    }

    public static void logAuctionInfoData(List<AuctionInfoItem> auctionInfos) {
        if (auctionInfos == null || auctionInfos.isEmpty()) {
            log.info(" --- Auction info is empty --- ");
        } else {
            System.out.println("Auction data: \n Buy now prices  Expire time :");
            auctionInfos.forEach(auctionInfo -> {
                System.out.println("       " + auctionInfo.getBuyNowPrice() + "  " + auctionInfo.getExpires() / 60 + " min");
            });
        }
    }

    public static void main(String[] args) {
        String response = "{ \"auctionInfo\": [ { \"tradeId\": 507816665773, \"itemData\": { \"id\": 149357107069, \"timestamp\": 1694272128, \"formation\": \"f3412\", \"untradeable\": false, \"assetId\": 183277, \"rating\": 84, \"itemType\": \"player\", \"resourceId\": 183277, \"owners\": 4, \"discardValue\": 1000, \"itemState\": \"forSale\", \"cardsubtypeid\": 3, \"lastSalePrice\": 8000, \"injuryType\": \"none\", \"injuryGames\": 0, \"preferredPosition\": \"LW\", \"contract\": 7, \"teamid\": 243, \"rareflag\": 1, \"playStyle\": 250, \"leagueId\": 53, \"assists\": 0, \"lifetimeAssists\": 0, \"loyaltyBonus\": 0, \"pile\": 5, \"nation\": 7, \"resourceGameYear\": 2023, \"guidAssetId\": \"c295ffd0-2695-40e9-9d70-b806e4679e5d\", \"groups\": [ 4 ], \"attributeArray\": [ 83, 80, 82, 87, 35, 63 ], \"statsArray\": [ 0, 0, 0, 0, 0 ], \"lifetimeStatsArray\": [ 0, 0, 0, 0, 0 ], \"skillmoves\": 3, \"weakfootabilitytypecode\": 4, \"attackingworkrate\": 0, \"defensiveworkrate\": 0, \"preferredfoot\": 1, \"possiblePositions\": [ \"LM\", \"LW\" ] }, \"tradeState\": \"active\", \"buyNowPrice\": 5600, \"currentBid\": 0, \"offers\": 0, \"watched\": false, \"bidState\": \"none\", \"startingBid\": 1100, \"confidenceValue\": 100, \"expires\": 3473, \"sellerName\": \"FIFA UT\", \"sellerEstablished\": 0, \"sellerId\": 0, \"tradeOwner\": false, \"tradeIdStr\": \"507816665773\" }, { \"tradeId\": 507807118406, \"itemData\": { \"id\": 157259746126, \"timestamp\": 1694244066, \"formation\": \"f3412\", \"untradeable\": false, \"assetId\": 183277, \"rating\": 84, \"itemType\": \"player\", \"resourceId\": 183277, \"owners\": 2, \"discardValue\": 1000, \"itemState\": \"forSale\", \"cardsubtypeid\": 3, \"lastSalePrice\": 1600, \"injuryType\": \"none\", \"injuryGames\": 0, \"preferredPosition\": \"LW\", \"contract\": 7, \"teamid\": 243, \"rareflag\": 1, \"playStyle\": 250, \"leagueId\": 53, \"assists\": 0, \"lifetimeAssists\": 0, \"loyaltyBonus\": 0, \"pile\": 5, \"nation\": 7, \"resourceGameYear\": 2023, \"guidAssetId\": \"c295ffd0-2695-40e9-9d70-b806e4679e5d\", \"groups\": [ 4 ], \"attributeArray\": [ 83, 80, 82, 87, 35, 63 ], \"statsArray\": [ 0, 0, 0, 0, 0 ], \"lifetimeStatsArray\": [ 0, 0, 0, 0, 0 ], \"skillmoves\": 3, \"weakfootabilitytypecode\": 4, \"attackingworkrate\": 0, \"defensiveworkrate\": 0, \"preferredfoot\": 1, \"possiblePositions\": [ \"LM\", \"LW\" ] }, \"tradeState\": \"active\", \"buyNowPrice\": 5800, \"currentBid\": 0, \"offers\": 0, \"watched\": false, \"bidState\": \"none\", \"startingBid\": 5700, \"confidenceValue\": 100, \"expires\": 58226, \"sellerName\": \"FIFA UT\", \"sellerEstablished\": 0, \"sellerId\": 0, \"tradeOwner\": false, \"tradeIdStr\": \"507807118406\" }, { \"tradeId\": 507807646899, \"itemData\": { \"id\": 151942849427, \"timestamp\": 1694245920, \"formation\": \"f3412\", \"untradeable\": false, \"assetId\": 183277, \"rating\": 84, \"itemType\": \"player\", \"resourceId\": 183277, \"owners\": 2, \"discardValue\": 1000, \"itemState\": \"forSale\", \"cardsubtypeid\": 3, \"lastSalePrice\": 3000, \"injuryType\": \"none\", \"injuryGames\": 0, \"preferredPosition\": \"LW\", \"contract\": 7, \"teamid\": 243, \"rareflag\": 1, \"playStyle\": 250, \"leagueId\": 53, \"assists\": 0, \"lifetimeAssists\": 0, \"loyaltyBonus\": 0, \"pile\": 5, \"nation\": 7, \"resourceGameYear\": 2023, \"guidAssetId\": \"c295ffd0-2695-40e9-9d70-b806e4679e5d\", \"groups\": [ 4 ], \"attributeArray\": [ 83, 80, 82, 87, 35, 63 ], \"statsArray\": [ 0, 0, 0, 0, 0 ], \"lifetimeStatsArray\": [ 0, 0, 0, 0, 0 ], \"skillmoves\": 3, \"weakfootabilitytypecode\": 4, \"attackingworkrate\": 0, \"defensiveworkrate\": 0, \"preferredfoot\": 1, \"possiblePositions\": [ \"LM\", \"LW\" ] }, \"tradeState\": \"active\", \"buyNowPrice\": 5800, \"currentBid\": 0, \"offers\": 0, \"watched\": false, \"bidState\": \"none\", \"startingBid\": 5700, \"confidenceValue\": 100, \"expires\": 60083, \"sellerName\": \"FIFA UT\", \"sellerEstablished\": 0, \"sellerId\": 0, \"tradeOwner\": false, \"tradeIdStr\": \"507807646899\" }, { \"tradeId\": 507807708711, \"itemData\": { \"id\": 158291240220, \"timestamp\": 1694246205, \"formation\": \"f3412\", \"untradeable\": false, \"assetId\": 183277, \"rating\": 84, \"itemType\": \"player\", \"resourceId\": 183277, \"owners\": 2, \"discardValue\": 1000, \"itemState\": \"forSale\", \"cardsubtypeid\": 3, \"lastSalePrice\": 4200, \"injuryType\": \"none\", \"injuryGames\": 0, \"preferredPosition\": \"LW\", \"contract\": 7, \"teamid\": 243, \"rareflag\": 1, \"playStyle\": 250, \"leagueId\": 53, \"assists\": 0, \"lifetimeAssists\": 0, \"loyaltyBonus\": 0, \"pile\": 5, \"nation\": 7, \"resourceGameYear\": 2023, \"guidAssetId\": \"c295ffd0-2695-40e9-9d70-b806e4679e5d\", \"groups\": [ 4 ], \"attributeArray\": [ 83, 80, 82, 87, 35, 63 ], \"statsArray\": [ 0, 0, 0, 0, 0 ], \"lifetimeStatsArray\": [ 0, 0, 0, 0, 0 ], \"skillmoves\": 3, \"weakfootabilitytypecode\": 4, \"attackingworkrate\": 0, \"defensiveworkrate\": 0, \"preferredfoot\": 1, \"possiblePositions\": [ \"LM\", \"LW\" ] }, \"tradeState\": \"active\", \"buyNowPrice\": 5800, \"currentBid\": 0, \"offers\": 0, \"watched\": false, \"bidState\": \"none\", \"startingBid\": 5700, \"confidenceValue\": 100, \"expires\": 60364, \"sellerName\": \"FIFA UT\", \"sellerEstablished\": 0, \"sellerId\": 0, \"tradeOwner\": false, \"tradeIdStr\": \"507807708711\" }, { \"tradeId\": 507810970049, \"itemData\": { \"id\": 139610368460, \"timestamp\": 1694256187, \"formation\": \"f3412\", \"untradeable\": false, \"assetId\": 183277, \"rating\": 84, \"itemType\": \"player\", \"resourceId\": 183277, \"owners\": 3, \"discardValue\": 1000, \"itemState\": \"forSale\", \"cardsubtypeid\": 3, \"lastSalePrice\": 5400, \"injuryType\": \"none\", \"injuryGames\": 0, \"preferredPosition\": \"LW\", \"contract\": 7, \"teamid\": 243, \"rareflag\": 1, \"playStyle\": 250, \"leagueId\": 53, \"assists\": 0, \"lifetimeAssists\": 0, \"loyaltyBonus\": 0, \"pile\": 5, \"nation\": 7, \"resourceGameYear\": 2023, \"guidAssetId\": \"c295ffd0-2695-40e9-9d70-b806e4679e5d\", \"groups\": [ 4 ], \"attributeArray\": [ 83, 80, 82, 87, 35, 63 ], \"statsArray\": [ 0, 0, 0, 0, 0 ], \"lifetimeStatsArray\": [ 0, 0, 0, 0, 0 ], \"skillmoves\": 3, \"weakfootabilitytypecode\": 4, \"attackingworkrate\": 0, \"defensiveworkrate\": 0, \"preferredfoot\": 1, \"possiblePositions\": [ \"LM\", \"LW\" ] }, \"tradeState\": \"active\", \"buyNowPrice\": 5700, \"currentBid\": 0, \"offers\": 0, \"watched\": false, \"bidState\": \"none\", \"startingBid\": 5600, \"confidenceValue\": 100, \"expires\": 70352, \"sellerName\": \"FIFA UT\", \"sellerEstablished\": 0, \"sellerId\": 0, \"tradeOwner\": false, \"tradeIdStr\": \"507810970049\" }, { \"tradeId\": 507811040279, \"itemData\": { \"id\": 156467559036, \"timestamp\": 1694256392, \"formation\": \"f3412\", \"untradeable\": false, \"assetId\": 183277, \"rating\": 84, \"itemType\": \"player\", \"resourceId\": 183277, \"owners\": 2, \"discardValue\": 1000, \"itemState\": \"forSale\", \"cardsubtypeid\": 3, \"lastSalePrice\": 2500, \"injuryType\": \"none\", \"injuryGames\": 0, \"preferredPosition\": \"LW\", \"contract\": 7, \"teamid\": 243, \"rareflag\": 1, \"playStyle\": 250, \"leagueId\": 53, \"assists\": 0, \"lifetimeAssists\": 0, \"loyaltyBonus\": 0, \"pile\": 5, \"nation\": 7, \"resourceGameYear\": 2023, \"guidAssetId\": \"c295ffd0-2695-40e9-9d70-b806e4679e5d\", \"groups\": [ 4 ], \"attributeArray\": [ 83, 80, 82, 87, 35, 63 ], \"statsArray\": [ 0, 0, 0, 0, 0 ], \"lifetimeStatsArray\": [ 0, 0, 0, 0, 0 ], \"skillmoves\": 3, \"weakfootabilitytypecode\": 4, \"attackingworkrate\": 0, \"defensiveworkrate\": 0, \"preferredfoot\": 1, \"possiblePositions\": [ \"LM\", \"LW\" ] }, \"tradeState\": \"active\", \"buyNowPrice\": 5700, \"currentBid\": 0, \"offers\": 0, \"watched\": false, \"bidState\": \"none\", \"startingBid\": 5600, \"confidenceValue\": 100, \"expires\": 70556, \"sellerName\": \"FIFA UT\", \"sellerEstablished\": 0, \"sellerId\": 0, \"tradeOwner\": false, \"tradeIdStr\": \"507811040279\" }, { \"tradeId\": 507812209327, \"itemData\": { \"id\": 155291669784, \"timestamp\": 1694259819, \"formation\": \"f3412\", \"untradeable\": false, \"assetId\": 183277, \"rating\": 84, \"itemType\": \"player\", \"resourceId\": 183277, \"owners\": 2, \"discardValue\": 1000, \"itemState\": \"forSale\", \"cardsubtypeid\": 3, \"lastSalePrice\": 1600, \"injuryType\": \"none\", \"injuryGames\": 0, \"preferredPosition\": \"LW\", \"contract\": 7, \"teamid\": 243, \"rareflag\": 1, \"playStyle\": 250, \"leagueId\": 53, \"assists\": 0, \"lifetimeAssists\": 0, \"loyaltyBonus\": 0, \"pile\": 5, \"nation\": 7, \"resourceGameYear\": 2023, \"guidAssetId\": \"c295ffd0-2695-40e9-9d70-b806e4679e5d\", \"groups\": [ 4 ], \"attributeArray\": [ 83, 80, 82, 87, 35, 63 ], \"statsArray\": [ 0, 0, 0, 0, 0 ], \"lifetimeStatsArray\": [ 0, 0, 0, 0, 0 ], \"skillmoves\": 3, \"weakfootabilitytypecode\": 4, \"attackingworkrate\": 0, \"defensiveworkrate\": 0, \"preferredfoot\": 1, \"possiblePositions\": [ \"LM\", \"LW\" ] }, \"tradeState\": \"active\", \"buyNowPrice\": 5600, \"currentBid\": 0, \"offers\": 0, \"watched\": false, \"bidState\": \"none\", \"startingBid\": 5500, \"confidenceValue\": 100, \"expires\": 73981, \"sellerName\": \"FIFA UT\", \"sellerEstablished\": 0, \"sellerId\": 0, \"tradeOwner\": false, \"tradeIdStr\": \"507812209327\" }, { \"tradeId\": 507812684218, \"itemData\": { \"id\": 61549677840, \"timestamp\": 1694261062, \"formation\": \"f3412\", \"untradeable\": false, \"assetId\": 183277, \"rating\": 84, \"itemType\": \"player\", \"resourceId\": 183277, \"owners\": 2, \"discardValue\": 1000, \"itemState\": \"forSale\", \"cardsubtypeid\": 3, \"lastSalePrice\": 5000, \"injuryType\": \"none\", \"injuryGames\": 0, \"preferredPosition\": \"LW\", \"contract\": 7, \"teamid\": 243, \"rareflag\": 1, \"playStyle\": 250, \"leagueId\": 53, \"assists\": 0, \"lifetimeAssists\": 0, \"loyaltyBonus\": 0, \"pile\": 5, \"nation\": 7, \"resourceGameYear\": 2023, \"guidAssetId\": \"c295ffd0-2695-40e9-9d70-b806e4679e5d\", \"groups\": [ 4 ], \"attributeArray\": [ 83, 80, 82, 87, 35, 63 ], \"statsArray\": [ 0, 0, 0, 0, 0 ], \"lifetimeStatsArray\": [ 0, 0, 0, 0, 0 ], \"skillmoves\": 3, \"weakfootabilitytypecode\": 4, \"attackingworkrate\": 0, \"defensiveworkrate\": 0, \"preferredfoot\": 1, \"possiblePositions\": [ \"LM\", \"LW\" ] }, \"tradeState\": \"active\", \"buyNowPrice\": 5600, \"currentBid\": 0, \"offers\": 0, \"watched\": false, \"bidState\": \"none\", \"startingBid\": 5500, \"confidenceValue\": 100, \"expires\": 75221, \"sellerName\": \"FIFA UT\", \"sellerEstablished\": 0, \"sellerId\": 0, \"tradeOwner\": false, \"tradeIdStr\": \"507812684218\" }, { \"tradeId\": 507813083168, \"itemData\": { \"id\": 156973921529, \"timestamp\": 1694262091, \"formation\": \"f3412\", \"untradeable\": false, \"assetId\": 183277, \"rating\": 84, \"itemType\": \"player\", \"resourceId\": 183277, \"owners\": 2, \"discardValue\": 1000, \"itemState\": \"forSale\", \"cardsubtypeid\": 3, \"lastSalePrice\": 4500, \"injuryType\": \"none\", \"injuryGames\": 0, \"preferredPosition\": \"LW\", \"contract\": 7, \"teamid\": 243, \"rareflag\": 1, \"playStyle\": 250, \"leagueId\": 53, \"assists\": 0, \"lifetimeAssists\": 0, \"loyaltyBonus\": 0, \"pile\": 5, \"nation\": 7, \"resourceGameYear\": 2023, \"guidAssetId\": \"c295ffd0-2695-40e9-9d70-b806e4679e5d\", \"groups\": [ 4 ], \"attributeArray\": [ 83, 80, 82, 87, 35, 63 ], \"statsArray\": [ 0, 0, 0, 0, 0 ], \"lifetimeStatsArray\": [ 0, 0, 0, 0, 0 ], \"skillmoves\": 3, \"weakfootabilitytypecode\": 4, \"attackingworkrate\": 0, \"defensiveworkrate\": 0, \"preferredfoot\": 1, \"possiblePositions\": [ \"LM\", \"LW\" ] }, \"tradeState\": \"active\", \"buyNowPrice\": 5600, \"currentBid\": 0, \"offers\": 0, \"watched\": false, \"bidState\": \"none\", \"startingBid\": 5500, \"confidenceValue\": 100, \"expires\": 76255, \"sellerName\": \"FIFA UT\", \"sellerEstablished\": 0, \"sellerId\": 0, \"tradeOwner\": false, \"tradeIdStr\": \"507813083168\" }, { \"tradeId\": 507813111399, \"itemData\": { \"id\": 157790369991, \"timestamp\": 1694262169, \"formation\": \"f3412\", \"untradeable\": false, \"assetId\": 183277, \"rating\": 84, \"itemType\": \"player\", \"resourceId\": 183277, \"owners\": 3, \"discardValue\": 1000, \"itemState\": \"forSale\", \"cardsubtypeid\": 3, \"lastSalePrice\": 5000, \"injuryType\": \"none\", \"injuryGames\": 0, \"preferredPosition\": \"LW\", \"contract\": 7, \"teamid\": 243, \"rareflag\": 1, \"playStyle\": 250, \"leagueId\": 53, \"assists\": 0, \"lifetimeAssists\": 0, \"loyaltyBonus\": 0, \"pile\": 5, \"nation\": 7, \"resourceGameYear\": 2023, \"guidAssetId\": \"c295ffd0-2695-40e9-9d70-b806e4679e5d\", \"groups\": [ 4 ], \"attributeArray\": [ 83, 80, 82, 87, 35, 63 ], \"statsArray\": [ 0, 0, 0, 0, 0 ], \"lifetimeStatsArray\": [ 0, 0, 0, 0, 0 ], \"skillmoves\": 3, \"weakfootabilitytypecode\": 4, \"attackingworkrate\": 0, \"defensiveworkrate\": 0, \"preferredfoot\": 1, \"possiblePositions\": [ \"LM\", \"LW\" ] }, \"tradeState\": \"active\", \"buyNowPrice\": 5600, \"currentBid\": 0, \"offers\": 0, \"watched\": false, \"bidState\": \"none\", \"startingBid\": 5500, \"confidenceValue\": 100, \"expires\": 76333, \"sellerName\": \"FIFA UT\", \"sellerEstablished\": 0, \"sellerId\": 0, \"tradeOwner\": false, \"tradeIdStr\": \"507813111399\" }, { \"tradeId\": 507816559896, \"itemData\": { \"id\": 158830464813, \"timestamp\": 1694272018, \"formation\": \"f3412\", \"untradeable\": false, \"assetId\": 183277, \"rating\": 84, \"itemType\": \"player\", \"resourceId\": 183277, \"owners\": 2, \"discardValue\": 1000, \"itemState\": \"forSale\", \"cardsubtypeid\": 3, \"lastSalePrice\": 4900, \"injuryType\": \"none\", \"injuryGames\": 0, \"preferredPosition\": \"LW\", \"contract\": 7, \"teamid\": 243, \"rareflag\": 1, \"playStyle\": 250, \"leagueId\": 53, \"assists\": 0, \"lifetimeAssists\": 0, \"loyaltyBonus\": 0, \"pile\": 5, \"nation\": 7, \"resourceGameYear\": 2023, \"guidAssetId\": \"c295ffd0-2695-40e9-9d70-b806e4679e5d\", \"groups\": [ 4 ], \"attributeArray\": [ 83, 80, 82, 87, 35, 63 ], \"statsArray\": [ 0, 0, 0, 0, 0 ], \"lifetimeStatsArray\": [ 0, 0, 0, 0, 0 ], \"skillmoves\": 3, \"weakfootabilitytypecode\": 4, \"attackingworkrate\": 0, \"defensiveworkrate\": 0, \"preferredfoot\": 1, \"possiblePositions\": [ \"LM\", \"LW\" ] }, \"tradeState\": \"active\", \"buyNowPrice\": 5500, \"currentBid\": 0, \"offers\": 0, \"watched\": false, \"bidState\": \"none\", \"startingBid\": 5400, \"confidenceValue\": 100, \"expires\": 86178, \"sellerName\": \"FIFA UT\", \"sellerEstablished\": 0, \"sellerId\": 0, \"tradeOwner\": false, \"tradeIdStr\": \"507816559896\" }, { \"tradeId\": 507816701416, \"itemData\": { \"id\": 157026568897, \"timestamp\": 1694272218, \"formation\": \"f3412\", \"untradeable\": false, \"assetId\": 183277, \"rating\": 84, \"itemType\": \"player\", \"resourceId\": 183277, \"owners\": 3, \"discardValue\": 1000, \"itemState\": \"forSale\", \"cardsubtypeid\": 3, \"lastSalePrice\": 5200, \"injuryType\": \"none\", \"injuryGames\": 0, \"preferredPosition\": \"LW\", \"contract\": 7, \"teamid\": 243, \"rareflag\": 1, \"playStyle\": 250, \"leagueId\": 53, \"assists\": 0, \"lifetimeAssists\": 0, \"loyaltyBonus\": 0, \"pile\": 5, \"nation\": 7, \"resourceGameYear\": 2023, \"guidAssetId\": \"c295ffd0-2695-40e9-9d70-b806e4679e5d\", \"groups\": [ 4 ], \"attributeArray\": [ 83, 80, 82, 87, 35, 63 ], \"statsArray\": [ 0, 0, 0, 0, 0 ], \"lifetimeStatsArray\": [ 0, 0, 0, 0, 0 ], \"skillmoves\": 3, \"weakfootabilitytypecode\": 4, \"attackingworkrate\": 0, \"defensiveworkrate\": 0, \"preferredfoot\": 1, \"possiblePositions\": [ \"LM\", \"LW\" ] }, \"tradeState\": \"active\", \"buyNowPrice\": 5500, \"currentBid\": 0, \"offers\": 0, \"watched\": false, \"bidState\": \"none\", \"startingBid\": 5400, \"confidenceValue\": 100, \"expires\": 86377, \"sellerName\": \"FIFA UT\", \"sellerEstablished\": 0, \"sellerId\": 0, \"tradeOwner\": false, \"tradeIdStr\": \"507816701416\" } ], \"bidTokens\": {} }";
        List<AuctionInfoItem> auctionInfos = JsonParser.parseFromString(response, TransfermarketSearchResponse.class).getAuctionInfo();

        AuctionInfoHelper.getLowestBinPriceFromAllAuctionInfo(auctionInfos);
    }
}
