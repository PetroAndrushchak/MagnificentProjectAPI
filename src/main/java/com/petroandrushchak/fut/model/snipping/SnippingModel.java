package com.petroandrushchak.fut.model.snipping;

import com.petroandrushchak.fut.helper.FUTPriceHelper;
import com.petroandrushchak.fut.model.SellPrices;
import com.petroandrushchak.fut.model.TransferMarketPrices;
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
        this.searchPrices = FUTPriceHelper.updatePrices(searchPrices);
    }

    @Override
    public String toString() {
        return "ItemsSearch:" + itemsSearch +
                ", \n searchPrices = " + searchPrices +
                ", \n sellPrices = " + sellPrices +
                '}';
    }
}
