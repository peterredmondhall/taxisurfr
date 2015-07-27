package com.taxisurfr.shared;

import com.taxisurfr.shared.model.RouteInfo;

public class CurrencyHelper
{

    public static int getPriceInDollars(RouteInfo routeInfo, Currency currency, Float rate)
    {
        if (routeInfo.getCents() != null)
        {
            int d = getPrice(routeInfo.getCents(), currency, rate);
            return d;
        }
        return 0;

    }

    public static String getPrice(RouteInfo routeInfo, Currency currency, Float rate)
    {
        return currency.symbol + getPriceInDollars(routeInfo, currency, rate);

    }

    private static int getPrice(long usCents, Currency currency, Float rate)
    {
        if (Currency.USD.equals(currency))
        {
            return (int) (usCents / 100);
        }
        else
        {
            float price = (usCents * rate) / 100;
            int priceInt = (int) (price + 5);
            return priceInt;

        }
    }

}
