package com.petroandrushchak.fut.helper;

import com.petroandrushchak.fut.model.SellPrices;
import com.petroandrushchak.fut.model.TransferMarketPrices;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FUTPriceHelper {

    private static final double EA_SELL_TAX = 0.05;


    private static final int MIN_MIN_BID_PRICE = 150;
    private static final int MAX_MIN_BID_PRICE = 500;
    private static final int MIN_MIN_BUY_NOW_PRICE = 200;
    private static final int MAX_MIN_BUY_NOW_PRICE = 500;

    private static final int PRICE_STEP = 50;

    public static TransferMarketPrices createSearchPricesWithMaxBuyNowPrice(long maxBuyNowPrice) {
        TransferMarketPrices prices = createSearchPrices();
        prices.setMaxBuyNowPrice(maxBuyNowPrice);
        return prices;
    }

    public static long createPriceForSnippingFromSellPrice(long sellPrice) {
        double tax = sellPrice * EA_SELL_TAX;
        long priceAfterTax = (long) (sellPrice - tax - getIncomePrice(sellPrice));
        return getRoundedPrice(priceAfterTax);
    }

    public static long getIncomeForPriceAfterTax(long priceAfterTax) {
        double tax = priceAfterTax * EA_SELL_TAX;
        return (long) (priceAfterTax - tax);
    }

    public static SellPrices createSellPrices(long buyNowPrice) {
        long startBidPrice = getReducedPrice(buyNowPrice);
        return new SellPrices(startBidPrice, buyNowPrice);
    }

    public static TransferMarketPrices createSearchPrices() {
        TransferMarketPrices prices = TransferMarketPrices.emptyPrices();
        prices.setMinBidPrice(MAX_MIN_BID_PRICE);
        prices.setMinBuyNowPrice(MAX_MIN_BUY_NOW_PRICE);
        return prices;
    }

    public static TransferMarketPrices updatePrices(TransferMarketPrices transferMarketPrices) {
        return updatePrices(transferMarketPrices, transferMarketPrices.getMaxBuyNowPrice());
    }

    public static TransferMarketPrices updatePrices(TransferMarketPrices transferMarketPrices, long moxBuyNowPrice) {
        TransferMarketPrices newPrices = TransferMarketPrices.emptyPrices();
        newPrices.setMaxBuyNowPrice(moxBuyNowPrice);
        newPrices.setMaxBidPrice(transferMarketPrices.getMaxBidPrice());

        if (minBidPriceCanBeDecreased(transferMarketPrices.getMinBidPrice())) {
            newPrices.setMinBidPrice(decreaseMinPrice(transferMarketPrices.getMinBidPrice()));
            newPrices.setMinBuyNowPrice(transferMarketPrices.getMinBuyNowPrice());
            return newPrices;
        } else {
            newPrices.setMinBidPrice(MAX_MIN_BID_PRICE);
        }

        if (minBuyNowPriceCanBeDecreased(transferMarketPrices.getMinBuyNowPrice())) {
            newPrices.setMinBuyNowPrice(decreaseMinPrice(transferMarketPrices.getMinBuyNowPrice()));
            newPrices.setMinBidPrice(transferMarketPrices.getMinBidPrice());
            return newPrices;
        } else {
            newPrices.setMinBuyNowPrice(MAX_MIN_BUY_NOW_PRICE);
        }

        return newPrices;

    }

    private static boolean minBidPriceCanBeDecreased(long minBidPrice) {
        return minBidPrice - PRICE_STEP >= MIN_MIN_BID_PRICE;
    }

    private static boolean minBuyNowPriceCanBeDecreased(long minBuyNowPrice) {
        return minBuyNowPrice - PRICE_STEP >= MIN_MIN_BUY_NOW_PRICE;
    }

    private static long decreaseMinPrice(long minPrice) {
        return minPrice - PRICE_STEP;
    }

    public static long getIncreasedPrice(long price) {
        return price + getStepAmount(price);
    }

    public static long getIncreasedSignificantlyPrice(long price) {
        return price + getSignificantStepAmount(price);
    }

    public static long getReducedSignificantlyPrice(long price) {
        return price - getSignificantStepAmount(price);
    }

    public static long getReducedPrice(long price) {
        return price - getStepAmount(price);
    }

    private static long getRoundedPrice(long price) {
        long step = getStepAmount(price);
        long division = (price % step);
        long newPrice;
        if (division == 0) {
            newPrice = price;
        } else if ((step / 2) >= division) {
            newPrice = price - division;
        } else {
            newPrice = step - division + price;
        }
        return newPrice;
    }

    private static long getStepAmount(long price) {
        if (price < 1_000_000 && price >= 100_000) {
            return 1000;
        } else if (price < 100000 && price >= 50000) {
            return 250;
        } else if (price < 50000 && price >= 10000) {
            return 250;
        } else if (price < 10000 && price > 1000) {
            return 100;
        } else {
            return 50;
        }
    }

    private static long getSignificantStepAmount(long price) {
        if (price <= 1_000_000 && price > 100_000) {
            return 50_000;
        } else if (price <= 100000 && price > 50000) {
            return 5000;
        } else if (price <= 50000 && price > 10000) {
            return 2500;
        } else if (price <= 10000 && price > 1000) {
            return 1000;
        } else {
            return 50;
        }
    }

    //TODO Make it as a Config
    private static long getIncomePrice(long price) {
        if (price <= 10000000 && price > 500000) {
            return 5000;
        } else if (price <= 500000 && price > 100000) {
            return 2000;
        } else if (price <= 100000 && price > 50000) {
            return 500;
        } else if (price <= 50000 && price > 20000) {
            return 250;
        } else if (price <= 20000 && price > 5000) {
            return 150;
        } else if (price <= 5000 && price > 2000) {
            return 75;
        } else if (price <= 2000 && price > 1000) {
            return 100;
        } else if (price <= 1000 && price > 500) {
            return 50;
        } else {
            return 50;
        }
    }

    public static long removeCommaFromStringPrice(String price) {
        return Long.parseLong(price.replace(",", ""));
    }


}
