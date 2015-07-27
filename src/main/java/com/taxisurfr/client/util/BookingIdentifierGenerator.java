package com.taxisurfr.client.util;

import java.math.BigInteger;
import java.security.SecureRandom;

public final class BookingIdentifierGenerator
{
    private static final SecureRandom random = new SecureRandom();

    public static String nextBookingId()
    {
        return new BigInteger(130, random).toString(6);
    }
}