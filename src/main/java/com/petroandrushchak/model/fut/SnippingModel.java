package com.petroandrushchak.model.fut;

import com.petroandrushchak.fut.helper.FUTPriceHelper;
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

    public static SnippingModel playerModel(String playerName, int playerRating, long sellPrice) {
        PlayerItem playerItem = new PlayerItem();
        playerItem.setName(playerName);
        playerItem.setRating(String.valueOf(playerRating));
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
}
