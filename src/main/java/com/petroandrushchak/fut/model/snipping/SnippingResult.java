package com.petroandrushchak.fut.model.snipping;

import com.petroandrushchak.fut.api.AuctionInfoItem;
import com.petroandrushchak.fut.helper.FUTPriceHelper;
import com.petroandrushchak.fut.model.SellPrices;
import com.petroandrushchak.fut.model.TransferMarketPrices;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static com.petroandrushchak.fut.helper.FUTPriceHelper.getIncomeForPriceAfterTax;

public class SnippingResult {

    @Getter
    List<OneSnippingResult> snippingResults = new ArrayList<>();

    public void addOneSnippingResult(OneSnippingResult oneSnippingResult) {
        snippingResults.add(oneSnippingResult);
    }

    public long getPossibleProfit() {
        return snippingResults.stream()
                              .filter(OneSnippingResult::isItemBought)
                              .map(snippingResult -> {
                                  var buyNowPrice = snippingResult.getBoughtItem().getBuyNowPrice();
                                  var sellPrice = snippingResult.getSellPrices().getBuyNowPrice();
                                  return sellPrice - buyNowPrice;
                              })
                              .map(FUTPriceHelper::getIncomeForPriceAfterTax)
                              .reduce(0L, Long::sum);
    }

    @Setter
    @Getter
    public static class OneSnippingResult {

        TransferMarketPrices searchPrices;
        SellPrices sellPrices;

        List<AuctionInfoItem> foundItems;
        AuctionInfoItem boughtItem;

        public boolean isItemBought() {
            return boughtItem != null;
        }

    }

    public static OneSnippingResult createOneSnippingResult() {
        return new OneSnippingResult();
    }

}
