package com.petroandrushchak.model.domain;

import com.petroandrushchak.fut.helper.FUTPriceHelper;
import com.petroandrushchak.model.fut.Item;
import com.petroandrushchak.model.fut.PlayerItem;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static com.petroandrushchak.fut.helper.FUTPriceHelper.createSellPrices;

@Getter
@Setter
@Builder(builderMethodName = "anSnippingModel", toBuilder = true, setterPrefix = "with")
public class SnippingModel {

    Item itemsSearch;

    TransferMarketPrices searchPrices;
    SellPrices sellPrices;

    SnippingResult snippingResult;

    public static SnippingModel playerModel(String playerName, int playerRating, long sellPrice) {
        PlayerItem playerItem = new PlayerItem();
        playerItem.setPlayerName(playerName);
        playerItem.setRating(playerRating);
        var searchPrices = FUTPriceHelper.createSearchPricesWithMaxBuyNowPrice(FUTPriceHelper.createPriceForSnippingFromSellPrice(sellPrice));

        return anSnippingModel()
                .withItemsSearch(playerItem)
                .withSearchPrices(searchPrices)
                .withSellPrices(createSellPrices(sellPrice))
                .build();

    }

    public void updateSearchPrices() {
        this.searchPrices = FUTPriceHelper.updateMinBidAndMinBuySearchPrices(searchPrices);
    }

    @Override
    public String toString() {
        return "ItemsSearch:" + itemsSearch +
                ", \n searchPrices = " + searchPrices +
                ", \n sellPrices = " + sellPrices +
                '}';
    }

    public SnippingResult snippingResult() {
        return snippingResult;
    }
}
