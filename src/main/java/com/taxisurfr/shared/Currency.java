package com.taxisurfr.shared;

import java.io.Serializable;

public enum Currency implements Serializable
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
