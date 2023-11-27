package com.petroandrushchak.service.helpers;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FutApiHelper {

    public static Long getTradeIdFromBuyItemRequestUrl(String url) {
        //URL https://utas.mob.v2.fut.ea.com/ut/game/fc24/trade/511039469014/bid
        var tradeId = url.split("/trade/")[1].split("/bid")[0];
        return Long.valueOf(tradeId);
    }
}
