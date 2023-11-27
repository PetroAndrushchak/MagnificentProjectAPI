package com.petroandrushchak.service.fut;

import com.petroandrushchak.helper.Waiter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class FutApiService {

    org.openqa.selenium.devtools.v113.network.model.Request buyRequest;
    String buyUrl;

    String sellItemUrl = "https://utas.mob.v2.fut.ea.com/ut/game/fc24/item";
    String sellAuctionHouseUrl = "https://utas.mob.v2.fut.ea.com/ut/game/fc24/auctionhouse";

    public void saveBuyItemRequestInfo(org.openqa.selenium.devtools.v113.network.model.Request request) {
        this.buyRequest = request;
        this.buyUrl = request.getUrl();
    }

    @SneakyThrows
    public boolean buyItem(String tradeId, long bidPrice) {

        OkHttpClient client = new OkHttpClient();

        AtomicReference<Headers.Builder> headersBuilder = new AtomicReference<>(new Headers.Builder());
        buyRequest.getHeaders().forEach((key, value) -> {
            var updateHeaderBuilder = headersBuilder.get();
            updateHeaderBuilder.add(key, value.toString());
            headersBuilder.set(updateHeaderBuilder);
        });

        var headers = headersBuilder.get().build();

        var newUrl = getBuyUlrWithChangedTradeId(tradeId);

        Request request = new Request.Builder()
                .url(newUrl)
                .headers(headers)
                .method("PUT", RequestBody.create(MediaType.parse("application/json"), "{\"bid\":" + bidPrice + "}"))
                .build();

        log.info("Buying item with tradeId: " + tradeId + ", bidPrice: " + bidPrice);
        Response response = client.newCall(request).execute();

        if (response.code() == 200) {
            log.info("Item is bought for " + bidPrice);
            return true;
        } else if (response.code() == 461 || response.code() == 478) {
            log.info("Item was not bought, response code: " + response.code());
            return false;
        } else {
            log.error("Response code: " + response.code() + ", response body: ");
            try (ResponseBody body = response.body()) {
                if (body != null) {
                    log.error("Response Body is: ");
                    log.error(body.string());
                }
            }
            throw new RuntimeException("Unexpected response code: " + response.code());
        }
    }

    private String getBuyUlrWithChangedTradeId(String tradeId) {
        String regexPattern = "/trade/\\d+/";
        String updatedURL = buyUrl.replaceAll(regexPattern, "/trade/" + tradeId + "/");
        log.info("Updated URL: " + updatedURL);
        return updatedURL;
    }

    @SneakyThrows
    public boolean sellItem(String tradeId) {
        Waiter.waitRandomTimeFromTwoToThreeMinutes();
        log.info("Selling item with tradeId: " + tradeId);
        OkHttpClient client = new OkHttpClient();

        AtomicReference<Headers.Builder> headersBuilder = new AtomicReference<>(new Headers.Builder());
        buyRequest.getHeaders().forEach((key, value) -> {
            var updateHeaderBuilder = headersBuilder.get();
            updateHeaderBuilder.add(key, value.toString());
            headersBuilder.set(updateHeaderBuilder);
        });

        var headers = headersBuilder.get().build();

        Request request = new Request.Builder()
                .url(sellItemUrl)
                .headers(headers)
                .method("PUT", RequestBody.create(MediaType.parse("application/json"), "{\"itemData\":[{\"id\":" + tradeId + ",\"pile\":\"trade\"}]}\n"))
                .build();

        Response response = client.newCall(request).execute();

        if (response.code() == 200) {
            log.info("Item was sell successfully");
            return true;
        } else {
            log.info("Item was sell bought, response code: " + response.code() + ", response body: " + response.body().string());
            return false;
        }
    }

    @SneakyThrows
    public boolean sellItemInAuctionHouse(String tradeId, long buyNowPrice, long startingBid) {
        log.info("Selling item with tradeId: " + tradeId + ", buyNowPrice: " + buyNowPrice + ", startingBid: " + startingBid);
        OkHttpClient client = new OkHttpClient();

        AtomicReference<Headers.Builder> headersBuilder = new AtomicReference<>(new Headers.Builder());
        buyRequest.getHeaders().forEach((key, value) -> {
            var updateHeaderBuilder = headersBuilder.get();
            updateHeaderBuilder.add(key, value.toString());
            headersBuilder.set(updateHeaderBuilder);
        });

        var headers = headersBuilder.get().build();

        Request request = new Request.Builder()
                .url(sellAuctionHouseUrl)
                .headers(headers)
                .method("POST", RequestBody.create(MediaType.parse("application/json"),
                        "{\"buyNowPrice\":" + buyNowPrice + ",\"duration\":3600,\"itemData\":{\"id\":" + tradeId + "},\"startingBid\":" + startingBid + "}"))
                .build();

        Response response = client.newCall(request).execute();

        if (response.code() == 200) {
            log.info("Item was sell successfully");
            return true;
        } else {
            log.info("Item was sell bought, response code: " + response.code() + ", response body: " + response.body().string());
            return false;
        }
    }

}
