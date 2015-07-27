package com.taxisurfr.shared;

public enum Currency
{
    EUR("EUR €"),
    USD("$US"),
    AUD("$AU"),
    GBP("GBP £");
    public String symbol;

    Currency(String sym)
    {
        symbol = sym;
    }
}
