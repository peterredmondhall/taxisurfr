package com.taxisurfr.server;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.taxisurfr.shared.model.BookingInfo;
import com.stripe.Stripe;
import com.stripe.model.Charge;

public class StripePayment
{
    private static final Logger logger = Logger.getLogger(StripePayment.class.getName());

    public String charge(String card, BookingInfo bookingInfo, String stripeSecret)
    {
        String error = null;
        try
        {
            Stripe.apiKey = stripeSecret;

            Map<String, Object> chargeParams = new HashMap<String, Object>();
            int cents = bookingInfo.getPaidPrice() * 100;
            chargeParams.put("amount", cents);
            chargeParams.put("currency", bookingInfo.getCurrency().name().toLowerCase());
            chargeParams.put("card", card); // obtained with Stripe.js
            chargeParams.put("description", "Taxi Charges Sri Lanka - order " + bookingInfo.getOrderRef());

            logger.info("charging cents " + bookingInfo.getCurrency().name().toLowerCase() + cents);
            Charge charge = Charge.create(chargeParams);
            logger.info("charging successful");
            return error;
        }

        catch (Exception exception)
        {
            error = exception.getMessage();
            logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
        return error;
    }
}
